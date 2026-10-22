package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

public class MechanumBot {

    public Telemetry telemetry;

    public DcMotorEx frontLeft;
    public DcMotorEx backLeft;
    public DcMotorEx frontRight;
    public DcMotorEx backRight;

    public double wheelRadiusInches = 1.88976378;
    public double wheelCircumferenceInches = 2 * Math.PI * wheelRadiusInches;
    public double motorToWheelGearRatio = 1.0 / 1.0; // # of wheel gear teeth / # of motor gear teeth
    public double motorEncoderTicksPerRotation = ((((1 + (46 / 17.0))) * (1 + (46 / 11.0))) * 28); // from https://www.gobilda.com/5203-series-yellow-jacket-planetary-gear-motor-19-2-1-ratio-24mm-length-8mm-rex-shaft-312-rpm-3-3-5v-encoder/

    public GoBildaPinpointDriver pinpoint;
    private static final double pinpointXOffsetMM = 0;
    private static final double pinpointYOffsetMM = 0;

    public IMU imu;

    private static final RevHubOrientationOnRobot.UsbFacingDirection imuUsbFacingDirection = RevHubOrientationOnRobot.UsbFacingDirection.UP;
    private static final RevHubOrientationOnRobot.LogoFacingDirection imuLogoDirection = RevHubOrientationOnRobot.LogoFacingDirection.RIGHT;

    public void init(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;

        frontLeft = hardwareMap.get(DcMotorEx.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
        backLeft = hardwareMap.get(DcMotorEx.class, "backLeft");
        backRight = hardwareMap.get(DcMotorEx.class, "backRight");

        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.FORWARD);

        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        frontLeft.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        initPinpoint(hardwareMap);

        initImu(hardwareMap);
    }

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

    private void initPinpoint(HardwareMap hardwareMap) {

        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");

        /*
         *  Set the odometry pod positions relative to the point that you want the position to be measured from.
         *
         *  The X pod offset refers to how far sideways from the tracking point the X (forward) odometry pod is.
         *  Left of the center is a positive number, right of center is a negative number.
         *
         *  The Y pod offset refers to how far forwards from the tracking point the Y (strafe) odometry pod is.
         *  Forward of center is a positive number, backwards is a negative number.
         */
        pinpoint.setOffsets(pinpointXOffsetMM, pinpointYOffsetMM, DistanceUnit.MM); //these are tuned for 3110-0002-0001 Product Insight #1

        /*
         * Set the kind of pods used by your robot. If you're using goBILDA odometry pods, select either
         * the goBILDA_SWINGARM_POD, or the goBILDA_4_BAR_POD.
         * If you're using another kind of odometry pod, uncomment setEncoderResolution and input the
         * number of ticks per unit of your odometry pod.  For example:
         *     pinpoint.setEncoderResolution(13.26291192, DistanceUnit.MM);
         */
        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);

