# ClimberSubsystem Guide

## What Does This Do?
The ClimberSubsystem makes the robot climb! It controls the motor that lifts the robot up during endgame.

## Key Parts:
- **Climber Motor**: Powerful motor that pulls robot up
- **Brake Mode**: Holds position when not moving

## Important Methods:
- `setClimber(power)` - Sets motor speed (-1 to 1)
- `stop()` - Stops the motor

## ⚠️ MOTOR SETTINGS TO CHECK (SUPER IMPORTANT!)
Jump to [Constants.java](Constants.java) → `ClimbConstatns` class:
- **MOTOR ID**: `CLIMBER_MOTOR_ID` - Must match your robot's wiring!
- **Current Limit**: `CLIMBER_MOTOR_CURRENT_LIMIT` - Prevents motor burnout (set in REV Hardware Client too!)
- **Climb Speeds**: `CLIMBER_MOTOR_UP_PERCENT` (positive), `CLIMBER_MOTOR_DOWN_PERCENT` (negative)

## How to Debug:
1. **Won't climb up**: Check `CLIMBER_MOTOR_UP_PERCENT` value
2. **Won't go down**: Check `CLIMBER_MOTOR_DOWN_PERCENT` (should be negative)
3. **Motor not responding**: Check `CLIMBER_MOTOR_ID`
4. **Slips when stopped**: Check brake mode in config (`IdleMode.kBrake`)
5. **Motor hot**: Check current limit in constants and REV Client

## Commands That Use This:
- `ClimbUp.java` - Calls `setClimber()` with up speed
- `ClimbDown.java` - Calls `setClimber()` with down speed

## Safety Notes:
- Current limits prevent motor damage
- Brake mode keeps robot from falling
- Test slowly - climbing is dangerous!</content>
<parameter name="filePath">c:\Git\rebuiltJava\DriveSubsystem_guide.md