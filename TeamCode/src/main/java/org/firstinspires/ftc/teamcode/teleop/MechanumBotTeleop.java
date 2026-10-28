package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.hardware.MechanumBot;

@TeleOp(group = "MechanumBot")
public class MechanumBotTeleop extends OpMode {

    MechanumBot robot = new MechanumBot();
    Pose2D targetPose = new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 90);

    Pose2D redAudienceGoalPose = new Pose2D(DistanceUnit.INCH, -12, -12, AngleUnit.DEGREES, 90);
    Pose2D redFarGoalPose = new Pose2D(DistanceUnit.INCH, -12, 12, AngleUnit.DEGREES, -90);

    @Override
    public void init() {
        robot.init(hardwareMap, telemetry);
    }

    @Override
    public void loop() {
        robot.update();

        Pose2D currentPoseRelField = robot.pinpoint.getPosition();
        double robotHeadingRelField = currentPoseRelField.getHeading(AngleUnit.DEGREES);

        double xPowerRelField = gamepad1.left_stick_x;
        double yPowerRelField = -gamepad1.left_stick_y;

        double rightStickMag = Math.hypot(-gamepad1.right_stick_y, gamepad1.right_stick_x);
        double rightStickAngle = Math.toDegrees(Math.atan2(-gamepad1.right_stick_y, gamepad1.right_stick_x));

        Pose2D goalPose = currentPoseRelField.getY(DistanceUnit.INCH) > 0 ? redFarGoalPose : redAudienceGoalPose;
        robot.aimTurretAtRelField(goalPose);

        if (gamepad1.a) {
            robot.moveTowardsRelField(targetPose);
        } else {
            double counterClockwisePower;
            if (Math.abs(rightStickMag) > 0.1) {
                double targetHeadingRelRobot = robot.angleDiff(rightStickAngle, robotHeadingRelField);
                counterClockwisePower = robot.getError(targetHeadingRelRobot, 1.0/30, 1, 0);
            } else {
                counterClockwisePower = 0;
            }
            robot.movePowerRelField(xPowerRelField, yPowerRelField, counterClockwisePower, robotHeadingRelField);
        }

        if (gamepad1.b) {
            robot.setPinpointPositon(0,0, 90);
        }

        robot.poseTelemetry("currentPoseRelField", currentPoseRelField);
        robot.poseTelemetry("poseDelta", robot.pose2DDelta(targetPose, currentPoseRelField));
    }
}
