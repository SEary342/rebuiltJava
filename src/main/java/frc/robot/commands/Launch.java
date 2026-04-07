// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.CANFuelSubsystem;
import static frc.robot.Constants.FuelConstants.*;

import java.util.function.DoubleSupplier;

public class Launch extends Command {

  private final CANFuelSubsystem fuelSubsystem;
  private final DoubleSupplier rpmSupplier;

  /**
   * Launch command that uses a dynamic RPM.
   * @param fuelSystem The subsystem.
   * @param rpmSupplier A supplier for the target RPM.
   */
  public Launch(CANFuelSubsystem fuelSystem, DoubleSupplier rpmSupplier) {
    this.fuelSubsystem = fuelSystem;
   
    // SME20260407: If the camera isn't connected, then we end up with zero as the rpm supplier, which causes the launcher to
    //  not ramp.  stop. To prevent this, we can set a minimum RPM based on our RPM table.
    if (rpmSupplier<kRPMTable[0])
    {
      rpmSupplier = kRPMTable[0]; // set to minimum as this could be that we are too close but also this is a safe value. 
    }
    this.rpmSupplier = rpmSupplier;
    addRequirements(fuelSystem);
  }

  /**
   * Launch command that uses the default RPM.
   */
  public Launch(CANFuelSubsystem fuelSystem) {
    this(fuelSystem, () -> kDefaultRPM);
  }

  @Override
  public void initialize() {
    // Set launcher to target RPM
    fuelSubsystem.setLauncherRPM(rpmSupplier.getAsDouble());
    
    // Set feeder roller to launching speed
    fuelSubsystem.setFeederRoller(INDEXER_LAUNCHING_PERCENT);
  }

  @Override
  public void execute() {
    // Continuously update RPM in case the supplier changes (e.g. distance changes)
    fuelSubsystem.setLauncherRPM(rpmSupplier.getAsDouble());
  }

  @Override
  public void end(boolean interrupted) {
    // Rollers stopped by default command or explicitly here if needed
    // But usually we want them to stop when the command ends
    fuelSubsystem.stop();
  }

  @Override
  public boolean isFinished() {
    return false;
  }
}
