package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(group = "Rocco")
public class RoccoTeleop extends OpMode {
    int buttonPresses = 0;
    DcMotor leftMotor;
    DcMotor rightMotor;

    @Override
    public void init() {

        telemetry.addData("Name", "Rocco");

        leftMotor = hardwareMap.get(DcMotor.class, "leftDrive");
        rightMotor = hardwareMap.get(DcMotor.class, "rightDrive");

        leftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        rightMotor.setDirection(DcMotorSimple.Direction.FORWARD);

    }

    @Override
    public void loop() {

        double moveMultiplier = 0.5;
        double forward = -gamepad1.right_stick_y;
        double right = gamepad1.right_stick_x;

        if(gamepad1.right_bumper){
            moveMultiplier = 1;
        }

        forward *= moveMultiplier;
        right *= moveMultiplier;

        leftMotor.setPower( forward + right );
        rightMotor.setPower( forward - right);

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
