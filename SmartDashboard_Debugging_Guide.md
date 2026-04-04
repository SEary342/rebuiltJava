# SmartDashboard Debugging Guide

## Overview
This guide shows you **exactly which variables** to put on the SmartDashboard for debugging your FRC robot. Each variable includes:
- **Why it's useful** for debugging
- **Code example** of how to add it
- **What to look for** when troubleshooting

## 🚗 Drive Subsystem (Most Critical)

### Robot Position & Orientation
```java
// In DriveSubsystem.periodic():
SmartDashboard.putNumber("Drive/RobotX", getPose().getX());           // X position on field (meters)
SmartDashboard.putNumber("Drive/RobotY", getPose().getY());           // Y position on field (meters)
SmartDashboard.putNumber("Drive/Heading", getHeading());              // Direction robot is facing (degrees)
SmartDashboard.putNumber("Drive/TurnRate", getTurnRate());            // How fast robot is spinning (deg/sec)
```

**Debugging Use:**
- **"Robot won't drive straight"** → Check if heading changes when you push forward
- **"Odometry is wrong"** → Compare X/Y position to where you think you are

### Robot Movement Speeds
```java
// In DriveSubsystem.periodic():
ChassisSpeeds speeds = getRobotRelativeSpeeds();
SmartDashboard.putNumber("Drive/SpeedX", speeds.vxMetersPerSecond);    // Forward/back speed (m/s)
SmartDashboard.putNumber("Drive/SpeedY", speeds.vyMetersPerSecond);    // Left/right speed (m/s)
SmartDashboard.putNumber("Drive/SpeedRot", speeds.omegaRadiansPerSecond); // Rotation speed (rad/s)
```

**Debugging Use:**
- **"Robot moves jerky"** → Check if speeds change smoothly
- **"Too fast/slow"** → Verify speeds match your Constants

### Individual Wheel States
```java
// In DriveSubsystem.periodic():
SmartDashboard.putNumber("Drive/FL_Angle", m_frontLeft.getState().angle.getDegrees());
SmartDashboard.putNumber("Drive/FL_Speed", m_frontLeft.getState().speedMetersPerSecond);
SmartDashboard.putNumber("Drive/FR_Angle", m_frontRight.getState().angle.getDegrees());
SmartDashboard.putNumber("Drive/FR_Speed", m_frontRight.getState().speedMetersPerSecond);
SmartDashboard.putNumber("Drive/RL_Angle", m_rearLeft.getState().angle.getDegrees());
SmartDashboard.putNumber("Drive/RL_Speed", m_rearLeft.getState().speedMetersPerSecond);
SmartDashboard.putNumber("Drive/RR_Angle", m_rearRight.getState().angle.getDegrees());
SmartDashboard.putNumber("Drive/RR_Speed", m_rearRight.getState().speedMetersPerSecond);
```

**Debugging Use:**
- **"Wheel not turning correctly"** → Check if angle matches what you expect
- **"One wheel not working"** → Compare speeds between wheels

## 📷 Vision Subsystem

### Target Detection & Position
```java
// In VisionSubsystem.periodic():
PhotonTrackedTarget bestTarget = getBestTarget();
boolean hasValidTarget = (bestTarget != null);

SmartDashboard.putBoolean("Vision/HasTarget", hasValidTarget);
SmartDashboard.putNumber("Vision/TargetYaw", bestTarget.getYaw());         // Left/right position (-27° to +27°)
SmartDashboard.putNumber("Vision/TargetPitch", bestTarget.getPitch());     // Up/down position (-20° to +20°)
SmartDashboard.putNumber("Vision/Distance", getDistanceToTargetMeters(bestTarget)); // Distance in meters
SmartDashboard.putNumber("Vision/TargetID", bestTarget.getFiducialId());   // AprilTag ID number
```

**Debugging Use:**
- **"Vision not working"** → Check if HasTarget is true
- **"Wrong target"** → Verify TargetID matches expected AprilTag
- **"Distance wrong"** → Compare to tape measure distance

