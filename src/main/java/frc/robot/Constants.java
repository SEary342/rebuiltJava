// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.stream.IntStream;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.util.Units;

/**
 * CONSTANTS FILE - Robot Settings & Tuning!
 *
 * This file contains ALL the numbers that make your robot work.
 * Think of it like the settings menu on a video game!
 *
 * 🎮 TUNING GUIDE: Change these values to make your robot drive, shoot, and behave better!
 * - Too fast? Make numbers smaller
 * - Too slow? Make numbers bigger
 * - Jerky movements? Adjust acceleration limits
 * - Wrong directions? Check motor IDs and angles
 */
public final class Constants {

  // ===========================================
  // 🚗 DRIVING CONSTANTS - Make robot move!
  // ===========================================

  public static final class DriveConstants {

    // MAXIMUM SPEEDS - How fast your robot can go
    // REAL LIFE: Like speed limit signs on a road
    // TUNE: Test on smooth floor, adjust based on battery and weight
    public static final double kMaxSpeedMetersPerSecond = 4.8;  // 4.8 m/s = ~10.7 mph
    public static final double kMaxAngularSpeed = 2 * Math.PI;   // Full rotation per second

    // ROBOT SIZE - Measure your robot with a tape measure!
    // TUNE: Measure from wheel center to wheel center
    // WRONG VALUES = Robot won't drive straight or turn properly
    public static final double kTrackWidth = Units.inchesToMeters(26.5);  // Left to right wheels
    public static final double kWheelBase = Units.inchesToMeters(26.5);   // Front to back wheels

    // MATH MAGIC - Calculates how wheels work together
    // DON'T CHANGE: This math makes swerve drive possible!
    public static final SwerveDriveKinematics kDriveKinematics = new SwerveDriveKinematics(
        new Translation2d(kWheelBase / 2, kTrackWidth / 2),    // Front-left
        new Translation2d(kWheelBase / 2, -kTrackWidth / 2),   // Front-right
        new Translation2d(-kWheelBase / 2, kTrackWidth / 2),   // Back-left
        new Translation2d(-kWheelBase / 2, -kTrackWidth / 2)); // Back-right

    // WHEEL ANGLES - Calibrate each wheel to point straight forward
    // TUNE: Use REV Hardware Client to find correct angles when wheels face forward
    // WRONG VALUES = Wheels turn wrong way, robot goes crazy!
    public static final double kFrontLeftChassisAngularOffset = -Math.PI / 2;  // -90 degrees
    public static final double kFrontRightChassisAngularOffset = 0;            // 0 degrees
    public static final double kBackLeftChassisAngularOffset = Math.PI;        // 180 degrees
    public static final double kBackRightChassisAngularOffset = Math.PI / 2;   // 90 degrees

    // MOTOR IDs - Which motor controller controls which wheel
    // TUNE: Check your robot wiring! Wrong IDs = wheels don't work
    // Use REV Hardware Client to see which motor is which
    public static final int kFrontLeftDrivingCanId = 2;   // SPIN motor (makes wheel go)
    public static final int kRearLeftDrivingCanId = 3;
    public static final int kFrontRightDrivingCanId = 6;
    public static final int kRearRightDrivingCanId = 7;

    public static final int kFrontLeftTurningCanId = 1;   // STEER motor (turns wheel)
    public static final int kRearLeftTurningCanId = 4;
    public static final int kFrontRightTurningCanId = 5;
    public static final int kRearRightTurningCanId = 8;

    // GYRO DIRECTION - Flip if robot turns backwards when you push forward
    // TUNE: Test driving - if backwards, change to true
    public static final boolean kGyroReversed = false;
    public static final int kRearLeftDrivingCanId = 3;
    public static final int kFrontRightDrivingCanId = 6;
    public static final int kRearRightDrivingCanId = 7;

    public static final int kFrontLeftTurningCanId = 1;
    public static final int kRearLeftTurningCanId = 4;
    public static final int kFrontRightTurningCanId = 5;
    public static final int kRearRightTurningCanId = 8;

    public static final boolean kGyroReversed = false;
  }

  // ===========================================
  // ⚙️ MOTOR GEAR MATH - Don't change unless you change gears!
  // ===========================================

