from ultralytics import YOLO
from sort import Sort
from sort import KalmanBoxTracker
from datetime import datetime, timezone
from enum import Enum
import cv2
import sys
import numpy as np
import math
import toml
import logging
import requests
import httpx
import asyncio
import threading


"""Defined Directions the objects can take. These values will be sent to the server for event capturing"""
class Direction(Enum):
    ENTERING = 1
    EXITING = -1

"""All states the object can be in at any time"""
class ObjectState(Enum):
    BEFORE_LINE_A = 0
    ON_LINE_A = 1
    BETWEEN_LINES = 2
    ON_LINE_B = 3
    AFTER_LINE_B = 4

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
            sys.exit(1)

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

class TrackedObject:

    OBJECT_PATH_LENGTH = 3

    def __init__(self, id, x1, y1, x2, y2, alive_frames, line_a_pos, line_b_pos, line_tolerance, mode):
        """
        The TrackedObject Class represents an object currently being tracked by the ObjectTracker class.
        Args:
            id: The ID of this object
            x1: The first x coordinate of the object
            y1: The first y coordinate of the object
            x2: The second x coordinate of the object
            y2: The second y coordinate of the object
            alive_frames: The amount of frames the object tracker will wait before removing the object from memory
            line_a_pos: The pixel position of line A
            line_b_pos: The pixel position of line B
            line_tolerance: The amount of pixels to tolerate between lines to cosider the object touching the line
            mode: The mode of the volume agent
        """
        self.id = id
        self.mode = mode
        self.logger = logging.getLogger("tracked-object")
        self.set_coordinates(x1, y1, x2, y2)
        self.set_center()
        self.set_alive_frames(alive_frames)
        self.state = None
        self.counted = False
        self.object_path = []
        self.state = self.set_tracked_object_state(line_a_pos, line_b_pos, line_tolerance)

    def set_coordinates(self, x1, y1, x2, y2):
        """
        The set_coordiantes methods set the x and y values of this object. It also sets the center.
        Args:
            x1: The first x coordinate of the object
            y1: The first y coordinate of the object
            x2: The second x coordinate of the object
            y2: The second y coordinate of the object
        """
        self.x1 = x1
        self.x2 = x2
        self.y1 = y1
        self.y2 = y2
        self.set_center()

    def set_center(self):
        """
        The set_center method sets the center x coordinate of this object
        """
        self.center = (self.x1 + self.x2) / 2

    def set_alive_frames(self, alive_frames):
        """
        The set_alive_framer method sets the frames instance variable this object
        """
        self.frames = alive_frames

    def set_tracked_object_state(self, line_a_pos, line_b_pos, line_tolerance):
        """
        The set_tracked_object_state method sets the state of the object, updating it if need be.
        Args:
            line_a_pos: The pixel position of line A
            line_b_pos: The pixel position of line B
            line_tolerance: The amount of pixels to tolerate between lines to cosider the object touching the line
        Returns:
            The current state of the object
        """
        prev_state = self.state
        if self.center < line_a_pos - line_tolerance:
            state = ObjectState.BEFORE_LINE_A
        elif abs(self.center - line_a_pos) <= line_tolerance:
            state = ObjectState.ON_LINE_A
        elif line_a_pos + line_tolerance < self.center < line_b_pos - line_tolerance:
            state = ObjectState.BETWEEN_LINES
        elif abs(self.center - line_b_pos) <= line_tolerance:
            state = ObjectState.ON_LINE_B
        else:
            state = ObjectState.AFTER_LINE_B

        if not prev_state == state:
            self.update_object_path(state)
            self.state = state
            if not self.mode == "production":
                self.logger.info(f"Object with Track_ID: {self.id} made a state change from {prev_state} to {state}")

        return self.state
    
    def update_object_path(self, new_state):
        """
        The update_object_path method updates the current path of the object, 
        ensuring it stays the length of OBJECT_PATH_LENGTH
        """

        if len(self.object_path) < self.OBJECT_PATH_LENGTH:
            self.object_path.append(new_state)
        else:
            self.object_path.pop(0)
            self.object_path.append(new_state)
    
    def __str__(self):
        return f"TrackedObject[{self.id}][{self.state}][({self.x1}, {self.y1}) ({self.x2}, {self.y2})][AliveTime: {self.frames}][Path: {self.object_path}]"
    
    def set_counted(self, value):
        """
        Sets this object has a counted object.
        """
        self.counted = value

    def was_counted(self):
        """
        Determines if this object has been counted.
        """
        return self.counted
               
