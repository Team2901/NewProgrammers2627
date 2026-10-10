package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

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

    public Pose2D pose2DDelta(Pose2D poseA, Pose2D poseB) {
        return new Pose2D(DistanceUnit.INCH,
                poseA.getX(DistanceUnit.INCH) - poseB.getX(DistanceUnit.INCH),
                poseA.getY(DistanceUnit.INCH) - poseB.getY(DistanceUnit.INCH),
                AngleUnit.DEGREES,
                angleDiff(poseA.getHeading(AngleUnit.DEGREES), poseB.getHeading(AngleUnit.DEGREES)));
    }

    public double getError(double value, double errorScaler, double maxThreshold, double minThreshold) {
        double value1 = value * errorScaler;
        if (Math.abs(value1) > maxThreshold) {
            return Math.signum(value1) * maxThreshold;
        } else if (Math.abs(value1) < minThreshold) {
            return 0;
        } else {
            return value1;
        }
    }

    public void poseTelemetry(String caption, Pose2D pose) {
        telemetry.addData(caption, "%.2f %.2f %.2f", pose.getX(DistanceUnit.INCH), pose.getY(DistanceUnit.INCH), pose.getHeading(AngleUnit.DEGREES));
    }

    public void movePowerRelRobot(double xPowerRelRobot, double yPowerRelRobot, double counterClockwisePower) {
        // xPowerRelRobot = "forward", yPowerRelRobot = "left"
        double frontLeftPower = xPowerRelRobot - yPowerRelRobot - counterClockwisePower;
        double frontRightPower = xPowerRelRobot + yPowerRelRobot + counterClockwisePower;
        double backLeftPower = xPowerRelRobot + yPowerRelRobot - counterClockwisePower;
        double backRightPower = xPowerRelRobot - yPowerRelRobot + counterClockwisePower;

        // If any power is outside -1 to 1, scale all four down by the same amount
        // so the robot still moves in the same direction.
        double max = Math.max(1.0, Math.max(
                Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower)),
                Math.max(Math.abs(backLeftPower), Math.abs(backRightPower))));

        frontLeft.setPower(frontLeftPower / max);
        frontRight.setPower(frontRightPower / max);
        backLeft.setPower(backLeftPower / max);
        backRight.setPower(backRightPower / max);
    }

    public double[] translatePowerToRelRobot(double xDiffRelField, double yDiffRelField, double robotHeadingRelField) {
        double xyMag = Math.hypot(xDiffRelField, yDiffRelField);
        double xyHeadingRelField = Math.toDegrees(Math.atan2(yDiffRelField, xDiffRelField));
        double xyHeadingRelRobot = xyHeadingRelField - robotHeadingRelField;
        double xDiffRelRobot = xyMag * Math.cos(Math.toRadians(xyHeadingRelRobot));
        double yDiffRelRobot = xyMag * Math.sin(Math.toRadians(xyHeadingRelRobot));
        return new double[]{xDiffRelRobot, yDiffRelRobot};
    }

    public void movePowerRelField(double xPowerRelField, double yPowerRelField, double counterClockwisePower, double robotHeadingRelField) {
        double[] xyPowerRelRobot = translatePowerToRelRobot(xPowerRelField, yPowerRelField, robotHeadingRelField);
        movePowerRelRobot(xyPowerRelRobot[0], xyPowerRelRobot[1], counterClockwisePower);
    }

    public void moveTowardsRelField(Pose2D targetPoseRelField) {
        Pose2D currentPoseRelField = pinpoint.getPosition();
        Pose2D poseDeltaRelField = pose2DDelta(targetPoseRelField, currentPoseRelField);
        double xPowerRelField = getError(poseDeltaRelField.getX(DistanceUnit.INCH), 1.0/24, 1, 0.01);
        double yPowerRelField = getError(poseDeltaRelField.getY(DistanceUnit.INCH), 1.0/24, 1, 0.01);
        double counterClockwisePower = getError(poseDeltaRelField.getHeading(AngleUnit.DEGREES), 1.0/30, 1, 0.01);
        movePowerRelField(xPowerRelField, yPowerRelField, counterClockwisePower, currentPoseRelField.getHeading(AngleUnit.DEGREES));
    }

    public double angleDiff(double targetHeadingRelField, double robotHeadingRelField) {
        return AngleUnit.normalizeDegrees(targetHeadingRelField - robotHeadingRelField);
    }

}