package frc.robot.subsystems;

import java.util.List;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonUtils;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.VisionConstants;
import frc.robot.Constants.TargetConstants;

public class VisionSubsystem extends SubsystemBase {
    private final PhotonCamera m_camera;
    private List<PhotonPipelineResult> m_unreadResults;
    private PhotonPipelineResult m_latestResult;

    public VisionSubsystem() {
        m_camera = new PhotonCamera(VisionConstants.kCameraName);
        m_latestResult = new PhotonPipelineResult();
    }

    @Override
    public void periodic() {
        // Fetch all unread results once per loop to clear the PhotonVision queue.
        m_unreadResults = m_camera.getAllUnreadResults();

        // Update 'latest' reference if new data exists
        if (!m_unreadResults.isEmpty()) {
            m_latestResult = m_unreadResults.get(m_unreadResults.size() - 1);
        }

        PhotonTrackedTarget bestTarget = getBestTarget();
        boolean hasValidTarget = (bestTarget != null);
        
        SmartDashboard.putBoolean("Vision/HasTarget", hasValidTarget);
        
        if (hasValidTarget) {
            SmartDashboard.putNumber("Vision/BestTargetYaw", bestTarget.getYaw());
            SmartDashboard.putNumber("Vision/BestTargetPitch", bestTarget.getPitch());
            SmartDashboard.putNumber("Vision/DistanceToTarget", getDistanceToTargetMeters(bestTarget));
            SmartDashboard.putNumber("Vision/SuggestedRPM", getLauncherRPM(bestTarget));
            SmartDashboard.putNumber("Vision/TargetID", bestTarget.getFiducialId());
        }
    }

    /**
     * Filters targets based on TargetConstants.ALL_TARGETS
     */
    public boolean isTargetValid(PhotonTrackedTarget target) {
        int id = target.getFiducialId();
        for (int validId : TargetConstants.ALL_TARGETS) {
            if (id == validId) return true;
        }
        return false;
    }

    /**
     * Gets the best valid target from the most recent result.
     */
    public PhotonTrackedTarget getBestTarget() {
        if (!m_latestResult.hasTargets()) return null;

        PhotonTrackedTarget bestTarget = null;
        for (PhotonTrackedTarget target : m_latestResult.getTargets()) {
            if (isTargetValid(target)) {
                // Logic: Pick the target closest to the center of the camera (lowest Yaw)
                if (bestTarget == null || Math.abs(target.getYaw()) < Math.abs(bestTarget.getYaw())) {
                    bestTarget = target;
                }
            }
        }
        return bestTarget;
    }

    public boolean hasTargets() {
        return getBestTarget() != null;
    }

    /**
     * Get the distance to a specific target in meters.
     */
    public double getDistanceToTargetMeters(PhotonTrackedTarget target) {
        if (target == null) return -1.0;

        return PhotonUtils.calculateDistanceToTargetMeters(
                VisionConstants.kCameraZOffset,
                VisionConstants.kTargetHeight,
                VisionConstants.kCameraPitch,
                Units.degreesToRadians(target.getPitch()));
    }

    /**
     * Calculate required launcher RPM using the calibrated lookup table in Constants.
     */
    public double getLauncherRPM(PhotonTrackedTarget target) {
        double distance = getDistanceToTargetMeters(target);
        if (distance < 0) return 0;

        double[][] table = TargetConstants.kRPMTable;
        
        // Linear Interpolation
        if (distance <= table[0][0]) return table[0][1];
        if (distance >= table[table.length - 1][0]) return table[table.length - 1][1];
        
        for (int i = 0; i < table.length - 1; i++) {
            if (distance >= table[i][0] && distance <= table[i+1][0]) {
                double x0 = table[i][0];
                double y0 = table[i][1];
                double x1 = table[i+1][0];
                double y1 = table[i+1][1];
                
                return y0 + (y1 - y0) * (distance - x0) / (x1 - x0);
            }
        }
        return table[0][1]; 
    }

    /**
     * Returns all results received in the current loop. 
     * Useful for Pose Estimators.
     */
    public List<PhotonPipelineResult> getUnreadResults() {
        return m_unreadResults;
    }

    public PhotonCamera getCamera() {
        return m_camera;
    }
}