### Vision Performance
```java
// In VisionSubsystem.periodic():
SmartDashboard.putNumber("Vision/TargetArea", bestTarget.getArea());              // How big target appears (0-100)
SmartDashboard.putNumber("Vision/TargetSkew", bestTarget.getSkew());              // Target rotation angle
SmartDashboard.putNumber("Vision/NumTargets", m_latestResult.getTargets().size()); // How many targets seen
SmartDashboard.putBoolean("Vision/TargetValid", isTargetValid(bestTarget));       // Is target in valid list?
SmartDashboard.putNumber("Vision/Latency", m_latestResult.getLatencyMillis());    // Camera delay (ms)
```

**Debugging Use:**
- **"Vision laggy"** → Check latency (should be <100ms)
- **"Wrong target selected"** → Check NumTargets and TargetValid

### Launcher RPM Calculation
```java
// In VisionSubsystem.periodic():
SmartDashboard.putNumber("Vision/SuggestedRPM", getLauncherRPM(bestTarget));     // RPM needed for this distance
```

**Debugging Use:**
- **"Shots going wrong way"** → Check if RPM matches your lookup table

## 🎯 Fuel Subsystem

### Launcher Motor Performance
```java
// In CANFuelSubsystem.periodic():
SmartDashboard.putNumber("Fuel/LeftRPM", LeftIntakeLauncher.getEncoder().getVelocity());
SmartDashboard.putNumber("Fuel/RightRPM", RightIntakeLauncher.getEncoder().getVelocity());
SmartDashboard.putNumber("Fuel/TargetRPM", getTargetRPM());  // From RobotContainer
```

**Debugging Use:**
- **"Launcher not spinning fast enough"** → Compare actual RPM to target RPM
- **"Motors out of sync"** → Check if Left/Right RPM are different

### Motor Health & Power
```java
// In CANFuelSubsystem.periodic():
SmartDashboard.putNumber("Fuel/LeftTemp", LeftIntakeLauncher.getMotorTemperature());
SmartDashboard.putNumber("Fuel/RightTemp", RightIntakeLauncher.getMotorTemperature());
SmartDashboard.putNumber("Fuel/IndexerTemp", Indexer.getMotorTemperature());

SmartDashboard.putNumber("Fuel/LeftCurrent", LeftIntakeLauncher.getOutputCurrent());
SmartDashboard.putNumber("Fuel/RightCurrent", RightIntakeLauncher.getOutputCurrent());
SmartDashboard.putNumber("Fuel/IndexerCurrent", Indexer.getOutputCurrent());

SmartDashboard.putNumber("Fuel/LeftOutput", LeftIntakeLauncher.getAppliedOutput());
SmartDashboard.putNumber("Fuel/RightOutput", RightIntakeLauncher.getAppliedOutput());
SmartDashboard.putNumber("Fuel/IndexerOutput", Indexer.getAppliedOutput());
```

**Debugging Use:**
- **"Motors getting hot"** → Check temperatures (>60°C is concerning)
- **"Not enough power"** → Check current draw and output percentage
- **"Motor not responding"** → Check if output matches what you commanded

## 🎮 Input Subsystem

### Processed Control Values
```java
// In InputSubsystem.periodic():
SmartDashboard.putNumber("Input/LinearVelX", m_linearVelocity.getX());     // Forward/back speed command
SmartDashboard.putNumber("Input/LinearVelY", m_linearVelocity.getY());     // Left/right speed command
SmartDashboard.putNumber("Input/AngularVel", m_angularVelocity);           // Rotation speed command
```

**Debugging Use:**
- **"Controls feel wrong"** → Check if velocities match joystick input
- **"Robot not responding to joystick"** → Verify values change when you move sticks

### Control Modes & Settings
```java
// In InputSubsystem.periodic():
SmartDashboard.putBoolean("Input/SlowMode", m_isSlowMode);                 // Is precision mode active?
SmartDashboard.putBoolean("Input/FieldRelative", m_fieldRelative);         // Field vs robot-relative driving
SmartDashboard.putNumber("Input/LinearCoeff", m_linearCoeff);              // Speed multiplier (0.4 = 40% speed)
SmartDashboard.putNumber("Input/AngularCoeff", m_angularCoeff);            // Turn multiplier (0.4 = 40% turn)
```

**Debugging Use:**
- **"Slow mode not working"** → Check if SlowMode becomes true
- **"Robot driving wrong direction"** → Check FieldRelative setting

