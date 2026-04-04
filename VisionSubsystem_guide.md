# VisionSubsystem Guide

## What Does This Do?
The VisionSubsystem uses the camera to see targets and calculate shooting! It finds AprilTags on the field and figures out distance and RPM needed.

## Key Parts:
- **PhotonCamera**: USB camera that sees AprilTags
- **Target Detection**: Finds valid targets (red and blue speaker tags)
- **Distance Calculation**: Measures how far from target
- **RPM Lookup**: Uses distance to find correct shooter speed

## Important Methods:
- `getBestTarget()` - Gets the best visible target
- `getDistanceToTargetMeters(target)` - Calculates distance
- `getLauncherRPM(target)` - Looks up correct RPM from table
- `hasTargets()` - Checks if any targets visible

## ⚠️ MOTOR SETTINGS TO CHECK
This subsystem doesn't control motors, but provides data that controls the shooter motors!

Jump to [Constants.java](Constants.java) → `VisionConstants` class:
- **Camera Name**: `kCameraName` - Must match PhotonVision app name!
- **Camera Position**: `kCameraXOffset`, `kCameraYOffset`, `kCameraZOffset` - Measure camera position on robot
- **Camera Angle**: `kCameraPitch` - Measure camera mounting angle
- **Target Height**: `kTargetHeight` - Height of AprilTags on field

Also `TargetConstants`:
- **Valid Targets**: `ALL_TARGETS` - List of AprilTag IDs for your alliance
- **RPM Table**: `kRPMTable` - Distance-to-RPM values (calibrate by testing!)

## How to Debug:
1. **No targets found**: Check camera connected and `kCameraName`
2. **Wrong distance**: Check `kCameraZOffset` and `kTargetHeight`
3. **Wrong RPM**: Check `kRPMTable` values (calibrate on real robot!)
4. **Camera not working**: Check USB connection and PhotonVision app

## Dashboard Values:
Look for these on SmartDashboard:
- `Vision/HasTarget` - True if target visible
- `Vision/DistanceToTarget` - Distance in meters
- `Vision/SuggestedRPM` - Calculated RPM
- `Vision/BestTargetYaw` - Target position

## Commands That Use This:
- `AimAtTarget.java` - Uses vision to turn robot
- `LaunchSequence.java` - Gets RPM from vision</content>
<parameter name="filePath">c:\Git\rebuiltJava\ClimberSubsystem_guide.md