class ObjectTracker:

    LINE_TOLERANCE = 10
    RIGHT_TO_LEFT = [ObjectState.ON_LINE_B, ObjectState.BETWEEN_LINES, ObjectState.ON_LINE_A]
    LEFT_TO_RIGHT = [ObjectState.ON_LINE_A, ObjectState.BETWEEN_LINES, ObjectState.ON_LINE_B]

    def __init__(self, keep_alive_frames, min_hits, iou_threshold, group_name, lot_id, server, line_start, line_gap, entrance_side, exit_side, mode):
        """
        The ObjectTracker class handles objects based on detections from the ML model.
        Args:
            keep_alive_frames: The amount of frames to go without seeing an object before considering it to be gone.
            min_hits: The amount of miniumn to see a specifc object before considering it to exist.
            iou_threshold: The percentage of difference between objects before consider it to be a new object.
            group_name: The name of the group this agent is apart of.
            lot_id: The lot this agent is apart of.
            line_start: The beginning pixel of the first line.
            line_gap: How far apart the lines are from each other in pixels.
            entrance_side: The side of the camera that is the entrance of the lot (From the Camera's POV).
            exit_side: The side of the camera that is the exit of the lot (From the Camera's POV).
            mode: The current mode of the volume agent.
        """
        self.tracker = Sort(max_age=keep_alive_frames, min_hits=min_hits, iou_threshold=iou_threshold)
        self.object_history = {}
        self.volume = 0
        self.keep_alive_frames = keep_alive_frames
        self.group_name = group_name
        self.lot_id = lot_id
        self.server = server
        self.line_a_pos = line_start
        self.line_b_pos = line_start + line_gap
        self.mode = mode
        self.entrance_side = entrance_side
        self.exit_side = exit_side
        self.logger = logging.getLogger("volume-agent")

        # Allow for background requests
        self.loop = asyncio.new_event_loop()
        threading.Thread(target=self._run_loop, daemon=True).start()

    def schedule_server_message(self, value):
        """
        Schedule to send a message to the agent's server in some future point in time. This method also handles logging in the
        event of an Exception occuring in the thread that the message is being sent on.
        Args:
            value: Either 1 or -1 to increment or decrement.
        """

        future = asyncio.run_coroutine_threadsafe(self._send_to_sever(value), self.loop)
    
    async def _send_to_sever(self, value):
        """
        The _send_to_server method sends an increment or decrement to the volume of this area.
        Args:
            value: Either 1 or -1 to increment or decrement. Value is some enumerated type.
        """
        capture_time = datetime.now(timezone.utc)
        data = {
            "capturedAt": str(capture_time),
            "value": value.value,
        }
        params = {"groupId": self.group_name, "lotId": self.lot_id}

        async with httpx.AsyncClient() as client:
            response = await client.post(self.server, json=data, params=params)
            response.raise_for_status()

    def _run_loop(self):
        """
        The _run_loop method runs an async event loop in the backgroud for the ObjectTracker. This is needed
        to send updates to some given server over a network without request blocking.
        """
        asyncio.set_event_loop(self.loop)
        self.loop.run_forever()

    def cleanup(self):
        """
        The cleanup methods is called when the object tracker is no longer being used.
        """
        self.loop.stop()
        
    def update_SORT_tracker(self, detections):
        """
        The update_SORT_tracker updates the simple, online and realtime tracker that
        differeantiants new objects from ones on the camera.
        Args:
            detections: An array of objects that were detected by the ML model
        Returns:
            The updated objects in the SORT tracker
        """

        if len(detections) > 0:
            detections_np = np.array(detections)
        else:
            detections_np = np.empty((0, 5))

        return self.tracker.update(detections_np)
    
    def update_object_history(self, tracked_objects):
        """
        The update_object_history method updates the Object Tracker's current know objects, adding new ones if needed. Existing objects
        have there keep alive frames reset and are checked for possible state changes
        Args:
            tracked_objects: The current list of seen objects
        """
        for x1, y1, x2, y2, track_id in tracked_objects:
            x1, y1, x2, y2, track_id = int(x1), int(y1), int(x2), int(y2), int(track_id)
            if not track_id in self.object_history:

                self.object_history[track_id] = \
                TrackedObject(
                    track_id, 
                    x1, y1, x2, y2, 
                    alive_frames=self.keep_alive_frames, 
                    line_a_pos=self.line_a_pos, 
                    line_b_pos=self.line_b_pos, 
                    line_tolerance=self.LINE_TOLERANCE,
                    mode=self.mode
                )

            else:
                self.object_history[track_id].set_alive_frames(self.keep_alive_frames)
                self.object_history[track_id].set_coordinates(x1, y1, x2, y2)
                self.object_history[track_id].set_tracked_object_state(self.line_a_pos, self.line_b_pos, self.LINE_TOLERANCE)
                self.check_object_path(self.object_history[track_id])

    def is_entering(self, object):
        """
        The is_entering method determines if the current object is entering the lot.
        Args:
            object: The current object
        Returns:
            True if the object is entering th lot, false otherwise.
        """

        if self.entrance_side == "left":
            return object.object_path == self.LEFT_TO_RIGHT
        else:
            return object.object_path == self.RIGHT_TO_LEFT
        
    def is_exiting(self, object):
        """
        The is_exiting method determines if the current object is entering the lot.
        Args:
            object: The current object
        Returns:
            True if the object is exiting th lot, false otherwise.
        """
        if self.exit_side == "left":
            return object.object_path == self.LEFT_TO_RIGHT
        else:
            return object.object_path == self.RIGHT_TO_LEFT
    
    def check_object_path(self, tracked_object):
        """
        The check_object_path checks the current object's path. If the object's path is entering or exiting the lot,
        then send an update to the server with the data. The object is then removed from memory
        Args:
            tracked_object: The current object
        """
        if tracked_object.was_counted():
            return
        
        EnteredOrExited = False
        if self.is_entering(tracked_object):
            self.schedule_server_message(Direction.ENTERING)
            EnteredOrExited = True
        elif self.is_exiting(tracked_object):
            self.schedule_server_message(Direction.EXITING)
            EnteredOrExited = True

        if EnteredOrExited:
            tracked_object.set_counted(True)

    def remove_old_objects(self, tracked_objects):
        """
        The remove_old_objects method cleans up the object history of objects that have been stored for longer than
        the keep alive frames value without being seen.
        Args:
            tracked_objects: The list of seen objects
        """
        object_history_copy = self.object_history.copy()

        for track_id in object_history_copy.keys():

            if not track_id in tracked_objects:
                prev_alive_frames = self.object_history[track_id].frames
                self.object_history[track_id].set_alive_frames(prev_alive_frames - 1)
                if self.object_history[track_id].frames <= 0:
                    self.object_history.pop(track_id)

        # Reset track ids to prevent memory leak
        if KalmanBoxTracker.count > 100 or not self.object_history:
            KalmanBoxTracker.count = 0
            
    def update_tracked_items(self, detections):
        """
        The update_tracked_items method updates the items in the Object Tracker, making event updates if needed.

        Args:
            detections: The detected objects from the ML model
        Returns:
            The current tracked objects from the Object Tracker
        """
        tracked_objects = self.update_SORT_tracker(detections)
        self.update_object_history(tracked_objects)
        self.remove_old_objects(tracked_objects)
        return tracked_objects
        

