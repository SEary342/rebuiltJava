// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Constants.FuelConstants;
import frc.robot.subsystems.CANFuelSubsystem;

import java.util.function.DoubleSupplier;

public class LaunchSequence extends SequentialCommandGroup {
  /** 
   * LaunchSequence with a dynamic RPM.
   * @param fuelSubsystem The subsystem.
   * @param rpmSupplier A supplier for the target RPM.
   */
  public LaunchSequence(CANFuelSubsystem fuelSubsystem, DoubleSupplier rpmSupplier) {
    addCommands(
        new SpinUp(fuelSubsystem, rpmSupplier).withTimeout(FuelConstants.SPIN_UP_SECONDS),
        new Launch(fuelSubsystem, rpmSupplier));
  }

  /**
   * LaunchSequence with default RPM.
   */
  public LaunchSequence(CANFuelSubsystem fuelSubsystem) {
    this(fuelSubsystem, () -> FuelConstants.kDefaultRPM);
  }
}
