from ultralytics import YOLO
from sort import Sort
from sort import KalmanBoxTracker
from datetime import datetime, timezone
import cv2
import sys
import numpy as np
import math
import toml
import logging

# Temporary storage of data since the storage DNE
events = []

def send_to_sever(value, group_name):
    """
    The send_to_server method sends an increment or decrement to the volume of this area.
    Args:
        value: Either 1 or -1 to increment or decrement.
        group_name: The group name this agent is apart of.
    """
    capture_time = datetime.now(timezone.utc)
    data = {
        "captured_at": str(capture_time),
        "value": value,
        "group": group_name
    }
    events.append(data)

class Camera:
    def __init__(self, name, width=640, height=640):
        """
        The Camera class is used to gather camera data from the system's default camera.

        Args:
            name: The name of the camera window.
            width: The width of the camera window.
            height: The height of the camera window.
        """
        self.cap = cv2.VideoCapture(0)
        if not self.cap.isOpened():
            print("Error: Could not open Camera", file=sys.stderr)
            exit(1)

        self.name = name
        self.cap.set(cv2.CAP_PROP_FRAME_WIDTH, width)
        self.cap.set(cv2.CAP_PROP_FRAME_HEIGHT, height)
        
    def get_frame(self):
        """
        The get_frame method returns the a frame from the camera.
        """
        return self.cap.read()
    
    def draw_vertical_line(self, frame, line_color, starting_point, ending_point):
        """
        The draw_vertical_line method draws a line on a given frame.
        Args:
            frame: The frame to draw on.
            line_color: The color to paint the line.
            starting_point: The first point of the line (x1, y1).
            ending_point: The last point of the line (x2, y2).
        """
        cv2.line(frame, starting_point, ending_point, line_color, 5)

    def display_camera_frame(self, frame):
        """
        The display_camera_frame shows a given camera frame to a window.
        Args:
            frame: The frame to show to the screen.
        """
        cv2.imshow(self.name, frame)

    def shutdown(self):
        """
        The shutdown method is used to clean up resources used by the camera.
        """
        self.cap.release()
        cv2.destroyAllWindows()
    
class ObjectTracker:
    def __init__(self, keep_alive_frames, min_hits, iou_threshold, group_name):
        """
        The ObjectTracker class handles objects based on detections from the ML model.
        Args:
            keep_alive_frames: The amount of frames to go without seeing an object before considering it to be gone.
            min_hits: The amount of miniumn to see a specifc object before considering it to exist.
            iou_threshold: The percentage of difference between objects before consider it to be a new object.
            group_name: The name of the group this agent is apart of.
        """
        self.tracker = Sort(max_age=keep_alive_frames, min_hits=min_hits, iou_threshold=iou_threshold)
        self.line_a_objs = {}
        self.line_b_objs = {}
        self.volume = 0
        self.keep_alive_frames = keep_alive_frames
        self.group_name = group_name

    def update_objs_in_line_storage(self, objects):
        """
        The update_objs_in_line_storage updates objects based on the line they intersect with on the camera.
        If an object moves from one line to the next, an intercemnt happens. Vise versa for the other direction.
        No operation occurs when an object intersects with both lines.
        Args:
            objects: The current list of seen objects
        """
        for track_id, line in objects:
            if line == 'A':
                if track_id in self.line_b_objs:
                    # Send Data: Entering
                    send_to_sever(1, self.group_name)
                    self.line_b_objs.pop(track_id)
                    pass
                else:
                    self.line_a_objs[track_id] = self.keep_alive_frames
            elif line == 'B':
                if track_id in self.line_a_objs:
                    # Send Data: Leaving
                    send_to_sever(-1, self.group_name)
                    self.line_a_objs.pop(track_id)
                else:
                    self.line_b_objs[track_id] = self.keep_alive_frames

    def adjust_alive_time_line_sets(self, tracked_objects):
        """
        The adjust_alive_time_line_sets updates the objects in memory to remove old entires of objects that no longer exist.
        Object that have not been seen for more than the keep_alive_frames are discarded.
        Args:
            objects: The current list of seen objects
        """
        for _, _, _, _, track_id in tracked_objects:
            if track_id in self.line_a_objs:
                self.line_a_objs[track_id] = self.keep_alive_frames
            if track_id in self.line_b_objs:
                self.line_b_objs[track_id] = self.keep_alive_frames

        line_a_objs_keys = self.line_a_objs.copy().keys()
        line_b_objs_keys = self.line_b_objs.copy().keys()

        for track_id in line_a_objs_keys:
            self.line_a_objs[track_id] -= 1
            if self.line_a_objs[track_id] <= 0:
                self.line_a_objs.pop(track_id)

        for track_id in line_b_objs_keys:
            self.line_b_objs[track_id] -= 1
            if self.line_b_objs[track_id] <= 0:
                self.line_b_objs.pop(track_id)

        # Reset track ids to prevent memory leak
        if KalmanBoxTracker.count > 100 and (not self.line_a_objs and not self.line_b_objs):
            KalmanBoxTracker.count = 0

