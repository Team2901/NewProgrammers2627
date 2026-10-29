package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

/**
 * DrawBot: two drive motors (green and gold) on a single pod, a turnMotor that steers which way
 * the pod faces, and a potentiometer reading the pod's angle.
 * <p>
 * The wheels sit front right and back left. Equal power on both drive motors moves the robot in
 * whatever direction the pod points, without changing its heading. To turn the robot, the pod
 * steers to 45 degrees, where both wheels point around the center, and the two motors drive in
 * opposite directions.
 * <p>
 * Steering angle: 0 is straight forward, increasing counter-clockwise viewed from above.
 */
public class DrawBot {

    public Telemetry telemetry;

    public DcMotorEx turnMotor;
    public DcMotorEx greenDriveMotor;
    public DcMotorEx goldDriveMotor;

    public AnalogInput pot;

    public Servo penServo;

    // Pen servo positions for lifting the pen off the field and putting it down to draw.
    public static final double PEN_UP_POSITION = 0.0;
    public static final double PEN_DOWN_POSITION = 1.0;

    // Full-turn 3.3V potentiometer, with the wiper set so the pod pointing straight forward reads 3.08V.
    public static final double POT_MAX_VOLTAGE = 3.3;
    public static final double POT_FORWARD_VOLTAGE = 3.08;

    // Steer at full power until the pod is within this many degrees of the target, then slow down.
    public static final double TURN_SLOWDOWN_DEGREES = 10;

    // Pod angle for turning the robot in place: the wheels sit front right and back left, so at 45
    // degrees both point around the center of the robot.
    public static final double ROTATE_STEERING_ANGLE_DEGREES = 45;

    // When turning the robot, keep the drive wheels still until the pod is within this many
    // degrees of ROTATE_STEERING_ANGLE_DEGREES.
    public static final double ROTATE_ALIGNED_DEGREES = 5;

    // Turn the robot at full power until within this many degrees of the target heading, then slow down.
    public static final double ROTATE_SLOWDOWN_DEGREES = 30;

    public IMU imu;

    private static final RevHubOrientationOnRobot.UsbFacingDirection imuUsbFacingDirection = RevHubOrientationOnRobot.UsbFacingDirection.UP;
    private static final RevHubOrientationOnRobot.LogoFacingDirection imuLogoDirection = RevHubOrientationOnRobot.LogoFacingDirection.RIGHT;

    /**
     * Set up the robot: find and configure the turn and drive motors, the potentiometer and the
     * pen servo (starting with the pen up), then initialize the IMU. Call once from the OpMode's init.
     *
     * @param hardwareMap the OpMode's hardware map, used to find each device by its configured name.
     * @param telemetry   the OpMode's telemetry, kept for reporting status.
     */
    public void init(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;

        turnMotor = hardwareMap.get(DcMotorEx.class, "turnMotor");
        greenDriveMotor = hardwareMap.get(DcMotorEx.class, "greenDriveMotor");
        goldDriveMotor = hardwareMap.get(DcMotorEx.class, "goldDriveMotor");

        // Positive turnMotor power should steer the pod counter-clockwise (pot voltage goes up).
        // Reverse it if the real robot turns the other way.
        turnMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        // The two drive wheels face opposite ways on the pod, so one must be reversed for both to
        // roll the same way.
        greenDriveMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        goldDriveMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        turnMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        greenDriveMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        goldDriveMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        turnMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        greenDriveMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        goldDriveMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        pot = hardwareMap.get(AnalogInput.class, "pot");

        penServo = hardwareMap.get(Servo.class, "penServo");
        penUp();

        initImu(hardwareMap);
    }

