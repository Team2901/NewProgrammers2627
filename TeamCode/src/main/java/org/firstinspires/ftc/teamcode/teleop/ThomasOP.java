package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(group = "Thomas")
public class ThomasOP extends OpMode {
    int buttonPresses = 0;
    DcMotor leftMotor;
    DcMotor rightMotor;

    @Override
    public void init() {

        telemetry.addData("name", "ThomasLeung");

        leftMotor = hardwareMap.get(DcMotor.class, "leftDrive");
        rightMotor = hardwareMap.get(DcMotor.class, "rightDrive");

        leftMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        rightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    @Override
    public void loop() {
        leftMotor.setPower(gamepad1.right_stick_y);
        rightMotor.setPower(gamepad1.right_stick_y);

        telemetry.addData("joyLx", gamepad1.left_stick_x);

        if (gamepad1.a) {
            buttonPresses = buttonPresses + 1;
        }

        if (gamepad1.a) {
            telemetry.addData("message", "A is being pressed now");
        } else {
            telemetry.addData("message", "A is not being pressed now");
        }
        telemetry.addData("ButtonPresses", buttonPresses);
    }
}
