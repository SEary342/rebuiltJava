# Team 9721 FRC Robot 2026 - Java

This repository contains the Java-based command-oriented robot code for the 2026 FRC season, ported from the previous Python (Robotpy) implementation.

## 🎮 Controller Layout (Logitech F310)

The robot is controlled using two Logitech F310 controllers (Driver and Operator). Both controllers share core mechanism controls, while the Driver has exclusive access to drivetrain operations.

![Controller Layout](Controls.jpg)

### Core Mechanism Controls (Shared)
| Input | Action |
| :--- | :--- |
| **Right Bumper** | **Launch Sequence**: Fires fuel using the currently active RPM mode. |
| **Left Bumper** | **Intake**: Pulls fuel into the robot. |
| **A Button** | **Eject**: Reverses the intake to clear jams. |
| **Y Button** | **Toggle RPM Mode**: Switches between **Vision-Calculated RPM** and **Manual RPM**. |
| **POV Left/Right** | **Cycle Manual RPM**: Increments/decrements through the calibrated RPM lookup table. |
| **POV Up/Down** | **Climber**: Controls the robot's climbing mechanism. |

### Driver Exclusive Controls
| Input | Action |
| :--- | :--- |
| **Left Stick** | **Translation**: Move robot Forward/Backward and Left/Right. |
| **Right Stick** | **Rotation**: Rotate the robot. |
| **Left Trigger** | **Turn Left**: Slowly rotates to the left |
| **Right Trigger** | **Turn Right**: Slowly rotates to the right |
| **X Button** | **Auto-Aim**: Uses PhotonVision to rotate the robot toward the target. |
| **B Button** | **Parking Brake**: Applies parking brakes when held |
| **Left Trigger** | **Parking Brake**: Sets swerve modules to an 'X' pattern to prevent sliding. |
| **Start Button** | **Zero Gyro**: Resets the field-relative heading. |

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
    *   *Alternatively: Click the WPILib "W" icon in the top right and select "Deploy Robot Code".*
5.  **Verify**: Check the **Driver Station** to ensure the code status turns green.

### Troubleshooting
*   **No Connection**: Ensure your IP is set to `10.TE.AM.XX` or you are using the `172.22.11.2` USB address.
*   **Build Failures**: Run `./gradlew clean` in the terminal and try building again.
*   **Vendor Deps**: If libraries are missing, run `WPILib: Manage Vendor Libraries` -> `Check for updates (offline)`.

---

## 📸 Vision System
The robot uses **PhotonVision** with an Arducam OV9281.
*   **Mounting**: 10° upward pitch (80° off horizontal mounting plate).
*   **Targets**: Configured in `Constants.TargetConstants`.
*   **Distance**: Automatically calculated to interpolate required launcher RPM.
