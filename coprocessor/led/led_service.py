import time
import board
import neopixel
import ntcore
import threading

# --- Configuration ---
# WS2812B Wiring:
# 1. Data -> GPIO 18 (PWM0)
# 2. Ground -> Any Pi GND (must be shared with LED power ground)
# 3. (Optional) 5V -> Only if you aren't using your separate power wires.
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
    "PURPLE": (200, 0, 255),
    "WHITE": (255, 255, 255),
}


class LEDController:
    def __init__(self):
        # Initialize for WS2812B
        # pixel_order=neopixel.GRB is the standard for WS2812B.
        # If colors are swapped (e.g. Red shows as Green), change to neopixel.RGB
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

    def _show_rainbow(self, speed=0.01):
        def wheel(pos):
            if pos < 0 or pos > 255:
                return (0, 0, 0)
            if pos < 85:
                return (255 - pos * 3, pos * 3, 0)
            if pos < 170:
                pos -= 85
                return (0, 255 - pos * 3, pos * 3)
            pos -= 170
            return (pos * 3, 0, 255 - pos * 3)

        for j in range(255):
            with self._lock:
                if self.current_state != "RAINBOW":
                    break
            for i in range(LED_COUNT):
                pixel_index = (i * 256 // LED_COUNT) + j
                self.pixels[i] = wheel(pixel_index & 255)
            self.pixels.show()
            time.sleep(speed)

    def run_loop(self):
        """Main animation loop."""
        last_state = None
        while True:
            with self._lock:
                state = self.current_state

            if state == "RAINBOW":
                self._show_rainbow()
            elif state.startswith("BLINK_"):
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
    inst.setServerTeam(TEAM_NUMBER)
    inst.startClient4("LED_Pi")

    table = inst.getTable("LEDs")
    # Subscribe to "state", default to "OFF"
    state_sub = table.getStringTopic("state").subscribe("OFF")

    # 2. Start LED Controller
    controller = LEDController()

    # Run animation loop in a separate thread so NT remains responsive
    anim_thread = threading.Thread(target=controller.run_loop, daemon=True)
    anim_thread.start()

    print(f"LED Service Listening for Team {TEAM_NUMBER}...")

    while True:
        # Update the controller state based on NT value
        new_state = state_sub.get()
        if new_state != controller.current_state:
            print(f"Switching to state: {new_state}")
            controller.set_state(new_state)
        time.sleep(0.05)


if __name__ == "__main__":
    run_service()
