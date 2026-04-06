import ntcore
import time
import socket

def get_ip():
    """Helper to find your laptop's IP address to give to the Pi."""
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    try:
        s.connect(('10.255.255.255', 1))
        IP = s.getsockname()[0]
    except Exception:
        IP = '127.0.0.1'
    finally:
        s.close()
    return IP

def run_sim():
    inst = ntcore.NetworkTableInstance.getDefault()
    
    # 1. Start as a SERVER
    inst.startServer()
    
    my_ip = get_ip()
    print(f"--- NT Server Started (Simulating RoboRIO) ---")
    print(f"--- Laptop IP: {my_ip} ---")
    print("--- Use this IP in your LED Service script! ---\n")

    # 2. Setup Tables
    sd_table = inst.getTable("SmartDashboard")
    led_table = inst.getTable("LEDs")

    # 3. Setup Publishers
    state_pub = led_table.getStringTopic("state").publish()
    mode_pub = sd_table.getStringTopic("RobotMode").publish()
    has_target_pub = sd_table.getBooleanTopic("Vision/HasTarget").publish()

    print("Controls: [1: Disabled (Red), 2: Auton (Yellow), 3: Teleop (Green), 4: Toggle Target, q: Quit]")

    has_target = False
    while True:
        choice = input("Command > ").strip().lower()
        
        if choice == '1':
            mode_pub.set("DISABLED")
        elif choice == '2':
            mode_pub.set("AUTON")
        elif choice == '3':
            mode_pub.set("TELEOP")
        elif choice == '4':
            has_target = not has_target
            has_target_pub.set(has_target)
            print(f"Targeting: {has_target}")
        elif choice == 'q':
            break
        
        time.sleep(0.1)

if __name__ == "__main__":
    run_sim()