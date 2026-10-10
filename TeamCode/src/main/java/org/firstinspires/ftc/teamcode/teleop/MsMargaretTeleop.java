package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.hardware.MechanumBot;

@TeleOp(group = "__MsMargaret")
public class MsMargaretTeleop extends OpMode {

    MechanumBot robot = new MechanumBot();
    Pose2D targetPoseRelField;

    @Override
    public void init() {
        robot.init(hardwareMap, telemetry);
        targetPoseRelField = robot.pinpoint.getPosition();
    }

    @Override
    public void loop() {

        robot.pinpoint.update();
        Pose2D currentPoseRelField = robot.pinpoint.getPosition();
        double robotHeadingRelField = currentPoseRelField.getHeading(AngleUnit.DEGREES);

        double xPowerRelField = gamepad1.right_stick_x;
        double yPowerRelField = -gamepad1.right_stick_y;

        double leftStickMag = Math.hypot(-gamepad1.left_stick_y, gamepad1.left_stick_x);
        double leftStickAngle = Math.toDegrees(Math.atan2(-gamepad1.left_stick_y, gamepad1.left_stick_x));

        if (gamepad1.a) {
            robot.moveTowardsRelField(targetPoseRelField);
        } else {
            double targetHeadingRelField = (Math.abs(leftStickMag) > 0.1) ? leftStickAngle : robotHeadingRelField;
            double targetHeadingRelRobot = robot.angleDiff(targetHeadingRelField, robotHeadingRelField);
            double counterClockwisePower = robot.getError(targetHeadingRelRobot, 1.0/30, 1, 0.01);
            robot.movePowerRelField(xPowerRelField, yPowerRelField, counterClockwisePower, robotHeadingRelField);
        }

        robot.poseTelemetry("currentPoseRelField", currentPoseRelField);
        robot.poseTelemetry("targetPose", targetPoseRelField);
        robot.poseTelemetry("poseDelta", robot.pose2DDelta(targetPoseRelField, currentPoseRelField));

        telemetry.addData("FL", "%.2f", robot.frontLeft.getPower());
        telemetry.addData("FR", "%.2f", robot.frontRight.getPower());
        telemetry.addData("BL", "%.2f", robot.backLeft.getPower());
        telemetry.addData("BR", "%.2f", robot.backRight.getPower());
    }
}
