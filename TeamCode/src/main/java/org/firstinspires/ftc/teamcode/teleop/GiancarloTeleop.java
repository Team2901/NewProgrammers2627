package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.hardware.ClawBot;

@TeleOp(group = "Giancarlo")
public class GiancarloTeleop extends OpMode {
    int buttonPresses = 0;
    ClawBot robot = new ClawBot();
    double TargetXInches = 48;
    @Override
    public void init() {
        robot.init(hardwareMap,telemetry);
    }

    @Override
    public void loop() {
        robot.leftMotor.setPower(1);
        robot.leftMotor.setPower(1);
        robot.pinpoint.update();
        if (robot.pinpoint.getPosX(DistanceUnit.INCH)<=targetXInches);
        {
            robot.leftMotor.setPower(1);

        }
        double moveMultiplier = 0.5;
        double forward = -gamepad1.right_stick_y;
        double right = gamepad1.right_stick_x;

        if(gamepad1.right_bumper){
            moveMultiplier = 1;
        }

        forward *= moveMultiplier;
        right *= moveMultiplier;

        Pose2D currentPose = robot.pinpoint.getPosition();

        telemetry.addData("X Distance",currentPose.getX(DistanceUnit.INCH));
        telemetry.addData("Y Distance",currentPose.getY(DistanceUnit.INCH));
        telemetry.addData("Angle",currentPose.getHeading(AngleUnit.DEGREES));


        robot.leftMotor.setPower( forward + right );
        robot.rightMotor.setPower( forward - right);

        telemetry.addData("JoyLX", gamepad1.left_stick_x);

        if (gamepad1.aWasPressed()) {
            buttonPresses = buttonPresses + 1;
        }

        // if joystick is outside of the middle dead zone, show that it is pressed
        if (Math.abs(gamepad1.left_stick_x) > 0.5) {
            telemetry.addData("Message", "button is pressed!!!");
        } else {
            telemetry.addData("Message", "button is depressed!!!");
        }

        telemetry.addData("Button Presses", buttonPresses);
    }
}
