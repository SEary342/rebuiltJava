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
 * The Constants class provides a convenient place for teams to hold robot-wide
 * numerical or boolean constants. This class should not be used for any other
 * purpose. All constants should be declared globally (i.e. public static). Do
 * not put anything functional in this class.
 *
 * <p>
 * It is advised to statically import this class (or one of its inner classes)
 * wherever the constants are needed, to reduce verbosity.
 */
public final class Constants {

  public static final class DriveConstants {
    // Driving Parameters - Note that these are not the maximum capable speeds of
    // the robot, rather the allowed maximum speeds
    public static final double kMaxSpeedMetersPerSecond = 1.5;  //4.8;/1 did nothing TODO figure out what this is/ 
    public static final double kMaxAngularSpeed = 2 * Math.PI; // radians per second

    // Chassis configuration
    public static final double kTrackWidth = Units.inchesToMeters(26.5);
    // Distance between centers of right and left wheels on robot
    public static final double kWheelBase = Units.inchesToMeters(26.5);
    // Distance between front and back wheels on robot
    public static final SwerveDriveKinematics kDriveKinematics = new SwerveDriveKinematics(
        new Translation2d(kWheelBase / 2, kTrackWidth / 2),
        new Translation2d(kWheelBase / 2, -kTrackWidth / 2),
        new Translation2d(-kWheelBase / 2, kTrackWidth / 2),
        new Translation2d(-kWheelBase / 2, -kTrackWidth / 2));

    // Angular offsets of the modules relative to the chassis in radians
    public static final double kFrontLeftChassisAngularOffset = -Math.PI / 2;
    public static final double kFrontRightChassisAngularOffset = 0;
    public static final double kBackLeftChassisAngularOffset = Math.PI;
    public static final double kBackRightChassisAngularOffset = Math.PI / 2;

    // SPARK MAX CAN IDs
    public static final int kFrontLeftDrivingCanId = 2;
    public static final int kRearLeftDrivingCanId = 3;
    public static final int kFrontRightDrivingCanId = 6;
    public static final int kRearRightDrivingCanId = 7;

    public static final int kFrontLeftTurningCanId = 1;
    public static final int kRearLeftTurningCanId = 4;
    public static final int kFrontRightTurningCanId = 5;
    public static final int kRearRightTurningCanId = 8;

    public static final boolean kGyroReversed = false;
  }

  public static final class ModuleConstants {
    // The MAXSwerve module can be configured with one of three pinion gears: 12T,
    // 13T, or 14T. This changes the drive speed of the module (a pinion gear with
    // more teeth will result in a robot that drives faster).
    public static final int kDrivingMotorPinionTeeth = 13;

    // Calculations required for driving motor conversion factors and feed forward
    public static final double kDrivingMotorFreeSpeedRps = NeoMotorConstants.kFreeSpeedRpm / 60;
    public static final double kWheelDiameterMeters = 0.0762;
    public static final double kWheelCircumferenceMeters = kWheelDiameterMeters * Math.PI;
    // 45 teeth on the wheel's bevel gear, 22 teeth on the first-stage spur gear, 15
    // teeth on the bevel pinion
    public static final double kDrivingMotorReduction = (45.0 * 22) / (kDrivingMotorPinionTeeth * 15);
    public static final double kDriveWheelFreeSpeedRps = (kDrivingMotorFreeSpeedRps * kWheelCircumferenceMeters)
        / kDrivingMotorReduction;
  }

  public static final class FuelConstants {
    // Motor controller IDs for Fuel Mechanism motors
    // TODO change the motor ids on the superstructure
    public static final int LEFT_INTAKE_LAUNCHER_MOTOR_ID = 15;
    public static final int RIGHT_INTAKE_LAUNCHER_MOTOR_ID = 16;
    public static final int INDEXER_MOTOR_ID = 18;

    // Current limit for fuel mechanism motors.
    public static final int INDEXER_MOTOR_CURRENT_LIMIT = 80;
    public static final int LAUNCHER_MOTOR_CURRENT_LIMIT = 80;

    // All values likely need to be tuned based on your robot
    public static final double INDEXER_INTAKING_PERCENT = -.8;
    public static final double INDEXER_LAUNCHING_PERCENT = 0.6;
    public static final double INDEXER_SPIN_UP_PRE_LAUNCH_PERCENT = -0.5;

    public static final double INTAKE_INTAKING_PERCENT = 0.6;
    public static final double LAUNCHING_LAUNCHER_PERCENT = .85;
    public static final double INTAKE_EJECT_PERCENT = -0.8;

    public static final double SPIN_UP_SECONDS = 0.75;

    public static final double kDefaultRPM = 3500;
    public static final double kMaxRPM = 5500;
  }

  public static final class VisionConstants {
    public static final String kCameraName = "Arducam_OV9281_USB_Camera";

    // Camera position relative to robot center (meters)
    public static final double kCameraXOffset = 0.25; // Example: 25cm forward from center
    public static final double kCameraYOffset = 0.0;
    public static final double kCameraZOffset = 0.5; // Example: 50cm above ground

    // Camera mounting angle (radians)
    // 80 degrees mounting plate (10 degrees off horizontal, pointing up)
    public static final double kCameraPitch = Units.degreesToRadians(10);
    public static final double kCameraYaw = 0.0;
    public static final double kCameraRoll = 0.0;

    // Target height (meters)
    public static final double kTargetHeight = 1.83;
  }

  public static final class TargetConstants {
    public static final int[] RED_TARGETS = { 8, 5, 9, 10, 11, 2 };
    public static final int[] BLUE_TARGETS = { 18, 27, 26, 25, 21, 24 };

    // Merges both arrays into a single constant
    public static final int[] ALL_TARGETS = IntStream.concat(
        IntStream.of(RED_TARGETS),
        IntStream.of(BLUE_TARGETS)).toArray();

    // RPM Lookup Table: {Distance in Meters, Launcher RPM}
    // This will be calibrated on the final robot.
    public static final double[][] kRPMTable = {
        {0.5, 1500},
        {1.0, 2000},
        {1.5, 2500},
        {2.0, 3000},
        {2.5, 3500},
        {3.0, 4000},
        {3.5, 4500},
        {4.0, 5000},
        {4.5, 5500},
    };
  }

  public static final class ClimbConstatns {
    // Motor controller IDs for Climb motor
    public static final int CLIMBER_MOTOR_ID = 10;

    // Current limit for climb motor
    public static final int CLIMBER_MOTOR_CURRENT_LIMIT = 40;
    // Percentage to power the motor both up and down
    public static final double CLIMBER_MOTOR_DOWN_PERCENT = -0.8;
    public static final double CLIMBER_MOTOR_UP_PERCENT = 0.8;
  }

  public static final class OperatorConstants {

    // Port constants for driver and operator controllers. These should match the
    // values in the Joystick tab of the Driver Station software
    public static final int DRIVER_CONTROLLER_PORT = 0;
    public static final int OPERATOR_CONTROLLER_PORT = 1;
    public static final double kDriveDeadband = 0.05;
    public static final double XY_SLEW_RATE = 0.3;
    public static final double ROT_SLEW_RATE = 0.3;
    public static final double SPEED_LIMIT = 0.25;//0.4 default, 0.1 tooo slow //0.25 is closer
    public static final double TURN_SPEED_LIMIT = 0.01;
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
}