    /**
     * Find the IMU named "imu" and initialize it for how the Control Hub is mounted on the robot.
     *
     * @param hardwareMap the OpMode's hardware map.
     */
    private void initImu(HardwareMap hardwareMap) {

        imu = hardwareMap.get(IMU.class, "imu");

        IMU.Parameters parameters;
        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(imuLogoDirection, imuUsbFacingDirection);
        parameters = new IMU.Parameters(orientationOnRobot);
        boolean success = imu.initialize(parameters);

        if (success && (telemetry != null)) {
            telemetry.addLine("IMU initialized");
            telemetry.update();
        }
    }

    /**
     * Drive the robot toward a direction without turning it, for one step.
     * <p>
     * This does not wait for the pod to line up. Call it repeatedly in a loop; each call turns
     * the pod a little closer and sets the drive power for how well it is lined up right now.
     *
     * @param targetDriveAngleDegrees direction to drive, relative to the robot: 0 is forward,
     *                                counter-clockwise is positive.
     * @param power                   drive power, 0 to 1.
     */
    public void steerTowards(double targetDriveAngleDegrees, double power) {
        // Both wheels push the same way, so the robot moves without turning.
        steerAndDrive(targetDriveAngleDegrees, power, power);
    }

    /**
     * Turn the robot in place toward a heading without moving it, for one step.
     * <p>
     * Up to maxPower far from the target heading, slowing down once within
     * {@link #ROTATE_SLOWDOWN_DEGREES}. Call it repeatedly in a loop; the caller decides when the
     * robot is close enough.
     *
     * @param targetHeadingDegrees the heading to face, in the same frame as {@link #getHeadingAngleDegrees}.
     * @param maxPower             the fastest to turn, 0 to 1.
     */
    public void turnTowards(double targetHeadingDegrees, double maxPower) {
        double headingError = angleDiffDegrees(targetHeadingDegrees, getHeadingAngleDegrees());
        setRotatePower(maxPower * Range.clip(headingError / ROTATE_SLOWDOWN_DEGREES, -1, 1));
    }

    /**
     * Turn the robot in place, for one step.
     * <p>
     * The pod steers to 45 degrees, where both wheels point around the center of the robot. Once
     * it is within {@link #ROTATE_ALIGNED_DEGREES}, the green wheel drives forward while the gold
     * wheel drives backward; until then the drive wheels stay still. Call it repeatedly in a loop,
     * like {@link #steerTowards}.
     *
     * @param counterClockwisePower turn power, -1 to 1; positive turns counter-clockwise.
     */
    public void setRotatePower(double counterClockwisePower) {
        // The error already allows for the pod turning the robot from -135 degrees with the
        // motors reversed, so it is small when the pod is close to either angle.
        double steeringAngleError = getSteeringAngleErrorAndDirection(ROTATE_STEERING_ANGLE_DEGREES)[0];
        boolean aligned = Math.abs(steeringAngleError) < ROTATE_ALIGNED_DEGREES;

        double power = aligned ? counterClockwisePower : 0;
        steerAndDrive(ROTATE_STEERING_ANGLE_DEGREES, power, -power);
    }

    /**
     * Steer the pod toward an angle and set the two drive motors, for one step.
     * <p>
     * Equal powers move the robot; opposite powers at {@link #ROTATE_STEERING_ANGLE_DEGREES} turn it.
     *
     * @param angleDegrees    pod angle to steer to: 0 is forward, counter-clockwise is positive.
     * @param greenDrivePower power for the green wheel once the pod is lined up, -1 to 1.
     * @param goldDrivePower  power for the gold wheel once the pod is lined up, -1 to 1.
     */
    private void steerAndDrive(double angleDegrees, double greenDrivePower, double goldDrivePower) {
        // Step 1. How far the pod still has to turn, and which way to drive once it gets there.
        double[] errorAndDirection = getSteeringAngleErrorAndDirection(angleDegrees);
        double steeringAngleError = errorAndDirection[0];
        double driveDirection = errorAndDirection[1];

        // Step 2. Steer: full power far from the target, slowing down as it gets close.
        setTurnPower(Range.clip(steeringAngleError / TURN_SLOWDOWN_DEGREES, -1, 1));

        // Step 3. Drive. cos(error) is 1 when the pod points the right way and 0 when it is
        // sideways, so the robot only drives as much as the pod is lined up.
        double lineUp = driveDirection * Math.cos(Math.toRadians(steeringAngleError));
        greenDriveMotor.setPower(greenDrivePower * lineUp);
        goldDriveMotor.setPower(goldDrivePower * lineUp);
    }