        /*
         * Set the direction that each of the two odometry pods count. The X (forward) pod should
         * increase when you move the robot forward. And the Y (strafe) pod should increase when
         * you move the robot to the left.
         */
        pinpoint.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD,
                GoBildaPinpointDriver.EncoderDirection.FORWARD);

        /*
         * Before running the robot, recalibrate the IMU. This needs to happen when the robot is stationary
         * The IMU will automatically calibrate when first powered on, but recalibrating before running
         * the robot is a good idea to ensure that the calibration is "good".
         * resetPosAndIMU will reset the position to 0,0,0 and also recalibrate the IMU.
         * This is recommended before you run your autonomous, as a bad initial calibration can cause
         * an incorrect starting value for x, y, and heading.
         */
        pinpoint.resetPosAndIMU();

        // Set the location of the robot - this should be the place you are starting the robot from
        pinpoint.setPosition(new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 90));
    }


    /**
     * Set the drive motor powers for one step of a move toward a target pose on the field.
     * <p>
     * This does not wait for the robot to arrive. It looks at where the robot is now, sets the
     * motor powers once, and returns. Call it repeatedly in a loop; each call adjusts the power
     * as the robot gets closer, and the caller decides when the robot is close enough to stop.
     * <p>
     * The power for X, Y and heading each follows the {@link #getError} profile: full power far
     * from the target, ramping down linearly once within 2 ft (or 30 degrees for heading), and
     * never below stall power.
     *
     * @param targetPoseRelField the pose to move toward, in field coordinates: X and Y position
     *                           plus the heading the robot should end up facing.
     */
    public void moveTowardsRelField(Pose2D targetPoseRelField) {
        // Step 1. Get the robot's current pose from the pinpoint.
        // Call pinpoint.update() first so the position is fresh, then pinpoint.getPosition().
        Pose2D currentPoseRelField = pinpoint.getPosition();

        // Step 2. Get the difference between the target pose and the current pose
        // Use pose2DDelta(targetPoseRelField, currentPose).
        // This is how far the robot still has to go in field X, field Y and heading.
        Pose2D poseDeltaRelField = pose2DDelta(targetPoseRelField, currentPoseRelField);

        // Step 3. Turn the X and Y differences into field relative X and Y powers
        // Call getError,
        // e.g. errorScaler 1.0 / 24 so the robot slows down within 2 ft of the target.
        double xPowerRelField = getError(poseDeltaRelField.getX(DistanceUnit.INCH), 1.0/24, 1, 0.01);
        double yPowerRelField = getError(poseDeltaRelField.getY(DistanceUnit.INCH), 1.0/24, 1, 0.01);

        // Step 4. Turn the heading difference into a counter-clockwise power
        // Call getError,
        // e.g. errorScaler 1.0 / 30 so the robot slows down within 30 degrees.
        double counterClockwisePower = getError(poseDeltaRelField.getHeading(AngleUnit.DEGREES), 1.0/30, 1, 0.01);

        // Step 5. Drive by calling movePowerRelField
        // Pass the powers from steps 3 and 4 and the robot's current heading.
        // It converts the field relative powers to robot relative ones and sets the motors.
        movePowerRelField(xPowerRelField, yPowerRelField, counterClockwisePower, currentPoseRelField.getHeading(AngleUnit.DEGREES));
    }

    /**
     * Return the pose A-B: the X, Y and heading changes needed to get from pose B to pose A.
     * <p>
     * Typically A is the target pose and B is the robot's current pose, so the result is how
     * far the robot still has to go. Both poses and the result are in field coordinates.
     *
     * @param poseA the pose to measure to (e.g. the target).
     * @param poseB the pose to measure from (e.g. the robot's current pose).
     * @return a pose holding A-B for X and Y (inches) and the shortest turn from B's heading
     * to A's heading (degrees, -180 to 180).
     */
    public Pose2D pose2DDelta(Pose2D poseA, Pose2D poseB) {
        // Step 1. Calculate the X difference A-B.
        // Read both X values in the same unit (inches), e.g. poseA.getX(DistanceUnit.INCH).
        double xDiff = poseA.getX(DistanceUnit.INCH) - poseB.getX(DistanceUnit.INCH);

        // Step 2. Calculate the Y difference A-B the same way.
        double yDiff = poseA.getY(DistanceUnit.INCH) - poseB.getX(DistanceUnit.INCH);

        // Step 3.  Calculate the heading difference A-B
        // Use the helper method angleDiffDegrees, so the result is the shortest turn.
        // e.g. -160 - 180 -> 20 instead of -340.
        double headingDegreesDiff = angleDiffDegrees(poseA.getHeading(AngleUnit.DEGREES), poseB.getHeading(AngleUnit.DEGREES));

        // Step 4. Create the pose to return from those three values
        // Pose2D has no setters, so the values must go into the constructor (replace the zeros below).
        return new Pose2D(DistanceUnit.INCH, xDiff, yDiff, AngleUnit.DEGREES, headingDegreesDiff);
    }

    /**
     * General purpose error function: turn how far the robot is from a target into the power
     * to apply, using a trapezoidal profile.
     * <p>
     * The result is {@code value * errorScaler}, with its magnitude clipped to between
     * {@code minThreshold} and {@code maxThreshold}. The sign of {@code value} is kept, so the
     * robot drives back toward the target if it overshoots. Far from the target the power is
     * capped at the max; close in it ramps down linearly; very close it stays at the min so the
     * motors never stall short of the target. The caller decides when it is close enough to stop.
     * <p>
     * Example #1: drive in the X or Y direction at full power until within 2 ft of the target,
     * then slow down linearly:
     * <pre>
     *     value:        distance remaining to the target (inches)
     *     errorScaler:  1.0 (max power) / 24 (inches)
     *     maxThreshold: 1.0 (max power)
     *     minThreshold: 0.01 (min power aka stall speed)
     * </pre>
     * Example #2: pivot at full power until within 30 degrees of a target heading, then slow
     * down linearly:
     * <pre>
     *     value:        angle remaining to the target heading (degrees), e.g. from {@link #angleDiffDegrees}
     *     errorScaler:  1.0 (max power) / 30 (degrees)
     *     maxThreshold: 1.0 (max power)
     *     minThreshold: 0.01 (min power aka stall speed)
     * </pre>
     *
     * @param value        signed distance or angle remaining to the target.
     * @param errorScaler  power per unit of {@code value}; usually maxThreshold divided by the
     *                     distance at which slowing down should begin.
     * @param maxThreshold largest power magnitude to return.
     * @param minThreshold smallest power magnitude to return (stall speed).
     * @return the power to apply, with the same sign as {@code value}.
     */
    public double getError(double value, double errorScaler, double maxThreshold, double minThreshold) {
        // Step 1. Scale the input value by the errorScaler.
        // TODO implement step 1
        double result = value * errorScaler;

        // Step 2. Clip the result to the max.
        // Step 3. Clip the result to the min.
        result = Range.clip(result, minThreshold, maxThreshold);

        return result;
    }

    /**
     * Drive the robot using powers given in the field's coordinate system (field-centric drive).
     * <p>
     * Field positive X is always away from the audience; field positive Y is always toward
     * the BLUE side. The robot moves in the requested field direction no matter which way it
     * is facing: the powers are converted to the robot's coordinate system with
     * {@link #translatePowerToRelRobot} and then applied with {@link #movePowerRelRobot}.
     *
     * @param xPowerRelField              power to apply in the field relative X direction, -1 to 1.
     * @param yPowerRelField              power to apply in the field relative Y direction, -1 to 1.
     * @param counterClockwisePower       power to apply to rotation, -1 to 1; positive turns counter-clockwise.
     * @param robotHeadingDegreesRelField robot heading in the field coordinate system, in degrees;
     *                                    0 faces field +X and positive is counter-clockwise.
     */
    public void movePowerRelField(double xPowerRelField, double yPowerRelField, double counterClockwisePower, double robotHeadingDegreesRelField) {
        // Step 1. Convert from Field coordinate system to robot coordinate system.
        double[] xyPowerRelRobot = translatePowerToRelRobot(xPowerRelField, yPowerRelField, robotHeadingDegreesRelField);

        // Step 2. Move that robot in that direction.
        movePowerRelRobot(xyPowerRelRobot[0], xyPowerRelRobot[1], counterClockwisePower);
    }

    /**
     * Convert a power given in the field's coordinate system into the power to apply in the
     * robot's own coordinate system, given the robot's current heading on the field.
     * <p>
     * Field positive X is always away from the audience; field positive Y is always toward
     * the BLUE side.
     *
     * @param xDiffRelField               power to apply in the field relative X direction.
     * @param yDiffRelField               power to apply in the field relative Y direction.
     * @param robotHeadingDegreesRelField robot heading in the field coordinate system, in degrees.
     * @return a two-element array {xPowerRelRobot, yPowerRelRobot}: the forward and left power
     * to pass to {@link #movePowerRelRobot}.
     */
    public double[] translatePowerToRelRobot(double xDiffRelField, double yDiffRelField, double robotHeadingDegreesRelField) {
        // Step 1. Calculate the magnitude of the xy values.
        // We currently have a power to apply in the X direction and a power to apply in the Y direction.
        // Get the power to apply along the hypotenuse of the right triangle formed by X and Y values.
        // Hypotenuse equation. H = squareRoot(x**2 + y**2) which Math.hypot does for you.
        double xyMag = Math.hypot(xDiffRelField, yDiffRelField);

        // Step 2. Determine the target angle in field coordinate system using the X and Y values passed in.
        // The angle we have is the Target heading in the field coordinate system.
        // However the robot my already be at some angle already. May even be at the
        // target heading already..
        double xyHeadingRelField = Math.toDegrees(Math.atan2(yDiffRelField, xDiffRelField));

        // Step 3. Calculate the difference between the target angle and the robot's current heading in field coordinate system.
        double xyHeadingRelRobot = xyHeadingRelField - robotHeadingDegreesRelField;

        // Step 4. Determine how much of the power should be attributed to the Robot's X and Y directions.
        // To move forward on the field 2 ft, the robot may need to
        //    move 2ft forward if it is facing forward
        //    move -2ft forward if it is facing backward
        //    strafe +/- 2ft if it is turned 90 degrees from forward
        double xDiffRelRobot = xyMag * Math.cos(Math.toRadians(xyHeadingRelRobot));
        double yDiffRelRobot = xyMag * Math.sin(Math.toRadians(xyHeadingRelRobot));

        return new double[]{xDiffRelRobot, yDiffRelRobot};
    }

    /**
     * Drive the robot using powers given in the robot's own coordinate system.
     * <p>
     * Robot X is forward and robot Y is left, regardless of which way
     * the robot faces on the field. The three inputs are combined into a power for each of
     * the 4 drive motors. If any motor power falls outside -1 to 1, all four are scaled down
     * by the same amount so the robot still moves in the requested direction.
     *
     * @param xPowerRelRobot        power to apply in the robot relative X (forward) direction, -1 to 1.
     * @param yPowerRelRobot        power to apply in the robot relative Y (left) direction, -1 to 1.
     * @param counterClockwisePower power to apply to rotation, -1 to 1; positive turns counter-clockwise.
     */
    public void movePowerRelRobot(double xPowerRelRobot, double yPowerRelRobot, double counterClockwisePower) {
        // Step 1. Calculate the power needed for each motor
        double frontLeftPower = xPowerRelRobot - yPowerRelRobot - counterClockwisePower;
        double frontRightPower = xPowerRelRobot + yPowerRelRobot + counterClockwisePower;
        double backLeftPower = xPowerRelRobot + yPowerRelRobot - counterClockwisePower;
        double backRightPower = xPowerRelRobot - yPowerRelRobot + counterClockwisePower;

        // Step 2. Scale the power to remaining in [-1, 1].
        // If any power is outside -1 to 1, scale all four down by the same amount
        // so the robot still moves in the same direction.
        double max = Math.max(1.0, Math.max(
                Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower)),
                Math.max(Math.abs(backLeftPower), Math.abs(backRightPower))));

        // Step 3. Set power on all 4 motors.
        frontLeft.setPower(frontLeftPower / max);
        frontRight.setPower(frontRightPower / max);
        backLeft.setPower(backLeftPower / max);
        backRight.setPower(backRightPower / max);
    }

    /**
     * Computes the shortest signed difference between two angles, in degrees.
     * <p>
     * The result is {@code degreesA - degreesB} wrapped into [-180, 180)
     *
     * @param degreesA the angle to measure to, in degrees
     * @param degreesB the angle to measure from, in degrees
     * @return the signed difference {@code degreesA - degreesB}, normalized to [-180, 180)
     */
    public double angleDiffDegrees(double degreesA, double degreesB) {
        return AngleUnit.normalizeDegrees(degreesA - degreesB);
    }

    /**
     * Computes the shortest signed difference between two angles, in radians.
     * <p>
     * The result is {@code radiansA - radiansB} wrapped into [-π, π)
     *
     * @param radiansA the angle to measure to, in radians
     * @param radiansB the angle to measure from, in radians
     * @return the signed difference {@code radiansA - radiansB}, normalized to [-π, π)
     */
    public double angleDiffRadians(double radiansA, double radiansB) {
        return AngleUnit.normalizeRadians(radiansA - radiansB);
    }

    public void poseTelemetry(String caption, Pose2D pose) {
        telemetry.addData(caption, "%.2f %.2f %.2f", pose.getX(DistanceUnit.INCH), pose.getY(DistanceUnit.INCH), pose.getHeading(AngleUnit.DEGREES));
    }

    public double angleDiff(double targetHeadingRelField, double robotHeadingRelField) {
        return AngleUnit.normalizeDegrees(targetHeadingRelField - robotHeadingRelField);
    }

}