package frc.robot.commands;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.VisionSubsystem;
import org.photonvision.targeting.PhotonTrackedTarget;

public class AimAtTarget extends Command {
    private final DriveSubsystem m_driveSubsystem;
    private final VisionSubsystem m_visionSubsystem;
    private final PIDController m_turnController = new PIDController(0.05, 0, 0); // Tune these
    private final double SEARCH_ROTATION_SPEED = 0.4; // Percent of max speed to rotate when searching

    public AimAtTarget(DriveSubsystem driveSubsystem, VisionSubsystem visionSubsystem) {
        m_driveSubsystem = driveSubsystem;
        m_visionSubsystem = visionSubsystem;
        m_turnController.setTolerance(1.0); // Degrees of tolerance
        addRequirements(driveSubsystem);
    }

    @Override
    public void execute() {
        PhotonTrackedTarget target = m_visionSubsystem.getBestTarget();
        
        if (target != null) {
            // Found a valid target, use PID to aim
            double rotationSpeed = m_turnController.calculate(target.getYaw(), 0);
            m_driveSubsystem.drive(0, 0, rotationSpeed, false);
        } else {
            // Searching: Rotate robot until a target is found
            m_driveSubsystem.drive(0, 0, SEARCH_ROTATION_SPEED, false);
        }
    }

    @Override
    public boolean isFinished() {
        // Only finished if we have a target and are on setpoint
        return m_visionSubsystem.hasTargets() && m_turnController.atSetpoint();
    }

    @Override
    public void end(boolean interrupted) {
        m_driveSubsystem.drive(0, 0, 0, false);
    }
}