  public static final class ModuleConstants {
    // GEAR TEETH - How many teeth on the motor pinion gear
    // TUNE: Count the teeth on your actual gear! 12T, 13T, or 14T changes speed
    public static final int kDrivingMotorPinionTeeth = 13;

    // MATH: Converts motor RPM to wheel speed
    // DON'T CHANGE: This is physics math for your specific gears
    public static final double kDrivingMotorFreeSpeedRps = NeoMotorConstants.kFreeSpeedRpm / 60;
    public static final double kWheelDiameterMeters = 0.0762;  // 3 inches
    public static final double kWheelCircumferenceMeters = kWheelDiameterMeters * Math.PI;
    // 45 teeth on the wheel's bevel gear, 22 teeth on the first-stage spur gear, 15
    // teeth on the bevel pinion
    public static final double kDrivingMotorReduction = (45.0 * 22) / (kDrivingMotorPinionTeeth * 15);
    public static final double kDriveWheelFreeSpeedRps = (kDrivingMotorFreeSpeedRps * kWheelCircumferenceMeters)
        / kDrivingMotorReduction;
  }

  // ===========================================
  // 🎯 FUEL SYSTEM - Intake, Indexer, Launcher!
  // ===========================================

  public static final class FuelConstants {
    // MOTOR IDs - Which controllers control which motors
    // TUNE: Check your robot wiring! Use REV Hardware Client
    public static final int LEFT_INTAKE_LAUNCHER_MOTOR_ID = 15;
    public static final int RIGHT_INTAKE_LAUNCHER_MOTOR_ID = 16;
    public static final int INDEXER_MOTOR_ID = 18;

    // CURRENT LIMITS - Prevents motors from breaking or overheating
    // TUNE: Start at 80, decrease if motors get too hot, increase if not powerful enough
    public static final int INDEXER_MOTOR_CURRENT_LIMIT = 80;   // Amps
    public static final int LAUNCHER_MOTOR_CURRENT_LIMIT = 80;

    // INDEXER SPEEDS - Moves fuel from intake to launcher
    // TUNE: Test with fuel - too slow = jams, too fast = unreliable
    public static final double INDEXER_INTAKING_PERCENT = -.8;   // Negative = reverse direction
    public static final double INDEXER_LAUNCHING_PERCENT = 0.6;  // Positive = forward
    public static final double INDEXER_SPIN_UP_PRE_LAUNCH_PERCENT = -0.5; // Slow reverse before launch

    // INTAKE SPEEDS - Sucks in fuel from ground
    // TUNE: Test picking up fuel - too slow = misses, too fast = throws fuel out
    public static final double INTAKE_INTAKING_PERCENT = 0.6;    // How fast to intake
    public static final double LAUNCHING_LAUNCHER_PERCENT = .85; // How fast launcher spins
    public static final double INTAKE_EJECT_PERCENT = -0.8;      // Reverse to spit out fuel

    // TIMING - How long to wait for launcher to spin up
    // TUNE: Test shooting - too short = shoots before ready, too long = slow
    public static final double SPIN_UP_SECONDS = 0.75;  // Seconds

    // LAUNCHER RPM - Default speeds for shooting
    // TUNE: Test distance vs speed, update the lookup table in TargetConstants
    public static final double kDefaultRPM = 3500;  // Starting RPM // TODO: THIS NEEDS TO BE TUNED!
    public static final double kMaxRPM = 5500;      // Maximum safe RPM
  }

  // ===========================================
  // 📷 VISION SYSTEM - Camera settings!
  // ===========================================

  public static final class VisionConstants {
    // CAMERA NAME - Must match PhotonVision app
    // TUNE: Check PhotonVision dashboard for exact name
    public static final String kCameraName = "Arducam_OV9281_USB_Camera";

    // CAMERA POSITION - Where is camera mounted on robot?
    // TUNE: Measure from robot center (front/back, left/right, up/down)
    // WRONG VALUES = Distance calculations will be wrong!
    public static final double kCameraXOffset = 0.25; // Forward from center (meters)
    public static final double kCameraYOffset = 0.0;  // Left/right from center
    public static final double kCameraZOffset = 0.5;  // Height above ground

