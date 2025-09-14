from ultralytics import YOLO
from sort import Sort
import cv2
import sys
import numpy as np
import math
import toml

class Camera:
    def __init__(self, name, width=640, height=480):
        self.cap = cv2.VideoCapture(0)
        if not self.cap.isOpened():
            print("Error: Could not open Camera", file=sys.stderr)
            exit(1)

        self.name = name
        self.cap.set(cv2.CAP_PROP_FRAME_WIDTH, width)
        self.cap.set(cv2.CAP_PROP_FRAME_HEIGHT, height)
        
    def get_frame(self):
        return self.cap.read()
    
    def draw_vertical_line(self, frame, line_color, starting_point, ending_point):
        cv2.line(frame, starting_point, ending_point, line_color, 5)

    def display_camera_frame(self, frame):
        cv2.imshow(self.name, frame)

    def shutdown(self):
        self.cap.release()
        cv2.destroyAllWindows()
    
class ObjectTracker:
    def __init__(self, keep_alive_frames, min_hits, iou_threshold):
        self.tracker = Sort(max_age=keep_alive_frames, min_hits=min_hits, iou_threshold=iou_threshold)
        self.line_a_objs = {}
        self.line_b_objs = {}
        self.volume = 0
        self.keep_alive_frames = keep_alive_frames

    def update_objs_in_line_storage(self, objects):
        for track_id, line in objects:
            if line == 'A':
                if track_id in self.line_b_objs:
                    # Send Data: Entering
                    self.volume += 1
                    self.line_b_objs.pop(track_id)
                    pass
                else:
                    self.line_a_objs[track_id] = self.keep_alive_frames
            elif line == 'B':
                if track_id in self.line_a_objs:
                    # Send Data: Leaving
                    self.volume -= 1
                    self.line_a_objs.pop(track_id)
                else:
                    self.line_b_objs[track_id] = self.keep_alive_frames

    def adjust_alive_time_line_sets(self, tracked_objects):
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

class VolumeAgent:
    def __init__(self, objects, confidence, model_path, keep_alive_frames, min_hits, iou_threshold, line_gap, line_start, mode="production", exit_key='q'):
        self.objects = objects
        self.confidence = confidence
        self.camera = Camera(name="Volume Agent")
        self.model = YOLO(model_path)
        self.obj_tracker = ObjectTracker(keep_alive_frames, min_hits, iou_threshold)
        self.line_gap = line_gap
        self.line_start = line_start
        self.mode = mode
        if not exit_key.isalpha():
            print(f"Error: exit key is not valid. Expected alpha numeric key, got {exit_key}", file=sys.stderr)
            exit(1)
        self.exit_key = exit_key

    def _draw_debug_lines(self, frame, line_color=(0, 0, 255)):
        height, width = frame.shape[:2]
        val = self.line_start + self.line_gap
        if width < val:
            print(f"Error: Location of ending line out of bounds from frame. {val} > {width} (Frame Width)", file=sys.stderr)
            exit(1)
        self.camera.draw_vertical_line(frame, line_color, (self.line_start, 0), (self.line_start, height))
        self.camera.draw_vertical_line(frame, line_color, (val, 0), (val, height))

    def _gather_detections(self, detection_results):
        detections = []
        for result in detection_results:
            for box in result.boxes:
                x1, y1, x2, y2 = map(int, box.xyxy[0])
                confidence = math.ceil((box.conf[0] * 100)) / 100
                class_id = int(box.cls[0])
                class_name = self.model.names[class_id]
                if class_name in self.objects and confidence > self.confidence:
                    if not self.mode == "production":
                        print(f"Detected {class_name} at ({x1},{y1}) ({x2},{y2}) with confidence {confidence}")
                    detections.append([x1, y1, x2, y2, confidence])

        return detections

    def _update_tracked_items(self, detections):
            if len(detections) > 0:
                detections_np = np.array(detections)
            else:
                detections_np = np.empty((0, 5))

            # Update SORT tracker
            return self.obj_tracker.tracker.update(detections_np)

    def _draw_debug_data(self, frame, tracked_objects):
            self._draw_debug_lines(frame)
            for x1, y1, x2, y2, track_id in tracked_objects:
                track_id = int(track_id)
                cv2.rectangle(frame, (int(x1), int(y1)), (int(x2), int(y2)), (0, 255, 0), 2)
                cv2.putText(frame, f"track_id: {track_id}", (int(x1), int(y1) - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.6, (0, 255, 0), 2)

            self.camera.display_camera_frame(frame)
    
    def _is_intersecting_with_line(self, obj_x1y1, obj_x2y2, line_x1y1):
        obj_x1, _ = obj_x1y1
        obj_x2, _ = obj_x2y2
        line_x, _= line_x1y1
        return obj_x1 <= line_x <= obj_x2
    
    def _get_intersecting_objects(self, tracked_objects):
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
        while True:
            success, frame = self.camera.get_frame()
            if not success:
                print("Error: Failed to get frame", file=sys.stderr)
                break

            if cv2.waitKey(1) & 0xFF == ord(self.exit_key):
                break

            detection_results = self.model(frame, stream=True)
            detections = self._gather_detections(detection_results)
            tracked_objects = self._update_tracked_items(detections)
            self.obj_tracker.adjust_alive_time_line_sets(tracked_objects)
            intersecting_objects = self._get_intersecting_objects(tracked_objects)
            self.obj_tracker.update_objs_in_line_storage(intersecting_objects)
            print(self.obj_tracker.volume)
            print(self.obj_tracker.line_a_objs)
            print(self.obj_tracker.line_b_objs)
            if not self.mode == "production":
                self._draw_debug_data(frame, tracked_objects)

        self.camera.shutdown()

if __name__ == "__main__":

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
        mode=config["mode"],
    )
    
    agent.run()