## 🪜 Climber Subsystem

### Climber Motor Monitoring
```java
// In ClimberSubsystem.periodic():
SmartDashboard.putNumber("Climber/Temp", climberMotor.getMotorTemperature());
SmartDashboard.putNumber("Climber/Current", climberMotor.getOutputCurrent());
SmartDashboard.putNumber("Climber/Output", climberMotor.getAppliedOutput());
SmartDashboard.putNumber("Climber/Velocity", climberMotor.getEncoder().getVelocity());
```

**Debugging Use:**
- **"Climber not moving"** → Check if Output is non-zero when button pressed
- **"Climber too slow/fast"** → Monitor velocity and adjust power constants

## 🎯 Robot Container (Overall State)

### Launcher Control State
```java
// In RobotContainer.updateLauncherDashboard():
SmartDashboard.putBoolean("Launcher/VisionMode", isVisionRPMEnabled);      // Using vision or manual RPM?
SmartDashboard.putNumber("Launcher/ManualRPMIndex", manualRPMIndex);       // Which RPM table entry selected
SmartDashboard.putNumber("Launcher/ManualRPM", TargetConstants.kRPMTable[manualRPMIndex][1]);
```

**Debugging Use:**
- **"Wrong RPM selected"** → Check which mode is active and which RPM is being used

### Match Information
```java
// In RobotContainer.periodic() - Add this method:
@Override
public void periodic() {
    SmartDashboard.putNumber("Robot/Alliance", DriverStation.getAlliance().isPresent() ?
        (DriverStation.getAlliance().get() == DriverStation.Alliance.Red ? 1 : 0) : -1);
    SmartDashboard.putNumber("Robot/MatchTime", DriverStation.getMatchTime());
    SmartDashboard.putString("Robot/Mode", DriverStation.isAutonomous() ? "Auto" :
        (DriverStation.isTeleop() ? "Teleop" : "Disabled"));
}
```

**Debugging Use:**
- **"Wrong alliance colors"** → Check Alliance value
- **"Autonomous not working"** → Verify Mode shows "Auto"

## Implementation Priority

### 🔥 HIGH PRIORITY (Add these first - most useful for debugging)
1. Drive position, heading, and wheel states
2. Vision target detection and distance
3. Fuel launcher RPM (actual vs target)
4. Input velocities and control modes

### 🟡 MEDIUM PRIORITY (Add when you have time)
1. Motor temperatures and currents
2. Vision performance metrics
3. Climber motor data
4. Match state information

### 🔵 LOW PRIORITY (Nice to have)
1. Raw encoder positions
2. Additional vision data
3. Detailed motor outputs

## Common Debugging Scenarios

### "Robot won't drive straight"
- Check: `Drive/Heading` - should stay constant when pushing forward
- Check: `Drive/FL_Angle, FR_Angle, RL_Angle, RR_Angle` - should all be ~0° or 180°

### "Vision not detecting targets"
- Check: `Vision/HasTarget` - should be true when pointing at AprilTag
- Check: `Vision/Latency` - should be <100ms
- Check: `Vision/TargetValid` - should be true for valid AprilTags

### "Shooting is inconsistent"
- Check: `Fuel/LeftRPM, RightRPM` vs `Fuel/TargetRPM` - should match
- Check: `Vision/Distance` - should be accurate to tape measure
- Check: `Fuel/LeftTemp, RightTemp` - motors shouldn't be overheating

### "Controls feel jerky"
- Check: `Input/LinearVelX, LinearVelY` - should change smoothly
- Check: `Drive/SpeedX, SpeedY` - should ramp up/down gradually
- Check: `Input/SlowMode` - should activate when button pressed

### "Motors getting hot"
- Check: Temperature values >60°C means cooling issues
- Check: Current draw - high current = high power usage
- Check: Output percentage - should match what you commanded

## Quick Setup Code

Add this to each subsystem's `periodic()` method:

```java
@Override
public void periodic() {
    // Add your SmartDashboard.putXXX() calls here
    // They will update automatically every 20ms
}
```

Remember: **More data is better than less data** when debugging! You can always remove variables later, but you can't debug what you don't monitor. 🚀</content>
<parameter name="filePath">SmartDashboard_Debugging_Guide.md