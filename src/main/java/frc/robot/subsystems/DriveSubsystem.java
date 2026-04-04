// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

// These are like toolboxes full of math tools for robot movement
import edu.wpi.first.hal.FRCNetComm.tInstances;
import edu.wpi.first.hal.FRCNetComm.tResourceType;
import edu.wpi.first.hal.HAL;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.networktables.DoubleEntry;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import frc.robot.Constants.DriveConstants;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.config.PIDConstants;
import com.pathplanner.lib.config.RobotConfig;
import com.pathplanner.lib.controllers.PPHolonomicDriveController;

/**
 * DRIVE SUBSYSTEM - Makes the Robot Move!
 *
 * This is like the "engine" of your robot. It controls all 4 wheels that can drive
 * in any direction. Think of it like a car that can drive forwards, backwards,
 * sideways, and spin all at the same time!
 *
 * REAL LIFE EXAMPLE: Imagine a shopping cart that can roll in any direction
 * and turn its wheels while moving. That's what swerve drive does!
 */
public class DriveSubsystem extends SubsystemBase {

  // ===========================================
  // THE 4 SWERVE WHEELS (like 4 mini-cars!)
  // ===========================================

  // Each wheel can SPIN (drive motor) and TURN (steer motor)
  // Front-Left wheel - top left of robot
  private final MAXSwerveModule m_frontLeft = new MAXSwerveModule(
      DriveConstants.kFrontLeftDrivingCanId,    // SPIN motor ID
      DriveConstants.kFrontLeftTurningCanId,    // TURN motor ID
      DriveConstants.kFrontLeftChassisAngularOffset); // Angle offset (calibration)

  // Front-Right wheel - top right of robot
  private final MAXSwerveModule m_frontRight = new MAXSwerveModule(
      DriveConstants.kFrontRightDrivingCanId,
      DriveConstants.kFrontRightTurningCanId,
      DriveConstants.kFrontRightChassisAngularOffset);

  // Back-Left wheel - bottom left of robot
  private final MAXSwerveModule m_rearLeft = new MAXSwerveModule(
      DriveConstants.kRearLeftDrivingCanId,
      DriveConstants.kRearLeftTurningCanId,
      DriveConstants.kBackLeftChassisAngularOffset);

  // Back-Right wheel - bottom right of robot
  private final MAXSwerveModule m_rearRight = new MAXSwerveModule(
      DriveConstants.kRearRightDrivingCanId,
      DriveConstants.kRearRightTurningCanId,
      DriveConstants.kBackRightChassisAngularOffset);

  // ===========================================
  // THE GYRO (knows which way is forward!)
  // ===========================================

  // Gets direction data from the Raspberry Pi coprocessor
  // This is like a compass that tells us which way the robot is facing
  private final NetworkTable m_senseHatTable = NetworkTableInstance.getDefault().getTable("SenseHat");
  private final DoubleEntry m_yawEntry = m_senseHatTable.getDoubleTopic("yaw").getEntry(0.0);     // Current direction
  private final DoubleEntry m_rateEntry = m_senseHatTable.getDoubleTopic("rate_z").getEntry(0.0); // How fast we're spinning

  // ===========================================
  // ODOMETRY (tracks where we are on the field!)
  // ===========================================

  // This is like GPS for the robot - it calculates position using wheel movement + gyro
  // REAL LIFE EXAMPLE: Like tracking your position in a video game using footsteps + compass
  SwerveDriveOdometry m_odometry = new SwerveDriveOdometry(
      DriveConstants.kDriveKinematics,           // Math for wheel positions
      Rotation2d.fromDegrees(m_yawEntry.get()),  // Current direction
      new SwerveModulePosition[] {               // Starting positions of all wheels
          m_frontLeft.getPosition(),
          m_frontRight.getPosition(),
          m_rearLeft.getPosition(),
          m_rearRight.getPosition()
      });

  // ===========================================
  // CONSTRUCTOR - Sets up the robot when it starts
  // ===========================================

