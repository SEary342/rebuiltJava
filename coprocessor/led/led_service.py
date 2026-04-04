import time
import board
import neopixel
import ntcore
import threading

# --- Configuration ---
# Wiring for Strip (3 Control Wires + 2 Power Wires):
#
# Control Connector (3-pin):
# 1. GREEN (Data)  -> Raspberry Pi GPIO 18 (PWM0)
# 2. WHITE (GND)   -> Raspberry Pi GND (Must be shared with LED power ground)
# 3. RED   (5V)    -> Connected to 5V Power Supply
#
# Power Injection (2-pin):
# * 1. RED   (5V)    -> Connected to 5V Power Supply
# * 2. WHITE (GND)   -> Connected to Power Supply GND
#
# Note: Ensure the Pi GND and the LED Power Supply GND are connected.

LED_COUNT = 60
LED_PIN = board.D18  # GPIO 18 is best for WS2812B PWM timing
BRIGHTNESS = 0.4  # 0.0 to 1.0
TEAM_NUMBER = 9721

# Define Colors (R, G, B)
COLORS = {
    "OFF": (0, 0, 0),
    "RED": (255, 0, 0),
    "GREEN": (0, 255, 0),
    "BLUE": (0, 0, 255),
    "GOLD": (255, 100, 0),
    "YELLOW": (255, 150, 0),
    "PURPLE": (200, 0, 255),
    "WHITE": (255, 255, 255),
}


class LEDController:
    def __init__(self):
        # Initialize for WS2812B
        # pixel_order=neopixel.GRB is the standard for WS2812B.
        self.pixels = neopixel.NeoPixel(
            LED_PIN,
            LED_COUNT,
            brightness=BRIGHTNESS,
            auto_write=False,
            pixel_order=neopixel.GRB,
        )
        self.current_state = "OFF"
        self._lock = threading.Lock()

    def set_state(self, state):
        with self._lock:
            self.current_state = state

    def _show_solid(self, color):
        self.pixels.fill(color)
        self.pixels.show()

    def _show_blink(self, color, speed=0.25):
        self.pixels.fill(color)
        self.pixels.show()
        time.sleep(speed)
        self.pixels.fill((0, 0, 0))
        self.pixels.show()
        time.sleep(speed)

    def run_loop(self):
        """Main animation loop."""
        last_state = None
        while True:
            with self._lock:
                state = self.current_state

            if state.startswith("BLINK_"):
                color_name = state.replace("BLINK_", "")
                self._show_blink(COLORS.get(color_name, COLORS["RED"]))
            elif state != last_state:
                # Only update solid colors if the state actually changed to save CPU
                self._show_solid(COLORS.get(state, COLORS["OFF"]))

            last_state = state
            time.sleep(0.05)


def run_service():
    # 1. NetworkTables Setup
    inst = ntcore.NetworkTableInstance.getDefault()
    inst.setServer(192.168.1.117)
    # inst.setServerTeam(TEAM_NUMBER)
    inst.startClient4("LED_Pi")

    table = inst.getTable("LEDs")
    # Subscribe to "state", default to "OFF"
    state_sub = table.getStringTopic("state").subscribe("OFF")

    # Subscribe to Vision Target status and Robot Mode from SmartDashboard
    sd_table = inst.getTable("SmartDashboard")
    has_target_sub = sd_table.getBooleanTopic("Vision/HasTarget").subscribe(False)
    mode_sub = sd_table.getStringTopic("RobotMode").subscribe("DISABLED")

    # 2. Start LED Controller
    controller = LEDController()

    # Run animation loop in a separate thread so NT remains responsive
    anim_thread = threading.Thread(target=controller.run_loop, daemon=True)
    anim_thread.start()

    print(f"LED Service Listening for Team {TEAM_NUMBER}...")

    while True:
        has_target = has_target_sub.get()
        robot_mode = mode_sub.get()
        manual_state = state_sub.get()

        # Priority Logic:
        # 1. Disabled -> Solid RED
        # 2. Auton -> Solid YELLOW (Blink if target)
        # 3. Teleop -> Solid GREEN (Blink if target)
        # 4. Manual -> manual_state

        if robot_mode == "DISABLED":
            target_state = "RED"
        elif robot_mode == "AUTON":
            target_state = "BLINK_YELLOW" if has_target else "YELLOW"
        elif robot_mode == "TELEOP":
            target_state = "BLINK_GREEN" if has_target else "GREEN"
        else:
            # Fallback to manual state from NetworkTables
            target_state = manual_state

        # Update the controller state if it changed
        if target_state != controller.current_state:
            print(f"Switching to state: {target_state} (Mode: {robot_mode}, Target: {has_target})")
            controller.set_state(target_state)
        time.sleep(0.05)


if __name__ == "__main__":
    run_service()
