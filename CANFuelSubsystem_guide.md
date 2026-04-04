# CANFuelSubsystem Guide

## What Does This Do?
The CANFuelSubsystem controls the robot's fuel system - intake, indexer, and launcher! It picks up fuel, moves it inside the robot, and shoots it out.

## Key Parts:
- **Intake Rollers**: 2 motors that suck in fuel (left and right)
- **Indexer**: Moves fuel from intake to launcher
- **Launcher**: Shoots fuel at high speed

## Important Methods:
- `setIntakeLauncherRoller(power)` - Spins intake rollers
- `setFeederRoller(power)` - Moves indexer
- `setLauncherRPM(rpm)` - Sets shooter speed
- `stop()` - Stops all motors

## ⚠️ MOTOR SETTINGS TO CHECK (SUPER IMPORTANT!)
Jump to [Constants.java](Constants.java) → `FuelConstants` class:
- **MOTOR IDs**: `LEFT_INTAKE_LAUNCHER_MOTOR_ID`, `RIGHT_INTAKE_LAUNCHER_MOTOR_ID`, `INDEXER_MOTOR_ID` - Must match your robot's wiring!
- **Current Limits**: `LAUNCHER_MOTOR_CURRENT_LIMIT`, `INDEXER_MOTOR_CURRENT_LIMIT` - Prevents motor burnout
- **Intake Speeds**: `INTAKE_INTAKING_PERCENT` - How fast to suck in fuel
- **Shooting Speeds**: `INDEXER_LAUNCHING_PERCENT` - How fast to feed fuel to shooter
- **Launcher PID**: In constructor - `pid(0.0001, 0, 0)` - Tune these for accurate RPM control!

## How to Debug:
1. **Intake not working**: Check motor IDs and `INTAKE_INTAKING_PERCENT`
2. **Shooter weak**: Check `LAUNCHER_MOTOR_CURRENT_LIMIT` and PID values (tune in REV Client!)
3. **Fuel jams**: Check indexer speed `INDEXER_LAUNCHING_PERCENT`
4. **Motors hot**: Check current limits in constants
5. **RPM not accurate**: Tune PID values in constructor

## Dashboard Values:
Look for these on SmartDashboard:
- `Launcher/LeftRPM` and `Launcher/RightRPM` - Shooter speeds
- `Launcher/TargetRPM` - What speed we're trying for

## Commands That Use This:
- `Intake.java` - Calls intake methods
- `LaunchSequence.java` - Uses RPM control
- `Eject.java` - Reverses motors</content>
<parameter name="filePath">c:\Git\rebuiltJava\DriveSubsystem_guide.md