    // CAMERA ANGLE - How is camera tilted? 
    // TUNE: Measure the angle your camera points (usually slightly up)
    public static final double kCameraPitch = Units.degreesToRadians(10); // 10 degrees up
    public static final double kCameraYaw = 0.0;   // Straight forward
    public static final double kCameraRoll = 0.0;  // Level

    // TARGET HEIGHT - How tall are the AprilTags?
    // TUNE: Official FRC field AprilTags are 1.83m tall
    public static final double kTargetHeight = 1.83; // Meters
  }

  // ===========================================
  // 🎯 TARGET CONSTANTS - AprilTag IDs and RPM table!
  // ===========================================

  public static final class TargetConstants {
    // APRILTAG IDs - Which tags are on red alliance vs blue
    // DON'T CHANGE: These are official FRC field tag IDs
    public static final int[] RED_TARGETS = { 8, 5, 9, 10, 11, 2 };
    public static final int[] BLUE_TARGETS = { 18, 27, 26, 25, 21, 24 };

    // ALL TARGETS - Combines red and blue for general use
    public static final int[] ALL_TARGETS = IntStream.concat(
        IntStream.of(RED_TARGETS),
        IntStream.of(BLUE_TARGETS)).toArray();

    // 🎯 RPM LOOKUP TABLE - Distance to launcher speed!
    // FORMAT: {Distance in meters, Launcher RPM}
    // TUNE: Test shooting at different distances, measure actual distance and RPM needed
    // Add more rows for better accuracy!
    public static final double[][] kRPMTable = {
        {0.5, 1500},  // 0.5m away = 1500 RPM
        {1.0, 2000},  // 1.0m away = 2000 RPM
        {1.5, 2500},  // 1.5m away = 2500 RPM
        {2.0, 3000},  // 2.0m away = 3000 RPM
        {2.5, 3500},  // 2.5m away = 3500 RPM
        {3.0, 4000},  // 3.0m away = 4000 RPM
        {3.5, 4500},  // 3.5m away = 4500 RPM
        {4.0, 5000},  // 4.0m away = 5000 RPM
        {4.5, 5500},  // 4.5m away = 5500 RPM
    };
  }

  // ===========================================
  // 🪜 CLIMBER CONSTANTS - Climbing settings!
  // ===========================================

  public static final class ClimbConstatns {
    // MOTOR ID - Which controller for climber
    // TUNE: Check wiring!
    public static final int CLIMBER_MOTOR_ID = 10;

    // CURRENT LIMIT - Prevents motor damage
    // TUNE: Start at 40, decrease if motors get too hot, increase if not powerful enough
    public static final int CLIMBER_MOTOR_CURRENT_LIMIT = 40;

    // CLIMB SPEEDS - How fast to climb up/down
    // TUNE: Test climbing - too slow = takes forever, too fast = dangerous
    public static final double CLIMBER_MOTOR_DOWN_PERCENT = -0.8;  // Negative = down
    public static final double CLIMBER_MOTOR_UP_PERCENT = 0.8;     // Positive = up
  }

  // ===========================================
  // 🎮 CONTROLLER SETTINGS - Driver controls!
  // ===========================================

  public static final class OperatorConstants {
    // CONTROLLER PORTS - Which USB ports on driver station
    // TUNE: Check driver station USB tab
    public static final int DRIVER_CONTROLLER_PORT = 0;
    public static final int OPERATOR_CONTROLLER_PORT = 1;
    public static final int FLIGHT_JOYSTICK_PORT = 3;

    // OLD SLEW RATES - Replaced by InputConstants (keep for reference)
    //public static final double XY_SLEW_RATE = 0.3;
    //public static final double ROT_SLEW_RATE = 0.3;

    // SPEED LIMITS - Overall speed caps for teleop
    // TUNE: 0.4 = 40% of max speed, good for safety
    public static final double SPEED_LIMIT = 0.4;        // Forward/back speed limit
    public static final double TURN_SPEED_LIMIT = 0.4;   // Turning speed limit
  }

  public static final class InputConstants {
    // DEADBAND - Ignores small joystick movements
    // TUNE: Too small = twitchy, too big = hard to control
    public static final double kJoystickDeadband = 0.15;  // 15% of full stick

