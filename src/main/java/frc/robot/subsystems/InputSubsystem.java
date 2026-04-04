// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.InputConstants;

/**
 * Subsystem for handling driver input.
 * Extracts and processes joystick inputs to provide smooth control for the robot.
 */
public class InputSubsystem extends SubsystemBase {

  private final DriveSubsystem m_drive;

  private Translation2d m_linearVelocity = Translation2d.kZero;
  private double m_angularVelocity = 0.0;
  private boolean m_fieldRelative = true;
  private boolean m_isSlowMode = false;

  private double m_linearCoeff = 1.0;
  private double m_angularCoeff = 1.0;

  // Asymmetric slew rate limiters: moderate acceleration, snappy deceleration
  private final SlewRateLimiter m_xLimiter = new SlewRateLimiter(
      InputConstants.kLinearAccelerationLimit,
      InputConstants.kLinearDecelerationLimit,
      0);
  private final SlewRateLimiter m_yLimiter = new SlewRateLimiter(
      InputConstants.kLinearAccelerationLimit,
      InputConstants.kLinearDecelerationLimit,
      0);
  private final SlewRateLimiter m_rotLimiter = new SlewRateLimiter(
      InputConstants.kAngularAccelerationLimit,
      InputConstants.kAngularDecelerationLimit,
      0);

  public InputSubsystem(DriveSubsystem drive) {
    this.m_drive = drive;
  }

  /**
   * Processes generic inputs for driving.
   * 
   * @param xSpeedRaw The x-axis (forward/backward) input (-1 to 1)
   * @param ySpeedRaw The y-axis (left/right) input (-1 to 1)
   * @param rotRaw The rotation input (-1 to 1)
   */
  public void updateInputs(double xSpeedRaw, double ySpeedRaw, double rotRaw) {
    // 1. Calculate magnitude and angle for linear velocity
    Translation2d translation = new Translation2d(xSpeedRaw, ySpeedRaw);
    double magnitude = MathUtil.applyDeadband(translation.getNorm(), InputConstants.kJoystickDeadband);

    if (magnitude == 0) {
      // If no input, command zero, but let slew rates handle the ramp-down
      processVelocities(0, 0, 0);
    } else {
      // 2. Square the magnitude for better fine control
      double magnitudeSquared = Math.pow(magnitude, InputConstants.kLinearExponent);
      
      // 3. Create the squared translation vector
      Translation2d squaredTranslation = new Translation2d(magnitudeSquared, translation.getAngle());
      
      // 4. Calculate final velocities before slew rates
      double targetX = squaredTranslation.getX() * m_drive.getMaxLinearSpeedMetersPerSec();
      double targetY = squaredTranslation.getY() * m_drive.getMaxLinearSpeedMetersPerSec();
      
      // Calculate rotation
      double deadbandRot = MathUtil.applyDeadband(rotRaw, InputConstants.kJoystickDeadband);
      double rotSquared = Math.copySign(Math.pow(deadbandRot, InputConstants.kAngularExponent), rotRaw);
      double targetRot = rotSquared * m_drive.getMaxAngularSpeedRadPerSec();

      processVelocities(targetX, targetY, targetRot);
    }
  }

  /**
   * Internal helper to apply coefficients, slow mode, and slew rates.
   */
  private void processVelocities(double targetX, double targetY, double targetRot) {
    double slowModifier = m_isSlowMode ? InputConstants.kSlowModeModifier : 1.0;

    // Apply coefficients and slow mode
    targetX *= (m_linearCoeff * slowModifier);
    targetY *= (m_linearCoeff * slowModifier);
    targetRot *= (m_angularCoeff * slowModifier);

    // Apply slew rates
    m_linearVelocity = new Translation2d(
      m_xLimiter.calculate(targetX),
      m_yLimiter.calculate(targetY)
    );
    m_angularVelocity = m_rotLimiter.calculate(targetRot);
  }

  public void setSlowMode(boolean enabled) {
    this.m_isSlowMode = enabled;
  }

  public boolean isSlowMode() {
    return m_isSlowMode;
  }

  public void setCoefficients(double linearCoeff, double angularCoeff) {
    this.m_linearCoeff = linearCoeff;
    this.m_angularCoeff = angularCoeff;
  }

  public Translation2d getLinearVelocity() {
    return m_linearVelocity;
  }

  public double getAngularVelocity() {
    return m_angularVelocity;
  }

  public ChassisSpeeds getChassisSpeeds() {
    return new ChassisSpeeds(m_linearVelocity.getX(), m_linearVelocity.getY(), m_angularVelocity);
  }

  public boolean isFieldRelative() {
    return m_fieldRelative;
  }

  public void setFieldRelative(boolean fieldRelative) {
    this.m_fieldRelative = fieldRelative;
  }

  public void resetSlewRates() {
    m_xLimiter.reset(0);
    m_yLimiter.reset(0);
    m_rotLimiter.reset(0);
  }
}
