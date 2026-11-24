package org.firstinspires.ftc.teamcode.testing;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.hardware.ExampleTurretMrsToebes;
import org.firstinspires.ftc.teamcode.hardware.MechanumDriveBase;


@TeleOp(group = "test")
public class TestMoveDriveBaseTeleopMrsToebes extends OpMode {
    public MechanumDriveBase robot = new MechanumDriveBase();
    public ExampleTurretMrsToebes turret;

    public GoBildaPinpointDriver pinpoint;
    private static final double pinpointXOffsetMM = 0;
    private static final double pinpointYOffsetMM = 0;

    private static final Pose2D  turretTargetFieldRel = new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0);

    @Override
    public void init() {
        robot.init(hardwareMap, telemetry);
        initPinpoint(hardwareMap);
        turret = new ExampleTurretMrsToebes(hardwareMap, telemetry, pinpoint);
    }

    @Override
    public void loop() {
        robot.update();
        pinpoint.update();
        Pose2D currentPose = pinpoint.getPosition();

        double leftJoyY = -gamepad1.left_stick_y;
        double leftJoyX = gamepad1.left_stick_x;
        double rightJoyX = gamepad1.right_stick_x;

        double leftJoyRadius = Math.hypot(leftJoyX, leftJoyY);
        double leftJoyAngleDegrees = Math.toDegrees(Math.atan2(leftJoyY, leftJoyX));

        if (gamepad1.a) {
            robot.moveTowardsRelField(new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0), currentPose);
        } else {
            double forwardX = Math.cos(Math.toRadians(leftJoyAngleDegrees)) * leftJoyRadius;
            double leftY = Math.sin(Math.toRadians(leftJoyAngleDegrees)) * leftJoyRadius;
            robot.movePowerRelField(forwardX, leftY, -rightJoyX, currentPose.getHeading(AngleUnit.DEGREES));
        }

        // if gamepad2 right stick x is pressed, turn the turret continually in that direction (counter clockwise or clockwise)
        // else if gamepad2 left stick is pressed, turn the turret to face that direction (relative to the field)
        // else turn the turret to face the origin of the field (0,0,0)
        double joystick2Angle = Math.toDegrees(Math.atan2(-gamepad2.right_stick_y, gamepad2.right_stick_x));
        telemetry.addData("JoystickAngle", "%.2f", joystick2Angle);
        telemetry.addData("Right stick X", gamepad2.right_stick_x);
        telemetry.addData("Right stick Y", gamepad2.right_stick_y);
        telemetry.addLine("------------------");

        turret.turnTowardsRelField(joystick2Angle, currentPose);
        //turret.aimTurretAtRelField(turretTargetFieldRel, currentPose);

        telemetry.addData("Turret Power", turret.turretMotor.getPower());
        telemetry.addData("current turret heading", "%.2f", turret.getHeadingRelRobot());
        telemetry.addData("current turret heading", "%.2f", turret.getHeadingRelField(currentPose));
        telemetry.addLine("------------------");

        telemetry.addData("current pose", "(%.2f, %.2f, %.2f)",
                currentPose.getX(DistanceUnit.INCH),
                currentPose.getY(DistanceUnit.INCH),
                currentPose.getHeading(AngleUnit.DEGREES));
    }


    /**
     * Find the Pinpoint odometry computer named "pinpoint" and configure its pod offsets, pod type
     * and encoder directions. The position is left as it was, so a starting pose set by auto carries over.
     *
     * @param hardwareMap the OpMode's hardware map.
     */
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
        pinpoint.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.FORWARD);

        /*
         * Before running the robot, recalibrate the IMU. This needs to happen when the robot is stationary
         * The IMU will automatically calibrate when first powered on, but recalibrating before running
         * the robot is a good idea to ensure that the calibration is "good".
         */
        pinpoint.recalibrateIMU();

        if (telemetry != null) {
            Pose2D initPose = pinpoint.getPosition();
            telemetry.addData("pinpoint initialized", "%.2f %.2f %.2f", initPose.getX(DistanceUnit.INCH), initPose.getY(DistanceUnit.INCH), initPose.getHeading(AngleUnit.DEGREES));
        }
    }

}