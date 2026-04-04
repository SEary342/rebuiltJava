# Team 9721 FRC Robot 2026 - Java

This repository contains the Java-based command-oriented robot code for the 2026 FRC season, ported from the previous Python (Robotpy) implementation.

## 🎮 Controller Layout (Logitech F310 & Flight Stick)

The robot is controlled using two Logitech F310 controllers (Driver and Operator) or a Flight Joystick (Driver). Both driver inputs are combined for side-by-side testing.

![Controller Layout](Controls.jpg)
*(Note: Controls.jpg may be outdated. Refer to the tables below for current mappings.)*

### Core Mechanism Controls (Shared - Xbox)
| Input | Action |
| :--- | :--- |
| **Right Bumper** | **Launch Sequence**: Fires fuel using the currently active RPM mode. |
| **Left Bumper** | **Intake**: Pulls fuel into the robot. |
| **A Button** | **Eject**: Reverses the intake to clear jams. |
| **Y Button** | **Toggle RPM Mode**: Switches between **Vision-Calculated RPM** and **Manual RPM**. |
| **POV Left/Right** | **Cycle Manual RPM**: Increments/decrements through the calibrated RPM lookup table. |
| **POV Up/Down** | **Climber**: Controls the robot's climbing mechanism. |

### Driver Exclusive Controls (Xbox - Port 0)
| Input | Action |
| :--- | :--- |
| **Left Stick** | **Translation**: Move robot Forward/Backward and Left/Right. |
| **Right Stick** | **Rotation**: Rotate the robot (now uses asymmetric slew rates for snappy stops). |
| **Left Trigger** | **Slow Mode**: Hold to reduce robot speed to 40% for precision alignment. |
| **X Button** | **Auto-Aim**: Uses PhotonVision to rotate the robot toward the target. |
| **B Button** | **Parking Brake**: Sets swerve modules to an 'X' pattern to prevent sliding. |
| **Start Button** | **Zero Gyro**: Resets the field-relative heading. |

### Driver Exclusive Controls (Flight Joystick - Port 3)
| Input | Action |
| :--- | :--- |
| **Y Axis** | **Translation (X)**: Move robot Forward/Backward. |
| **X Axis** | **Translation (Y)**: Move robot Left/Right. |
| **Z Axis (Twist)** | **Rotation**: Rotate the robot. |
| **Button 1 (Trigger)** | **Slow Mode**: Hold to reduce robot speed to 40% for precision alignment. |
| **Button 3** | **Auto-Aim**: Uses PhotonVision to rotate the robot toward the target. |
| **Button 2** | **Parking Brake**: Sets swerve modules to an 'X' pattern to prevent sliding. |
| **Button 7** | **Zero Gyro**: Resets the field-relative heading. |

---

## ⚙️ Drivetrain & Input Tuning Guide

All driver-feel and performance constants are centralized in `src/main/java/frc/robot/Constants.java`.

### 1. Responsiveness & "Feel" (`InputConstants`)
Adjust these to change how the robot reacts to joystick movement:

*   **`kLinearAccelerationLimit` (default: 2.0):** Controls how fast the robot speeds up. Increase for a "punchier" feel, decrease if the robot is "twitchy."
*   **`kLinearDecelerationLimit` (default: -8.0):** Controls how fast the robot stops. A high negative value ensures a snappy, immediate stop without "drifting."
*   **`kLinearExponent` (default: 2.0):** The sensitivity curve. `2.0` (squared) provides more precision at low speeds.
*   **`kJoystickDeadband` (default: 0.15):** Increase if the robot "creeps" when the sticks are released.

### 2. Speed Limits (`DriveConstants` & `OperatorConstants`)
*   **`kMaxSpeedMetersPerSecond` (`DriveConstants`):** The absolute maximum the drivetrain is allowed to go.
*   **`SPEED_LIMIT` (`OperatorConstants`):** The default Teleop speed scale (e.g., `0.4` = 40% of max speed).
*   **`kSlowModeModifier` (`InputConstants`):** The speed multiplier applied while holding **Slow Mode** (default: `0.4`).

---

## 🛠️ Deployment Instructions

### Prerequisites
1.  **WPILib VS Code**: Ensure you have the [WPILib 2026 Installer](https://docs.wpilib.org/en/stable/docs/zero-to-robot/step-2/wpilib-setup.html) installed.
2.  **JDK 17**: Included with the WPILib installation.

### Flashing the Robot
1.  **Connect to the Robot**: Connect via USB-B (tether), Ethernet, or the Robot Radio (Wi-Fi).
2.  **Open the Project**: Open this folder in WPILib VS Code.
3.  **Build Code**: 
    *   Press `Ctrl+Shift+P` (Windows) or `Cmd+Shift+P` (Mac).
    *   Type `WPILib: Build Robot Code` and press Enter.
4.  **Deploy Code**:
    *   Press `Ctrl+Shift+P` / `Cmd+Shift+P`.
    *   Type `WPILib: Deploy Robot Code` and press Enter.
5.  **Verify**: Check the **Driver Station** to ensure the code status turns green.

---

## 📸 Vision System
The robot uses **PhotonVision** with an Arducam OV9281.
*   **Mounting**: 10° upward pitch (80° off horizontal mounting plate).
*   **Targets**: Configured in `Constants.TargetConstants`.
*   **Distance**: Automatically calculated to interpolate required launcher RPM.