  /** Creates a new DriveSubsystem. */
  public DriveSubsystem() {
    // Tell WPILib we're using swerve drive (for stats)
    HAL.report(tResourceType.kResourceType_RobotDrive, tInstances.kRobotDriveSwerve_MaxSwerve);

    // ===========================================
    // SET UP AUTONOMOUS DRIVING (PathPlanner)
    // ===========================================

    // PathPlanner helps the robot drive automatically during autonomous
    // REAL LIFE EXAMPLE: Like a GPS navigation system for the robot
    try {
      // Load robot configuration from PathPlanner app
      RobotConfig config = RobotConfig.fromGUISettings();

      // Configure PathPlanner to work with our robot
      AutoBuilder.configure(
          this::getPose,                                        // How to get current position
          this::resetOdometry,                                  // How to reset position
          this::getRobotRelativeSpeeds,                         // How to get current speeds
          (speeds, feedforwards) -> driveRobotRelative(speeds), // How to drive
          new PPHolonomicDriveController(                       // Controller for smooth movement
              new PIDConstants(5.0, 0.0, 0.0),                  // Translation PID (straight line movement)
              new PIDConstants(5.0, 0.0, 0.0)                   // Rotation PID (turning)
          ),
          config,                           // Robot configuration
          () -> {                           // Which alliance we're on
            var alliance = DriverStation.getAlliance();
            if (alliance.isPresent()) {
              return alliance.get() == DriverStation.Alliance.Red;
            }
            return false;
          },
          this);                            // This subsystem
    } catch (Exception e) {
      // If PathPlanner setup fails, show error but don't crash
      DriverStation.reportError("Failed to load PathPlanner config", e.getStackTrace());
    }
  }

  // ===========================================
  // PERIODIC - Runs every 20ms (50 times per second!)
  // ===========================================

  @Override
  public void periodic() {
    // Update the odometry in the periodic block
    m_odometry.update(
        Rotation2d.fromDegrees(m_yawEntry.get()),
        new SwerveModulePosition[] {
            m_frontLeft.getPosition(),
            m_frontRight.getPosition(),
            m_rearLeft.getPosition(),
            m_rearRight.getPosition()
        });
  }

  // ===========================================
  // GET POSITION METHODS
  // ===========================================

  /**
   * Returns the currently-estimated pose of the robot.
   * POSE = Position + Orientation (where + which way facing)
   *
   * REAL LIFE EXAMPLE: "I'm at the kitchen table, facing the fridge"
   * @return The pose (position and direction).
   */
  public Pose2d getPose() {
    return m_odometry.getPoseMeters();
  }

  /**
   * Resets the odometry to the specified pose.
   * This is like resetting your GPS to a new starting point
   *
   * REAL LIFE EXAMPLE: Telling your phone "I'm actually at the park now"
   * @param pose The pose to which to set the odometry.
   */
  public void resetOdometry(Pose2d pose) {
    m_odometry.resetPosition(
        Rotation2d.fromDegrees(m_yawEntry.get()),
        new SwerveModulePosition[] {
            m_frontLeft.getPosition(),
            m_frontRight.getPosition(),
            m_rearLeft.getPosition(),
            m_rearRight.getPosition()
        },
        pose);
  }

  // ===========================================
  // MAIN DRIVE METHODS
  // ===========================================

  /**
   * Method to drive the robot using ChassisSpeeds.
   * ChassisSpeeds = how fast robot moves forward/back, left/right, and spins
   *
   * @param speeds        The desired chassis speeds (forward, sideways, spin).
   * @param fieldRelative Whether speeds are relative to field (true) or robot (false).
   *
   * REAL LIFE EXAMPLE:
   * fieldRelative=true: "Drive north at 2 mph" (always same direction on field)
   * fieldRelative=false: "Drive forward from my perspective" (changes as I turn)
   */
  public void drive(ChassisSpeeds speeds, boolean fieldRelative) {
    // If field-relative, convert speeds to robot-relative
    // REAL LIFE EXAMPLE: Converting "north" to "my current facing direction"
    if (fieldRelative) {
      speeds = ChassisSpeeds.fromFieldRelativeSpeeds(speeds, getPose().getRotation());
    }

    // Convert chassis speeds to individual wheel speeds/angles
    // MATH MAGIC: Figures out what each wheel should do
    var swerveModuleStates = DriveConstants.kDriveKinematics.toSwerveModuleStates(speeds);

    // Make sure no wheel goes faster than max speed (like speed limit)
    SwerveDriveKinematics.desaturateWheelSpeeds(
        swerveModuleStates, DriveConstants.kMaxSpeedMetersPerSecond);

    // Tell each wheel what to do
    m_frontLeft.setDesiredState(swerveModuleStates[0]);
    m_frontRight.setDesiredState(swerveModuleStates[1]);
    m_rearLeft.setDesiredState(swerveModuleStates[2]);
    m_rearRight.setDesiredState(swerveModuleStates[3]);
  }

