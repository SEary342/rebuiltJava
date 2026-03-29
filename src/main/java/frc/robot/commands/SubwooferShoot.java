// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.CANFuelSubsystem;

public class SubwooferShoot extends SequentialCommandGroup {
    /**
     * Autonomous command for shooting from the subwoofer using a specific RPM.
     * @param fuelSubsystem The subsystem.
     */
    public SubwooferShoot(CANFuelSubsystem fuelSubsystem) {
        // Shooting from subwoofer usually uses a fixed, calibrated RPM (3500)
        addCommands(
                new LaunchSequence(fuelSubsystem, () -> 3500).withTimeout(10));
    }
}
