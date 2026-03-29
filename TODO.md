
## Photon Vision
[x] Photonlib code Install
[x] Photon AimAtTarget
[x] Photon Distance To Target
[x] Ball Launch RPM Calc (interpolated table stored in TargetConstants)

## Simple Auton
[x] SubwooferShoot needs to be able to provide its own RPM settings for launcher - 3500 seems to work right
[x] Instead of percent controls for the launcher, i need a dynamic RPM config.
[x] Implement default and max rpms in constants
[x] Implement robot container controls (Dynamic Vision RPM, Manual Default 3500, Manual Max 5500)
[x] Logitech F310 Controller layout updated in RobotContainer

## PathPlanner (Wait and do not proceed to this yet)
[ ] PathPlanner Install
[ ] Gyro Integration
[ ] Old Python Gyro access
    self.sensehat_table = self.inst.getTable("SenseHat")
    self.gyro_yaw_entry = self.sensehat_table.getDoubleTopic("yaw").getEntry(0.0)
[ ] NamedCommands