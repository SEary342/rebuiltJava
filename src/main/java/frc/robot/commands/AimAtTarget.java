package frc.robot.commands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.VisionSubsystem;
import org.photonvision.targeting.PhotonTrackedTarget;

public class AimAtTarget extends Command {
    private final DriveSubsystem m_driveSubsystem;
    private final VisionSubsystem m_visionSubsystem;

    // 1. Lowered P to 0.02 and added D at 0.003 to act as a "brake"
    private final PIDController m_turnController = new PIDController(0.02, 0, 0.003); 

    // 2. Search speed: 5% is usually the minimum to move a robot on carpet
    private final double SEARCH_ROTATION_SPEED = 0.05; 
    private final double MAX_ROTATION_SPEED = 0.08; // Safety cap

    public AimAtTarget(DriveSubsystem driveSubsystem, VisionSubsystem visionSubsystem) {
        m_driveSubsystem = driveSubsystem;
        m_visionSubsystem = visionSubsystem;
        
        // 3. Tolerance: 1.5 degrees prevents the robot from "hunting" forever
        m_turnController.setTolerance(1.5); 
        
        addRequirements(driveSubsystem);
    }

    @Override
    public void execute() {
        // This uses your subsystem's logic (filtering by TargetConstants.ALL_TARGETS)
        PhotonTrackedTarget target = m_visionSubsystem.getBestTarget();
        
        if (target != null) {
            // Calculate speed based on the Yaw (respects your UI Offset Points)
            double rotationSpeed = m_turnController.calculate(target.getYaw(), 0);
            
            // 4. Clamp the output to prevent aggressive snapping
            rotationSpeed = MathUtil.clamp(rotationSpeed, -MAX_ROTATION_SPEED, MAX_ROTATION_SPEED);

            // If we are "close enough," stop the motors to prevent micro-oscillations
            if (m_turnController.atSetpoint()) {
                rotationSpeed = 0;
            }

            m_driveSubsystem.drive(0, 0, rotationSpeed, false);
        } else {
            // Searching: Rotate until one of your ALL_TARGETS is found
            m_driveSubsystem.drive(0, 0, SEARCH_ROTATION_SPEED, false);
        }
    }

    @Override
    public boolean isFinished() {
        // Ends only if we see a valid target and are centered on it
        return m_visionSubsystem.hasTargets() && m_turnController.atSetpoint();
    }

    @Override
    public void end(boolean interrupted) {
        // Ensure the robot stops when the command is finished or cancelled
        m_driveSubsystem.drive(0, 0, 0, false);
    }
}