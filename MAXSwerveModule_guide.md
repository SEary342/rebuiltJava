# DriveSubsystem Guide

## What Does This Do?
The DriveSubsystem makes your robot move! It controls the 4 swerve wheels that let the robot drive in any direction. Like a car that can drive sideways!

## Key Parts:
- **4 Swerve Modules**: Each wheel can spin AND turn (front-left, front-right, back-left, back-right)
- **Gyro**: Gets direction from the coprocessor (Pi)
- **Odometry**: Tracks where the robot is on the field

## Important Methods:
- `drive(xSpeed, ySpeed, rot, fieldRelative)` - Main drive method
- `setX()` - Locks wheels in X shape (parking brake)
- `zeroHeading()` - Resets direction to 0
- `getPose()` - Gets current position on field

## ⚠️ MOTOR SETTINGS TO CHECK (SUPER IMPORTANT!)
Jump to [Constants.java](Constants.java) → `DriveConstants` class:
- **MOTOR IDs**: `kFrontLeftDrivingCanId`, `kFrontLeftTurningCanId`, etc. - Must match your robot's wiring! Wrong IDs = wheels don't work
- **Robot Size**: `kTrackWidth` & `kWheelBase` - Measure your robot and update these
- **Speed Limits**: `kMaxSpeedMetersPerSecond` - Test and adjust for your robot
- **Turning Speed**: `kMaxAngularSpeed` - How fast robot can spin

Also check `ModuleConstants` in [Constants.java](Constants.java):
- **Gear Ratio**: `kDrivingMotorReduction` - Depends on your gear setup
- **Wheel Size**: `kWheelDiameterMeters` - Measure your wheels

## How to Debug:
1. **Robot won't move**: Check motor IDs in `DriveConstants` match robot wiring
2. **Robot spins wrong way**: Check `kGyroReversed` in constants
3. **Robot position wrong**: Check `getPose()` on dashboard
4. **Wheels not turning**: Check CAN IDs and wiring
5. **Motors overheat**: Check current limits in REV Hardware Client

## PathPlanner Setup:
This subsystem uses PathPlanner for autonomous driving. If auto doesn't work:
- Check `RobotConfig.fromGUISettings()` in constructor
- Make sure PathPlanner app is configured for your robot size

## Dashboard Values:
Look for these on SmartDashboard:
- Robot position (X, Y, rotation)
- Module states (speed and angle for each wheel)</content>
<parameter name="filePath">c:\Git\rebuiltJava\InputSubsystem_guide.md