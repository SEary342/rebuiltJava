# InputSubsystem Guide

## What Does This Do?
The InputSubsystem takes joystick inputs and makes them smooth for driving! It processes controller sticks and makes the robot drive nicely.

## Key Parts:
- **Deadband**: Ignores small joystick movements
- **Slew Rate Limiters**: Prevents jerky movements
- **Slow Mode**: Makes robot drive slower for precision

## Important Methods:
- `updateInputs(x, y, rot)` - Processes joystick values
- `setSlowMode(true/false)` - Enables slow precision mode
- `getChassisSpeeds()` - Gets processed speeds for driving
- `isFieldRelative()` - Checks if driving relative to field

## ⚠️ MOTOR SETTINGS TO CHECK
This subsystem doesn't control motors directly, but affects how the DriveSubsystem motors behave!

Jump to [Constants.java](Constants.java) → `InputConstants` class:
- **Deadband**: `kJoystickDeadband` - How much stick movement to ignore (too small = twitchy, too big = no control)
- **Acceleration**: `kLinearAccelerationLimit` - How fast robot can speed up (lower = smoother but slower response)
- **Slow Mode**: `kSlowModeModifier` - How slow slow mode is (0.4 = 40% speed)

Also check `OperatorConstants`:
- **Speed Limits**: `SPEED_LIMIT` & `TURN_SPEED_LIMIT` - Overall speed caps

## How to Debug:
1. **Robot too jerky**: Increase `kLinearAccelerationLimit`
2. **Sticks too sensitive**: Increase `kJoystickDeadband`
3. **Slow mode not working**: Check `kSlowModeModifier` value
4. **Robot goes wrong direction**: Check input processing in `updateInputs()`

## Dashboard Values:
Look for these on SmartDashboard:
- InputSubsystem doesn't show much - check DriveSubsystem for speeds

## How It Connects:
- Gets inputs from controllers in `RobotContainer.configureBindings()`
- Passes processed speeds to `DriveSubsystem.drive()`</content>
<parameter name="filePath">c:\Git\rebuiltJava\CANFuelSubsystem_guide.md