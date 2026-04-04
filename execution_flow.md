# FRC Robot Code Execution Flow

## The Big Picture
Imagine your robot code as a video game that runs forever! The code "jumps" between different files like levels in a game. Here's the path it follows:

## 1. Starting Point: Main.java
```
📁 Main.java
   ↓
RobotBase.startRobot(Robot::new)  // This starts everything!
```

**What happens:** The robot turns on and WPILib says "Let's make a Robot!"

---

## 2. Robot Creation: Robot.java
```
📁 Robot.java
   ↓
new Robot()  // Constructor (makes the robot object)
   ↓
robotInit()  // First setup - runs ONCE
   ↓
Creates RobotContainer!  // This is where subsystems are born
```

**What happens:** 
- Robot gets created
- `robotInit()` runs once to set everything up
- Makes a `RobotContainer` (like a toolbox with all robot parts)

---

## 3. RobotContainer Setup: RobotContainer.java
```
📁 RobotContainer.java
   ↓
new RobotContainer()  // Constructor
   ↓
Creates ALL subsystems:
   - DriveSubsystem (wheels)
   - CANFuelSubsystem (shooter/intake)
   - VisionSubsystem (camera)
   - ClimberSubsystem (climber)
   - InputSubsystem (controllers)
   ↓
configureBindings()  // Sets up button controls
   ↓
Sets default commands (what robot does normally)
```

**What happens:** 
- All robot "parts" (subsystems) get created
- Buttons on controllers get connected to commands
- Default behaviors are set (like "drive normally")

---

## 4. The Forever Loop: Back to Robot.java
```
📁 Robot.java
   ↓
robotPeriodic()  // Runs EVERY 20 milliseconds FOREVER!
   ↓
CommandScheduler.getInstance().run()  // The magic manager
   ↓
Calls subsystem periodic() methods:
   - driveSubsystem.periodic()
   - fuelSubsystem.periodic()
   - visionSubsystem.periodic()
   - etc.
```

**What happens:** 
- Every 20ms (super fast!), the scheduler checks:
  - Did someone press a button? → Run that command!
  - Is autonomous running? → Keep running auto commands!
  - Update all subsystems (read sensors, move motors)

---

## 5. Commands in Action: commands/ folder
```
📁 commands/Intake.java
   ↓
When button pressed → CommandScheduler schedules it
   ↓
execute() method runs repeatedly
   ↓
Calls fuelSubsystem.intake()  // Actually moves the intake motor
```

**What happens:** 
- Commands tell subsystems what to do
- Example: Intake command calls `fuelSubsystem.intake()`

---

## 6. Subsystems Do the Work: subsystems/ folder
```
📁 subsystems/DriveSubsystem.java
   ↓
periodic()  // Updates every 20ms
   ↓
Reads gyro, encoders
   ↓
Updates robot position (odometry)
   ↓
Sends motor commands
```

**What happens:** 
- Subsystems talk to real robot hardware
- Read sensors, control motors
- Update dashboards with info

---

## Debug Navigation Map
When something breaks, follow this path:

### If robot won't move:
1. Check `RobotContainer.java` → Is DriveSubsystem created?
2. Check `configureBindings()` → Are drive controls set up?
3. Check `DriveSubsystem.java` → Is `periodic()` updating?

### If button doesn't work:
1. Check `RobotContainer.java` → Is button bound in `configureBindings()`?
2. Check command file (e.g., `Intake.java`) → Does it call subsystem methods?
3. Check subsystem file → Does the method exist and work?

### If autonomous doesn't run:
1. Check `Robot.java` → `autonomousInit()` schedules command?
2. Check `RobotContainer.java` → `getAutonomousCommand()` returns something?
3. Check PathPlanner files → Is auto path configured?

### If sensors don't update:
1. Check subsystem `periodic()` → Is it reading sensors?
2. Check `Robot.java` → Is `robotPeriodic()` calling scheduler?

## Key Files to Remember:
- **Main.java**: Entry point
- **Robot.java**: Main robot loop
- **RobotContainer.java**: Setup everything
- **commands/**: What the robot does
- **subsystems/**: How the robot does it

## The Loop Never Stops!
```
Robot turns on → Setup → Forever Loop (20ms) → Robot turns off
                    ↑
              Commands run here!
```

This flow helps you "jump" between files when debugging - just follow the arrows!</content>
<parameter name="filePath">execution_flow.md