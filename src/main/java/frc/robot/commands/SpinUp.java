// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.CANFuelSubsystem;
import static frc.robot.Constants.FuelConstants.*;
import static frc.robot.Constants.TargetConstants.kRPMTable;

import java.util.function.DoubleSupplier;

public class SpinUp extends Command {

  private final CANFuelSubsystem fuelSubsystem;
  private final DoubleSupplier rpmSupplier;

  /**
   * SpinUp command that uses a dynamic RPM.
   * 
   * @param fuelSystem  The subsystem.
   * @param rpmSupplier A supplier for the target RPM.
   */
  public SpinUp(CANFuelSubsystem fuelSystem, DoubleSupplier rpmSupplier) {
    this.fuelSubsystem = fuelSystem;
    final double minRPM = kRPMTable[0][1];

    // SME20260407: If the camera isn't connected, then we end up with zero as the
    // rpm supplier, which causes the launcher to not ramp. stop. To prevent this,
    // we can set a minimum RPM based on our RPM table.
    this.rpmSupplier = () -> Math.max(rpmSupplier.getAsDouble(), minRPM);

    addRequirements(fuelSystem);
  }

  /**
   * SpinUp command that uses the default RPM.
   */
  public SpinUp(CANFuelSubsystem fuelSystem) {
    this(fuelSystem, () -> kDefaultRPM);
  }

  @Override
  public void initialize() {
    // Set launcher to target RPM
    fuelSubsystem.setLauncherRPM(rpmSupplier.getAsDouble());

    // Set feeder roller to spin-up speed (usually negative to keep ball back)
    fuelSubsystem.setFeederRoller(INDEXER_SPIN_UP_PRE_LAUNCH_PERCENT);
  }

  @Override
  public void execute() {
    // Continuously update target RPM
    fuelSubsystem.setLauncherRPM(rpmSupplier.getAsDouble());
  }

  @Override
  public void end(boolean interrupted) {
    // If interrupted, we don't necessarily want to stop()
    // because this command is often followed by Launch
  }

  @Override
  public boolean isFinished() {
    // This command is meant to be used with .withTimeout()
    // or as part of a SequentialCommandGroup
    return false;
  }
}