  /**
   * Method to drive the robot using joystick info.
   * This is the most common way we drive - using controller inputs
   *
   * @param xSpeed        Speed forward/back (-1 to 1 from joystick)
   * @param ySpeed        Speed left/right (-1 to 1 from joystick)
   * @param rot           Rotation speed (-1 to 1 from joystick)
   * @param fieldRelative Whether speeds are relative to field or robot
   *
   * REAL LIFE EXAMPLE: Left joystick Y = forward/back, X = strafe, right X = spin
   */
  public void drive(double xSpeed, double ySpeed, double rot, boolean fieldRelative) {
    // Convert joystick values (-1 to 1) to actual speeds
    // Joystick says "half speed forward" → convert to meters per second
    double xSpeedDelivered = xSpeed * DriveConstants.kMaxSpeedMetersPerSecond; 
    double ySpeedDelivered = ySpeed * DriveConstants.kMaxSpeedMetersPerSecond; 
    double rotDelivered = rot * DriveConstants.kMaxAngularSpeed; 

    // Calculate what each wheel should do
    var swerveModuleStates = DriveConstants.kDriveKinematics.toSwerveModuleStates(
        fieldRelative
            ? ChassisSpeeds.fromFieldRelativeSpeeds(xSpeedDelivered, ySpeedDelivered, rotDelivered,
                getPose().getRotation())  // Field-relative: always same direction on field
            : new ChassisSpeeds(xSpeedDelivered, ySpeedDelivered, rotDelivered)); // Robot-relative

    // Prevent wheels from going too fast
    SwerveDriveKinematics.desaturateWheelSpeeds(
        swerveModuleStates, DriveConstants.kMaxSpeedMetersPerSecond);

    // Send commands to wheels
    m_frontLeft.setDesiredState(swerveModuleStates[0]);
    m_frontRight.setDesiredState(swerveModuleStates[1]);
    m_rearLeft.setDesiredState(swerveModuleStates[2]);
    m_rearRight.setDesiredState(swerveModuleStates[3]);
  }

  /**
   * Method to drive the robot relative to itself.
   * Used by PathPlanner for autonomous driving
   *
   * @param speeds Robot-relative chassis speeds.
   *
   * REAL LIFE EXAMPLE: "Drive forward 2 m/s from my current direction"
   */
  public void driveRobotRelative(ChassisSpeeds speeds) {
    // Convert from actual speeds back to joystick-style values (-1 to 1)
    drive(speeds.vxMetersPerSecond / DriveConstants.kMaxSpeedMetersPerSecond,
        speeds.vyMetersPerSecond / DriveConstants.kMaxSpeedMetersPerSecond,
        speeds.omegaRadiansPerSecond / DriveConstants.kMaxAngularSpeed,
        false); // Always robot-relative
  }

  /**
   * Returns the current robot-relative chassis speeds.
   * Used by PathPlanner to know how fast we're currently going
   *
   * @return Robot-relative chassis speeds.
   */
  public ChassisSpeeds getRobotRelativeSpeeds() {
    // Ask each wheel how fast it's spinning and which way it's pointed
    return DriveConstants.kDriveKinematics.toChassisSpeeds(
        m_frontLeft.getState(),
        m_frontRight.getState(),
        m_rearLeft.getState(),
        m_rearRight.getState());
  }

  // ===========================================
  // SPECIAL DRIVE METHODS
  // ===========================================