class VolumeAgent:
    def __init__(self, objects, confidence, model_path, keep_alive_frames, min_hits, iou_threshold, line_gap, line_start, group_name, mode="production", exit_key='q'):
        """
        The VolumeAgent class keeps track of the list of objects it has seen an agggreates the result.
        Args:
            objects: The list of object names to look out for.
            confidence: If object detection is below the confidence score, the object is discarded.
            model_path: The path to the YOLO ML model.
            keep_alive_frames: The amount of frames to go without seeing an object before considering it to be gone.
            min_hits: The amount of miniumn to see a specifc object before considering it to exist.
            iou_threshold: The percentage of difference between objects before consider it to be a new object.
            line_start: The pixel to start the first line.
            line_gap: The distance between both lines on the camera.
            group_name: The group name this agent is apart of.
            mode: The current mode of the VolumeAgent.
            exit_key: The key to press to exit the camera debugger
        """
        self.objects = objects
        self.confidence = confidence
        self.camera = Camera(name="Volume Agent")
        self.model = YOLO(model_path)
        self.obj_tracker = ObjectTracker(keep_alive_frames, min_hits, iou_threshold, group_name)
        self.line_gap = line_gap
        self.line_start = line_start
        self.mode = mode
        if not exit_key.isalpha():
            print(f"Error: exit key is not valid. Expected alpha numeric key, got {exit_key}", file=sys.stderr)
            exit(1)
        self.exit_key = exit_key

        format_string = "%(asctime)s [%(levelname)s] %(name)s: %(message)s"
        logging.basicConfig(
            level=logging.INFO,
            format=format_string,
            handlers=[logging.StreamHandler()]
        )
        self.logger = logging.getLogger("volume-agent")

        if not mode == "production":
            self.logger.info(f"Volume Agent creatd with YOLO model {model_path} and confidence level {self.confidence}. Looking out for {self.objects}")

    def _draw_debug_lines(self, frame, line_color=(0, 0, 255)):
        """
        The _draw_debug_lines is a private method that draws the lines objects intersect with onto the camera.
        Args:
            frame: The frame to draw the lines on.
            line_color: The color of the lines to be.
        """
        height, width = frame.shape[:2]
        val = self.line_start + self.line_gap
        if width < val:
            self.logger.error(f"Error: Location of ending line out of bounds from frame. {val} > {width} (Max Camera Width)")
            exit(1)
        self.camera.draw_vertical_line(frame, line_color, (self.line_start, 0), (self.line_start, height))
        self.camera.draw_vertical_line(frame, line_color, (val, 0), (val, height))

    def _gather_detections(self, detection_results):
        """
        The _gather_detections method is a private method use to gather the number of objects found in a specifc frame.
        Args:
            detection_results: The raw results from the ML model. This may contain objects we do not care for.
        Returns:
            An array of objects that fit within the confidence score and object list.
        """
        detections = []
        for result in detection_results:
            for box in result.boxes:
                x1, y1, x2, y2 = map(int, box.xyxy[0])
                confidence = math.ceil((box.conf[0] * 100)) / 100
                class_id = int(box.cls[0])
                class_name = self.model.names[class_id]
                if class_name in self.objects and confidence > self.confidence:
                    if not self.mode == "production":
                        self.logger.info(f"Detected {class_name} at ({x1},{y1}) ({x2},{y2}) with confidence {confidence}")
                    detections.append([x1, y1, x2, y2, confidence])

        return detections

    def _update_tracked_items(self, detections):
        """
        The _update_tracked_items is a private method that updates the list of objects we are storing. 
        Args:
            The list of objects that were detected.
        """
        if len(detections) > 0:
            detections_np = np.array(detections)
        else:
            detections_np = np.empty((0, 5))

        return self.obj_tracker.tracker.update(detections_np)

    def _draw_debug_data(self, frame, tracked_objects):
        """
        The _draw_debug_data is a private method that draws the camera to the screen along with debug information.
        Args:
            frame: The frame to draw the debug data to.
            tracked_objects: The objects to add debug data to on the screen.
        """
        self._draw_debug_lines(frame)
        for x1, y1, x2, y2, track_id in tracked_objects:
            track_id = int(track_id)
            cv2.rectangle(frame, (int(x1), int(y1)), (int(x2), int(y2)), (0, 255, 0), 2)
            cv2.putText(frame, f"track_id: {track_id}", (int(x1), int(y1) - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.6, (0, 255, 0), 2)

        self.camera.display_camera_frame(frame)
    
    def _is_intersecting_with_line(self, obj_x1y1, obj_x2y2, line_x1y1):
        """
        Determines if an object is intersection with a given line's x coordinate.
        Args:
            obj_x1y1: A tuple of the first x and y coordiante of the object.
            obj_x2y2: A tuple of the second x and y coordiante of the object.
            line_x1y1: A tuple of the first x andy coordinate of the object.
        Returns:
            If the object intersects, this returns true. Otherwise, false.
        """
        obj_x1, _ = obj_x1y1
        obj_x2, _ = obj_x2y2
        line_x, _= line_x1y1
        return obj_x1 <= line_x <= obj_x2
    
    def _get_intersecting_objects(self, tracked_objects):
        """
        The _get_intersecting_objects method takes the tracked objects and returns only the ones intersecting with only one line.
        Args:
            tracked_objects: The objects to check for intersection.
        Returns:
            A list containg the id of the object along with the line it is intersecting with (either A or B)
        """
        output = []
        line_A_x1y1 = (self.line_start, 0)
        line_B_x1y1 = (self.line_start + self.line_gap, 0)
        for x1, y1, x2, y2, track_id in tracked_objects:
            track_id = int(track_id)
            x1, y1, x2, y2 = int(x1), int(y1), int(x2), int(y2)
            if self._is_intersecting_with_line((x1, y1), (x2, y2), line_A_x1y1):
                if not self._is_intersecting_with_line((x1, y1), (x2, y2), line_B_x1y1):
                    output.append((track_id, 'A'))
            elif self._is_intersecting_with_line((x1, y1), (x2, y2), line_B_x1y1):
                if not self._is_intersecting_with_line((x1, y1), (x2, y2), line_A_x1y1):
                    output.append((track_id, 'B'))
        
        return output

    def run(self):
        """
        The run method executes the volume agent into a long running process to keep track of the volume of object it
        sees pass over 2 lines.
        """
        while True:
            success, frame = self.camera.get_frame()
            if not success:
                self.logger.error("Error: Failed to get frame", file=sys.stderr)
                break

            if cv2.waitKey(1) & 0xFF == ord(self.exit_key):
                break

            detection_results = self.model(frame, stream=True)
            detections = self._gather_detections(detection_results)
            tracked_objects = self._update_tracked_items(detections)
            self.obj_tracker.adjust_alive_time_line_sets(tracked_objects)
            intersecting_objects = self._get_intersecting_objects(tracked_objects)
            self.obj_tracker.update_objs_in_line_storage(intersecting_objects)
            if not self.mode == "production":
                self._draw_debug_data(frame, tracked_objects)
                self.logger.info(f"Current Object Volume {self.obj_tracker.volume}")
                self.logger.info(f"Objects who have crossed line A {self.obj_tracker.line_a_objs}")
                self.logger.info(f"Objects who have crossed line B {self.obj_tracker.line_b_objs}")
                self.logger.info(f"Events: {events}")
        self.camera.shutdown()

if __name__ == "__main__":

    # Make YOLO quiet
    logging.getLogger("ultralytics").setLevel(logging.ERROR)
    
    config = toml.load("config.toml")
    agent = VolumeAgent(
        objects=config["valid_objects"],
        confidence=float(config["confidence_percentage"]),
        model_path=config["model_path"],
        keep_alive_frames=int(config["keep_alive_frames"]),
        min_hits=int(config["minuiumn_frames_before_detecion"]),
        iou_threshold=float(config["iou_threshold"]),
        line_start=int(config["line_start"]),
        line_gap=int(config["line_gap"]),
        group_name=config["group"],
        mode=config["mode"],
    )
    agent.run()