class VolumeAgent:
    def __init__(self, objects, confidence, model_path, keep_alive_frames, min_hits, iou_threshold, line_gap, line_start, group_name, lot, server, entrance_side, exit_side, mode="production", exit_key='q'):
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
            lot: The id of the lot this agent is apart of.
            server: URI to send event data to.
            entrance_side: The side of the camera that is the entrance of the lot (From the Camera's POV).
            exit_side: The side of the camera that is the exit of the lot (From the Camera's POV).
            mode: The current mode of the VolumeAgent.
            exit_key: The key to press to exit the camera debugger
        """
        self.objects = objects
        self.confidence = confidence
        self.camera = Camera(name="Volume Agent")
        self.model = YOLO(model_path)
        self.obj_tracker = ObjectTracker(keep_alive_frames, min_hits, iou_threshold, group_name, lot, server, line_start, line_gap, entrance_side, exit_side, mode=mode)
        self.line_gap = line_gap
        self.line_start = line_start
        self.group_name = group_name
        self.sever = server
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

    def draw_debug_lines(self, frame, line_color=(0, 0, 255)):
        """
        The _draw_debug_lines is a method that draws the lines objects intersect with onto the camera.
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

    def gather_detections(self, detection_results):
        """
        The gather_detections method is use to gather the number of objects found in a specifc frame.
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

    def draw_debug_data(self, frame, tracked_objects):
        """
        The draw_debug_data is a method that draws the camera to the screen along with debug information.
        Args:
            frame: The frame to draw the debug data to.
            tracked_objects: The objects to add debug data to on the screen.
        """
        self.draw_debug_lines(frame)

        for x1, y1, x2, y2, track_id in tracked_objects:
            track_id = int(track_id)
            cv2.rectangle(frame, (int(x1), int(y1)), (int(x2), int(y2)), (0, 255, 0), 2)
            cv2.putText(frame, f"track_id: {track_id}", (int(x1), int(y1) - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.6, (0, 255, 0), 2)

        self.camera.display_camera_frame(frame)
    
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
            detections = self.gather_detections(detection_results)
            objects_on_screen = self.obj_tracker.update_tracked_items(detections)

            if not self.mode == "production":
                self.draw_debug_data(frame, objects_on_screen)
                self.camera.display_camera_frame(frame)
                self.logger.info("Current Tracked Objects")
                self.logger.info("=================")
                for obj in self.obj_tracker.object_history.values():
                    self.logger.info(str(obj))
                self.logger.info("=================")

        self.camera.shutdown()
        self.obj_tracker.cleanup()

def run():
    """
    The run function starts the volume agent by pulling variables from a known config file.
    """
    # Make YOLO quiet
    logging.getLogger("ultralytics").setLevel(logging.ERROR)
    
    config = toml.load("config.toml")

    # Make sure entrance and exit input is correct
    entrance = config["entrance_side"]
    exit = config["exit_side"]
    options = {"left", "right"}
    taken_option = ""

    if not entrance in options:
        print(f"Error: Entrance value '{entrance}' not valid. Valid options: {options}", file=sys.stderr)
        sys.exit(1)
    options.remove(entrance)
    if not exit in options:
        print(f"Exit value '{exit}' not valid. Valid options: {options}. Value '{entrance}' already taken by entrance", file=sys.stderr)
        sys.exit(1)

    # Start the agent
    agent = VolumeAgent(
        objects=config["valid_objects"],
        confidence=float(config["confidence_percentage"]),
        model_path=config["model_path"],
        keep_alive_frames=int(config["keep_alive_frames"]),
        min_hits=int(config["minimum_frames_before_detection"]),
        iou_threshold=float(config["iou_threshold"]),
        line_start=int(config["line_start"]),
        line_gap=int(config["line_gap"]),
        group_name=config["group"],
        lot=config["lot"],
        server=config["data_server"],
        entrance_side=entrance,
        exit_side=exit,
        mode=config["mode"],
    )
    agent.run()