  /**
   * Sets the wheels into an X formation to prevent movement.
   * PARKING BRAKE MODE!
   *
   * REAL LIFE EXAMPLE: Like parking brakes on a car - wheels at 45-degree angles
   * so robot can't roll away even on a ramp
   */
  public void setX() {
    m_frontLeft.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(45)));
    m_frontRight.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(-45)));
    m_rearLeft.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(-45)));
    m_rearRight.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(45)));
  }

  /**
   * Sets the swerve ModuleStates.
   * Advanced method - tells each wheel exactly what to do
   *
   * @param desiredStates The desired SwerveModule states.
   */
  public void setModuleStates(SwerveModuleState[] desiredStates) {
    SwerveDriveKinematics.desaturateWheelSpeeds(
        desiredStates, DriveConstants.kMaxSpeedMetersPerSecond);
    m_frontLeft.setDesiredState(desiredStates[0]);
    m_frontRight.setDesiredState(desiredStates[1]);
    m_rearLeft.setDesiredState(desiredStates[2]);
    m_rearRight.setDesiredState(desiredStates[3]);
  }

  // ===========================================
  // UTILITY METHODS
  // ===========================================

  /** Resets the drive encoders to currently read a position of 0. */
  public void resetEncoders() {
    m_frontLeft.resetEncoders();
    m_rearLeft.resetEncoders();
    m_frontRight.resetEncoders();
    m_rearRight.resetEncoders();
  }

  /** Zeroes the heading of the robot. */
  public void zeroHeading() {
    // Note: This only zeroes the internal odometry rotation,
    // it doesn't reset the physical gyro on the Pi.
    resetOdometry(new Pose2d(getPose().getTranslation(), new Rotation2d()));
  }

  // ===========================================
  // GETTER METHODS (for getting information)
  // ===========================================

  /**
   * Returns the max linear speed of the robot.
   * Used by InputSubsystem to know speed limits
   *
   * @return The max linear speed in meters per second.
   */
  public double getMaxLinearSpeedMetersPerSec() {
    return DriveConstants.kMaxSpeedMetersPerSecond;
  }

  /**
   * Returns the max angular speed of the robot.
   * Used by InputSubsystem for turning limits
   *
   * @return The max angular speed in radians per second.
   */
  public double getMaxAngularSpeedRadPerSec() {
    return DriveConstants.kMaxAngularSpeed;
  }

  /**
   * Returns the heading of the robot.
   * Which direction are we facing?
   *
   * @return the robot's heading in degrees, from -180 to 180
   */
  public double getHeading() {
    return getPose().getRotation().getDegrees();
  }

  /**
   * Returns the turn rate of the robot.
   * How fast are we spinning?
   *
   * @return The turn rate of the robot, in degrees per second
   */
  public double getTurnRate() {
    return m_rateEntry.get() * (DriveConstants.kGyroReversed ? -1.0 : 1.0);
  }

  // ===========================================
  // 🎮 DRIVING TUNING GUIDE 🎮
  // ===========================================
  //
  // HOW TO TUNE YOUR ROBOT'S DRIVING FEEL
  // Follow these steps to make driving smooth and fun!
  //
  // STEP 1: MEASURE YOUR ROBOT
  // Go to Constants.java → DriveConstants class:
  // - Measure wheelbase (front wheels to back wheels center-to-center)
  // - Measure track width (left wheels to right wheels center-to-center)
  // - Update kTrackWidth and kWheelBase with YOUR measurements
  //
  // STEP 2: SET MAX SPEEDS
  // Test your robot safely and find these values:
  // - kMaxSpeedMetersPerSecond: Fastest it can go straight (test on smooth floor)
  // - kMaxAngularSpeed: Fastest it can spin (test turning in place)
  // - Start with 3.0 m/s and 4.0 rad/s, adjust based on testing
  //
  // STEP 3: TUNE MOTOR IDs
  // Check that these match your robot's wiring:
  // - kFrontLeftDrivingCanId = 2 (spin motor)
  // - kFrontLeftTurningCanId = 1 (steer motor)
  // - And so on for all 4 modules
  // - Wrong IDs = wheels don't work!
  //
  // STEP 4: CALIBRATE WHEEL ANGLES
  // Each wheel has an "angular offset" - the angle when it's pointing straight forward
  // - Use the REV Hardware Client to find these values
  // - Update kFrontLeftChassisAngularOffset etc.
  //
  // STEP 5: TEST FIELD-ORIENTED DRIVING
  // - Drive with fieldRelative = true
  // - Push joystick forward - should always go toward driver stations
  // - If not, check gyro direction (kGyroReversed)
  //
  // REAL LIFE EXAMPLES:
  // - Too fast? Lower kMaxSpeedMetersPerSecond
  // - Jerky turning? The angular offsets might be wrong
  // - Drifts sideways? Check wheel angles are calibrated
  // - Spins wrong way? Try kGyroReversed = true
  //
  // PRO TIPS:
  // - Always test on smooth floor first
  // - Have someone watch wheels while driving
  // - Use slow mode (bumper) for testing
  // - Check dashboard for odometry position
  //
  // ===========================================
}
