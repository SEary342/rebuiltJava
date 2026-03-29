// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.CANFuelSubsystem;
import static frc.robot.Constants.FuelConstants.*;

import java.util.function.DoubleSupplier;

public class SpinUp extends Command {

  private final CANFuelSubsystem fuelSubsystem;
  private final DoubleSupplier rpmSupplier;

  /**
   * SpinUp command that uses a dynamic RPM.
   * @param fuelSystem The subsystem.
   * @param rpmSupplier A supplier for the target RPM.
   */
  public SpinUp(CANFuelSubsystem fuelSystem, DoubleSupplier rpmSupplier) {
    this.fuelSubsystem = fuelSystem;
    this.rpmSupplier = rpmSupplier;
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
