from typing import Callable, Optional
import paho.mqtt.client as mqtt
import threading
import json
import time
import toml
import logging
import agent
import socket

class MQTTPayloadReceiver:
    """
    The MQTTPayloadReceiver is used to call out to some MQTT broker for payloads to update the underlying configuration
    of this device.

    When updating configuration of this device using this method, all fields are updated instantly except for
    the MQTT information itself (ex: broker server, broker port, topic). Those can only be updated when a new
    MQTTPayloadReceiver Object is defined. A Quick way to do so is to restart the whole app.
    """
    def __init__(self, broker_address, broker_port, topic):
        self.broker_address = broker_address
        self.broker_port = broker_port
        self.topic = topic
        self.client_id = socket.gethostname()
        self.logger = logging.getLogger("mqtt-agent")
        
        self.payload_callback: Optional[Callable] = None
        
        self.client = mqtt.Client(mqtt.CallbackAPIVersion.VERSION2)
        self.client.on_connect = self._on_connect
        self.client.on_message = self._on_message
        self.client.on_disconnect = self._on_disconnect
        
        self.is_connected = False
        self._stop_flag = threading.Event()
        
    def set_credentials(self, username: str, password: str):
        """Set MQTT broker credentials if authentication is required"""
        self.client.username_pw_set(username, password)
    
    def set_payload_callback(self, callback: Callable):
        """
        Set a callback function to be called when new payload arrives.
        Callback should accept one argument: the payload data.
        """
        self.payload_callback = callback
    
    def _on_connect(self, client, userdata, flags, rc):
        if rc == 0:
            self.is_connected = True
            client.subscribe(self.topic)
        else:
            self.is_connected = False
            self.logger.error(f"Connection failed with code: {rc}")
    
    def _on_message(self, client, userdata, msg):
        try:
            payload_str = msg.payload.decode()
            
            try:
                payload_data = json.loads(payload_str)
            except json.JSONDecodeError:
                payload_data = payload_str
            
            self.payload_queue.put({
                'topic': msg.topic,
                'payload': payload_data,
            })
            
            if self.payload_callback:
                self.payload_callback(payload_data)
                
        except Exception as e:
            print(f"[MQTT] Error processing message: {e}")
    
    def _on_disconnect(self, client, userdata, rc):
        self.is_connected = False
        if rc != 0:
            self.logger.error(f"Unexpected disconnection. Code: {rc}")
        else:
            self.logger.info(f"Disconnected from {self.broker_address}:{self.broker_port}")
    
    def start(self):
        """Start the MQTT client in a background thread"""
        try:
            self.logger.info(f"Connecting to {self.broker_address}:{self.broker_port}...")
            self.client.connect(self.broker_address, self.broker_port, keepalive=60)
            self.client.loop_start()
            self.logger.info(f"Connected to {self.broker_address}:{self.broker_port} and subscribed to topic: {self.topic}")
            return True
        except Exception as e:
            self.logger.error(f"Failed to start: {e}")
            return False
    
    def stop(self):
        """Stop the MQTT client"""
        self.logger.info("Stopping client...")
        self._stop_flag.set()
        self.client.loop_stop()
        self.client.disconnect()
        self.logger.info("Client stopped")


def on_new_payload(payload):
    """Callback function when new payload arrives"""
    global agent_thread
    
    if not validate_payload(payload):
        logging.getLogger("mqtt-agent").warning("Invalid payload received")
        return
    
    logging.getLogger("mqtt-agent").info("Valid payload received, stopping agent...")
    
    agent.stop()
    agent_thread.join(timeout=5.0)
    
    logging.getLogger("mqtt-agent").info("Applying changes from payload...")
    apply_changes(payload)
    
    logging.getLogger("mqtt-agent").info("Restarting agent...")
    agent_thread = threading.Thread(target=agent.run, daemon=True)
    agent_thread.start()
    
    logging.getLogger("mqtt-agent").info("Agent restarted successfully")

def validate_payload(payload):
    """Validate the incoming payload"""

    # Variables that have manually been created in the config file are considered valid
    valid_variables = toml.load("config.toml").keys()
    for variable in payload.keys():
        if not variable in valid_variables:
            logging.getLogger("mqtt-agent").warning(f"Invalid variable received in MQTT payload '{variable}'")
            return False

    return True


def apply_changes(payload):
    """Apply changes from the payload"""
    pass


if __name__ == "__main__":
        
    format_string = "%(asctime)s [%(levelname)s] %(name)s: %(message)s"
    logging.basicConfig(
        level=logging.INFO,
        format=format_string,
        handlers=[logging.StreamHandler()]
    )

    config = toml.load("config.toml")
    broker_address = config["broker_address"]
    broker_port = config["broker_port"]
    topic = config["topic"]
    
    mqtt_receiver = MQTTPayloadReceiver(
        broker_address=broker_address,
        broker_port=broker_port,
        topic=topic
    )
    
    mqtt_receiver.set_payload_callback(on_new_payload)
    
    agent_thread = threading.Thread(target=agent.run, daemon=True)
    agent_thread.start()
    
    # Every 3 seconds, verify that the server connection is valid
    try:
        while (True):
            time.sleep(3)
            if not mqtt_receiver.is_connected:
                mqtt_receiver.start()

    except KeyboardInterrupt:
        mqtt_receiver.stop()