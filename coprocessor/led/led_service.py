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

LED_COUNT = 82
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

# --- Colorwheel helper for the rainbow effect ---
def colorwheel(pos):
    """Input a value 0 to 255 to get a color value."""
    pos = int(pos) % 255
    if pos < 85:
        return (255 - pos * 3, pos * 3, 0)
    if pos < 170:
        pos -= 85
        return (0, 255 - pos * 3, pos * 3)
    pos -= 170
    return (pos * 3, 0, 255 - pos * 3)


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
        self.current_state = "PURPLE"
        self._lock = threading.Lock()
        
        # State variables for animation tracking
        self.scan_pos = 0
        self.scan_dir = 1
        self.rainbow_cycle = 0

    def set_state(self, state):
        with self._lock:
            self.current_state = state

    def get_color(self, name):
        """Centralized color manager to handle standard colors and RAINBOW."""
        if name == "RAINBOW":
            return colorwheel(self.rainbow_cycle)
        return COLORS.get(name, COLORS["RED"])

    def _show_solid(self, color):
        self.pixels.fill(color)
        self.pixels.show()

    def _show_blink(self, color, speed=0.25):
        # Non-blocking blink check based on current time
        if (time.time() // speed) % 2 == 0:
            self.pixels.fill(color)
        else:
            self.pixels.fill((0, 0, 0))
        self.pixels.show()

    def _show_scan(self, color):
        self.pixels.fill((0, 0, 0))
        
        # Draw a wider 5-pixel "eye" with smoother fade
        for i in range(-2, 3):
            idx = self.scan_pos + i
            if 0 <= idx < LED_COUNT:
                if i == 0:
                    self.pixels[idx] = color # Center pixel is full brightness
                elif abs(i) == 1:
                    self.pixels[idx] = (color[0]//2, color[1]//2, color[2]//2) # Inner edges
                elif abs(i) == 2:
                    self.pixels[idx] = (color[0]//10, color[1]//10, color[2]//10) # Outer edges

        self.pixels.show()

        # Ping-pong the position
        self.scan_pos += self.scan_dir
        if self.scan_pos >= LED_COUNT - 1 or self.scan_pos <= 0:
            self.scan_dir *= -1

    def _show_rainbow_gradient(self):
        """Calculates a gradient across the entire strip."""
        for i in range(LED_COUNT):
            # The '3' determines how many rainbows fit on the strip at once
            # Higher number = more condensed rainbow
            pixel_index = (i * 256 // LED_COUNT) + self.rainbow_cycle
            self.pixels[i] = colorwheel(pixel_index & 255)
        self.pixels.show()

    def run_loop(self):
        """Main animation loop."""
        last_state = None
        while True:
            with self._lock:
                state = self.current_state

            # Advance the global rainbow counter
            self.rainbow_cycle = (self.rainbow_cycle + 2) % 255

            if state == "RAINBOW":
                self._show_rainbow_gradient()
            elif state.startswith("BLINK_"):
                color_name = state.replace("BLINK_", "")
                self._show_blink(self.get_color(color_name))
            elif state.startswith("SCAN_"):
                color_name = state.replace("SCAN_", "")
                self._show_scan(self.get_color(color_name))
            else:
                # Fallback to solid color logic
                self._show_solid(self.get_color(state))

            last_state = state
            time.sleep(0.05)


def run_service():
    inst = ntcore.NetworkTableInstance.getDefault()
    controller = LEDController()

    # Start animation thread immediately so LEDs show "OFF" or a "BOOT" color
    anim_thread = threading.Thread(target=controller.run_loop, daemon=True)
    anim_thread.start()

    # 1. Setup NetworkTables
    inst.setServerTeam(TEAM_NUMBER)
    # inst.setServer("192.168.1.15") # Use for laptop testing
    inst.startClient4("LED_Pi")

    # 2. Use a Connection Listener
    # This helps us know if we are actually talking to the robot
    def on_connection(event):
        # We use the .is_() method with the bitmask flags
        # This is the most stable way across different RobotPy versions
        if event.is_(ntcore.EventFlags.kConnected):
            print("Connected to RoboRIO!")
        elif event.is_(ntcore.EventFlags.kDisconnected):
            print("Disconnected from RoboRIO - Waiting...")
            controller.set_state("PURPLE")

    inst.addConnectionListener(True, on_connection)

    # 3. Setup Tables and Subscriptions
    table = inst.getTable("LEDs")
    sd_table = inst.getTable("SmartDashboard")

    state_sub = table.getStringTopic("state").subscribe("OFF")
    has_target_sub = sd_table.getBooleanTopic("Vision/HasTarget").subscribe(False)
    mode_sub = sd_table.getStringTopic("RobotMode").subscribe("DISABLED")

    print(f"LED Service active. Waiting for Team {TEAM_NUMBER}...")

    while True:
        # Check if we are connected before processing logic
        if inst.isConnected():
            has_target = has_target_sub.get()
            robot_mode = mode_sub.get()
            manual_state = state_sub.get()

            # Priority Logic
            if robot_mode == "DISABLED":
                target_state = "SCAN_RED"
            elif robot_mode == "AUTON":
                target_state = "BLINK_YELLOW" if has_target else "YELLOW"
            elif robot_mode == "TELEOP":
                target_state = "BLINK_GREEN" if has_target else "GREEN"
            else:
                target_state = manual_state

            if target_state != controller.current_state:
                controller.set_state(target_state)
        else:
            controller.set_state("PURPLE")

        time.sleep(0.1) # Slightly slower poll rate to save CPU


if __name__ == "__main__":
    run_service()