    /**
     * Set the power on the turn motor that steers the pod.
     *
     * @param power turn power, -1 to 1
     */
    public void setTurnPower(double power) {
        turnMotor.setPower(power);
    }

    /**
     * How far the pod still has to turn to reach an angle, and which way to drive once it gets there.
     * <p>
     * The pod drives just as well backwards. If the target is more than 90 degrees away, it is
     * quicker to aim for the opposite angle and drive the motors in reverse, so the error is never
     * more than 90 degrees.
     *
     * @param angleDegrees pod angle to steer to: 0 is forward, counter-clockwise is positive.
     * @return a two-element array {steeringAngleError, driveDirection}: the degrees left to turn,
     * -90 to 90 (positive is counter-clockwise), and 1 to drive forward or -1 to drive in reverse.
     */
    private double[] getSteeringAngleErrorAndDirection(double angleDegrees) {
        double steeringAngleError = angleDiffDegrees(angleDegrees, getSteeringAngleDegrees());
        double driveDirection = 1;
        if (Math.abs(steeringAngleError) > 90) {
            steeringAngleError = AngleUnit.normalizeDegrees(steeringAngleError + 180);
            driveDirection = -1;
        }
        return new double[]{steeringAngleError, driveDirection};
    }

    /**
     * Lift the pen off the field so the robot stops drawing.
     */
    public void penUp() {
        penServo.setPosition(PEN_UP_POSITION);
    }

    /**
     * Put the pen down on the field so the robot draws as it moves.
     */
    public void penDown() {
        penServo.setPosition(PEN_DOWN_POSITION);
    }

    /**
     * Read the pod's angle from the potentiometer.
     *
     * @return the steering angle in degrees, normalized to [-180, 180); 0 is straight forward.
     */
    public double getSteeringAngleDegrees() {
        return potVoltageToDegrees(pot.getVoltage());
    }

    /**
     * Read the robot's heading from the IMU.
     *
     * @return the robot's heading in degrees, normalized to [-180, 180);
     * 0 is the way the robot faced when the IMU was last reset.
     */
    public double getHeadingAngleDegrees() {
        return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
    }

    /**
     * Convert a potentiometer voltage to a steering angle.
     *
     * @param voltage the pot voltage, 0 to POT_MAX_VOLTAGE.
     * @return the steering angle in degrees, normalized to [-180, 180).
     */
    public double potVoltageToDegrees(double voltage) {
        return AngleUnit.normalizeDegrees((voltage - POT_FORWARD_VOLTAGE) / POT_MAX_VOLTAGE * 360.0);
    }

    /**
     * Convert a steering angle to the voltage the potentiometer reads at that angle.
     *
     * @param degrees the steering angle in degrees; 0 is straight forward.
     * @return the pot voltage, 0 to POT_MAX_VOLTAGE.
     */
    public double degreesToPotVoltage(double degrees) {
        double voltage = (POT_FORWARD_VOLTAGE + degrees / 360.0 * POT_MAX_VOLTAGE) % POT_MAX_VOLTAGE;
        return voltage < 0 ? voltage + POT_MAX_VOLTAGE : voltage;
    }

    /**
     * Computes the shortest signed difference between two angles, in degrees.
     *
     * @param degreesA the angle to measure to, in degrees
     * @param degreesB the angle to measure from, in degrees
     * @return the signed difference {@code degreesA - degreesB}, normalized to [-180, 180)
     */
    public double angleDiffDegrees(double degreesA, double degreesB) {
        return AngleUnit.normalizeDegrees(degreesA - degreesB);
    }

}
