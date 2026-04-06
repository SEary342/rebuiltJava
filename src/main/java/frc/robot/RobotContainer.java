// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import static frc.robot.Constants.OperatorConstants.*;

import frc.robot.Constants.OperatorConstants;
import frc.robot.Constants.TargetConstants;
import frc.robot.commands.AimAtTarget;
import frc.robot.commands.ClimbDown;
import frc.robot.commands.ClimbUp;
import frc.robot.commands.Eject;
import frc.robot.commands.Intake;
import frc.robot.commands.LaunchSequence;
import frc.robot.commands.SubwooferShoot;
import frc.robot.subsystems.CANFuelSubsystem;
import frc.robot.subsystems.ClimberSubsystem;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.VisionSubsystem;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a "declarative" paradigm, very little robot logic should
 * actually be handled in the {@link Robot} periodic methods (other than the
 * scheduler calls). Instead, the structure of the robot (including subsystems,
 * commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems
  private final DriveSubsystem driveSubsystem = new DriveSubsystem();
  private final CANFuelSubsystem fuelSubsystem = new CANFuelSubsystem();
  private final ClimberSubsystem climberSubsystem = new ClimberSubsystem();
  private final VisionSubsystem visionSubsystem = new VisionSubsystem();

  // Launcher State
  private boolean isVisionRPMEnabled = true;
  private int manualRPMIndex = 0; // Index into TargetConstants.kRPMTable

  // The driver's controller
  private final CommandXboxController driverController = new CommandXboxController(
      DRIVER_CONTROLLER_PORT);

  // The operator's controller
  private final CommandXboxController operatorController = new CommandXboxController(
      OPERATOR_CONTROLLER_PORT);
  private final CommandXboxController[] controllers = { driverController, operatorController };

  private final SlewRateLimiter xLimiter = new SlewRateLimiter(XY_SLEW_RATE);
  private final SlewRateLimiter yLimiter = new SlewRateLimiter(XY_SLEW_RATE);
  private final SlewRateLimiter rotLimiter = new SlewRateLimiter(ROT_SLEW_RATE);

  // The autonomous chooser
  private final SendableChooser<Command> autoChooser;

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
    // Register Named Commands
    NamedCommands.registerCommand("SubwooferShoot", new SubwooferShoot(fuelSubsystem));
    NamedCommands.registerCommand("Intake", new Intake(fuelSubsystem).withTimeout((5)));
    NamedCommands.registerCommand("Eject", new Eject(fuelSubsystem).withTimeout(1));
    NamedCommands.registerCommand("AimAtTarget", new AimAtTarget(driveSubsystem, visionSubsystem).withTimeout(2));
    NamedCommands.registerCommand("LaunchSequence", new LaunchSequence(fuelSubsystem, this::getTargetRPM));

    configureBindings();

    // Set the options to show up in the Dashboard for selecting auto modes.
    autoChooser = AutoBuilder.buildAutoChooser("AutoStart");
    SmartDashboard.putData("Auto Chooser", autoChooser);
  }

  /**
   * Helper to get the currently selected launcher RPM based on toggle state.
   */
  private double getTargetRPM() {
    if (isVisionRPMEnabled) {
      return visionSubsystem.getLauncherRPM(visionSubsystem.getBestTarget());
    } else {
      return TargetConstants.kRPMTable[manualRPMIndex][1];
    }
  }

  /**
   * Helper to change the manual RPM index and update state/dashboard.
   */
  private void changeManualRPM(int delta) {
    manualRPMIndex = MathUtil.clamp(manualRPMIndex + delta, 0, TargetConstants.kRPMTable.length - 1);
    isVisionRPMEnabled = false; // Switch to manual if adjusting
    updateLauncherDashboard();
  }

  /**
   * Updates launcher-related telemetry on the SmartDashboard.
   */
  private void updateLauncherDashboard() {
    SmartDashboard.putBoolean("Launcher/VisionModeEnabled", isVisionRPMEnabled);
    SmartDashboard.putNumber("Launcher/ManualRPMIndex", manualRPMIndex);
    SmartDashboard.putNumber("Launcher/ManualRPMValue", TargetConstants.kRPMTable[manualRPMIndex][1]);
  }

  /**
   * Use this method to define your trigger->command mappings.
   */
  private void configureBindings() {
    // --- Shared Controller Bindings (Driver + Operator) ---
    for (CommandXboxController controller : controllers) {
      // While the left bumper is held, intake Fuel
      controller.leftBumper().whileTrue(new Intake(fuelSubsystem));

      // While the A button is held, eject fuel back out the intake
      controller.a().whileTrue(new Eject(fuelSubsystem));

      // While the down arrow on the directional pad is held it will unclimb the robot
      controller.povDown().whileTrue(new ClimbDown(climberSubsystem));

      // While the up arrow on the directional pad is held it will cimb the robot
      controller.povUp().whileTrue(new ClimbUp(climberSubsystem));

      // Right Bumper: Launch Sequence using the current RPM (Vision or Manual)
      controller.rightBumper().whileTrue(new LaunchSequence(fuelSubsystem, this::getTargetRPM));

      // Y Button: Toggle between Vision-calculated RPM and Manual RPM
      controller.y().onTrue(new InstantCommand(() -> {
        isVisionRPMEnabled = !isVisionRPMEnabled;
        updateLauncherDashboard();
      }));

      // POV Left/Right: Cycle through manual RPM lookup table values
      controller.povLeft().onTrue(new InstantCommand(() -> changeManualRPM(-1)));
      controller.povRight().onTrue(new InstantCommand(() -> changeManualRPM(1)));
    }

    // --- Driver Specific Bindings (Logitech F310) ---

    // X Button: Aim at target (rotate robot)
    driverController.x().whileTrue(new AimAtTarget(driveSubsystem, visionSubsystem));

    // Left Trigger: Parking Brakes (Set modules to X)
    driverController.b().whileTrue(new RunCommand(
        () -> driveSubsystem.setX(),
        driveSubsystem));

    // Start Button: Zero out Gyro
    driverController.start().onTrue(new InstantCommand(
        () -> driveSubsystem.zeroHeading(),
        driveSubsystem));

    // --- Default Commands ---

    driveSubsystem.setDefaultCommand(new RunCommand(
        () -> {
          // --- 1. GET & DEADBAND ---
          double yInput = -MathUtil.applyDeadband(driverController.getLeftY(), OperatorConstants.kDriveDeadband);
          double xInput = -MathUtil.applyDeadband(driverController.getLeftX(), OperatorConstants.kDriveDeadband);
          double rotInput = -MathUtil.applyDeadband(driverController.getRightX(), OperatorConstants.kDriveDeadband);

          // --- 2. SQUARE THE INPUTS ---
          // Math.copySign preserves the direction (+ or -) after squaring
          yInput = Math.copySign(yInput * yInput, yInput);
          xInput = Math.copySign(xInput * xInput, xInput);
          rotInput = Math.copySign(rotInput * rotInput, rotInput);

          // --- 3. APPLY SPEED LIMIT ---
          yInput *= SPEED_LIMIT;
          xInput *= SPEED_LIMIT;
          rotInput *= SPEED_LIMIT;

          // --- 4. TRIGGER SLOW TURN ---
          double slowTurn = (driverController.getLeftTriggerAxis() - driverController.getRightTriggerAxis())
              * TURN_SPEED_LIMIT;
          double combinedRot = MathUtil.clamp(rotInput + slowTurn, -1.0, 1.0);

          // --- 5. LIMIT & DRIVE ---
          // Try Slew Rates between 1.5 and 2.5 now that inputs are squared
          driveSubsystem.drive(
              yLimiter.calculate(yInput),
              xLimiter.calculate(xInput),
              rotLimiter.calculate(combinedRot),
              true);
        },
        driveSubsystem));

    fuelSubsystem.setDefaultCommand(fuelSubsystem.run(() -> fuelSubsystem.stop()));

    climberSubsystem.setDefaultCommand(climberSubsystem.run(() -> climberSubsystem.stop()));

    // Initial Dashboard states
    updateLauncherDashboard();
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    return autoChooser.getSelected();
  }

  public DriveSubsystem getDriveSubsystem() {
    return driveSubsystem;
  }
}