    // EXPONENTS - Makes controls more precise at low speeds
    // TUNE: 2.0 = squared, gives better fine control
    public static final double kLinearExponent = 2.0;   // Forward/back precision
    public static final double kAngularExponent = 2.0;  // Turning precision

    // SLEW RATE LIMITS - Prevents jerky movements!
    // TUNE: Too small = sluggish, too big = jerky
    // ACCELERATION: How fast robot speeds up (smaller = smoother)
    // DECELERATION: How fast robot slows down (more negative = snappier stops)
    public static final double kLinearAccelerationLimit = 2.0;   // m/s² acceleration
    public static final double kLinearDecelerationLimit = -8.0;  // m/s² deceleration
    public static final double kAngularAccelerationLimit = 2.5;  // rad/s² turn acceleration
    public static final double kAngularDecelerationLimit = -8.0; // rad/s² turn deceleration

    // SLOW MODE - Precision driving multiplier
    // TUNE: 0.4 = 40% speed, adjust for how slow you want slow mode
    public static final double kSlowModeModifier = 0.4;
  }

  public static final class AutoConstants {
    public static final double kMaxSpeedMetersPerSecond = 3;
    public static final double kMaxAccelerationMetersPerSecondSquared = 3;
    public static final double kMaxAngularSpeedRadiansPerSecond = Math.PI;
    public static final double kMaxAngularSpeedRadiansPerSecondSquared = Math.PI;

    public static final double kPXController = 1;
    public static final double kPYController = 1;
    public static final double kPThetaController = 1;

    // Constraint for the motion profiled robot angle controller
    public static final TrapezoidProfile.Constraints kThetaControllerConstraints = new TrapezoidProfile.Constraints(
        kMaxAngularSpeedRadiansPerSecond, kMaxAngularSpeedRadiansPerSecondSquared);
  }

  public static final class NeoMotorConstants {
    public static final double kFreeSpeedRpm = 5676;
  }

  // ===========================================
  // 🎮 ROBOT TUNING GUIDE - How to make your robot awesome!
  // ===========================================
  /*
  🎯 QUICK TUNING CHECKLIST:

  DRIVING ISSUES:
  - Robot goes wrong way when pushing forward? Check kGyroReversed = true
  - Wheels turn wrong way? Check kFrontLeftChassisAngularOffset values
  - Robot doesn't drive straight? Measure kTrackWidth and kWheelBase exactly
  - Too jerky? Increase kLinearAccelerationLimit or decrease kLinearDecelerationLimit
  - Too sluggish? Decrease kLinearAccelerationLimit or increase kLinearDecelerationLimit

  SHOOTING ISSUES:
  - Fuel jams? Decrease INDEXER speeds, increase INTAKE speeds
  - Shots too weak? Increase LAUNCHER speeds or RPM values
  - Shots too strong? Decrease LAUNCHER speeds or RPM values
  - Wrong distance calculations? Measure camera position (kCameraXOffset, kCameraZOffset)

  CLIMBING ISSUES:
  - Climber too slow? Increase CLIMBER_MOTOR_UP_PERCENT
  - Climber too fast/dangerous? Decrease CLIMBER_MOTOR_UP_PERCENT
  - Motor overheating? Decrease CLIMBER_MOTOR_CURRENT_LIMIT

  CONTROLS ISSUES:
  - Controls too sensitive? Increase kJoystickDeadband
  - Hard to control precisely? Increase kLinearExponent/kAngularExponent
  - Jerky movements? Adjust slew rate limits

  GENERAL TIPS:
  - Always test changes in small increments (0.1 at a time)
  - Have someone watch the robot while you change values
  - Document what works for your robot!
  - Different battery levels affect performance - test with charged battery

  🔧 COMMON FIXES:
  - If robot spins when you push forward: kGyroReversed = true
  - If wheels don't turn correctly: Check angular offsets with REV Hardware Client
  - If shooting is inconsistent: Add more rows to kRPMTable with tested distances
  - If controls feel wrong: Start with kJoystickDeadband = 0.1, adjust up/down
  */
}
