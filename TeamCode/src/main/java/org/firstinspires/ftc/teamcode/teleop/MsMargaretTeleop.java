package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.hardware.ClawBot;

@TeleOp(group = "MsMargaret")
public class MsMargaretTeleop extends OpMode {

    // MechanumBot robot = new MechanumBot();
    ClawBot robot = new ClawBot();

    @Override
    public void init() {
        robot.init(hardwareMap, telemetry);
    }

    @Override
    public void loop() {

        float turnPower = gamepad1.left_stick_x;
        float rightPower = gamepad1.right_stick_x;
        float forwardPower = -gamepad1.right_stick_y;

        robot.leftMotor.setPower(forwardPower + turnPower);
        robot.rightMotor.setPower(forwardPower -turnPower);

        /*
        robot.frontLeft.setPower(forwardPower + rightPower + turnPower);
        robot.frontRight.setPower(forwardPower - rightPower - turnPower);
        robot.backLeft.setPower(forwardPower - rightPower + turnPower);
        robot.backRight.setPower(forwardPower + rightPower - turnPower);
         */

        robot.pinpoint.update();
        Pose2D pose2D = robot.pinpoint.getPosition();

        telemetry.addData("imu angle (DEGREES)", robot.imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES));

        telemetry.addData("X coordinate (IN)", pose2D.getX(DistanceUnit.INCH));
        telemetry.addData("Y coordinate (IN)", pose2D.getY(DistanceUnit.INCH));
        telemetry.addData("Heading angle (DEGREES)", pose2D.getHeading(AngleUnit.DEGREES));
    }
}
