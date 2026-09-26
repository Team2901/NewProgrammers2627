package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(group = "Mary")
public class TankOpMode extends OpMode {
    int buttonPresses = 0;
    DcMotor leftMotor;
    DcMotor rightMotor;

    @Override
    public void init() {
        telemetry.addData("Name", "Mary");
        leftMotor = hardwareMap.get(DcMotor.class, "leftDrive");
        rightMotor = hardwareMap.get(DcMotor.class, "rightDrive");
        leftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        rightMotor.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    @Override
    public void loop() {

        double moveMultiplier = 1.0;
        double foward = -gamepad1.right_stick_y;
        double right = gamepad1.right_stick_x;

        if (gamepad1.right_bumper) {
            moveMultiplier = 0.5;
        }

        foward = foward * moveMultiplier;
        right = right * moveMultiplier;

        leftMotor.setPower(-gamepad1.left_stick_y);
        rightMotor.setPower(-gamepad1.right_stick_y);

        telemetry.addData("JoyLX",gamepad1.left_stick_x);

        if (gamepad1.aWasPressed()) {
            buttonPresses = buttonPresses + 1;
        }

        telemetry.addData("JoyLX", gamepad1.left_stick_x);
        telemetry.addData("JoyLY", gamepad1.left_stick_y);
        telemetry.addData("JoyRX", gamepad1.right_stick_x);
        telemetry.addData("JoyRY", gamepad1.right_stick_y);
        telemetry.addData("left drive power", leftMotor.getPower());
        telemetry.addData("left drive power", rightMotor.getPower());
        if (gamepad1.a) {
            telemetry.addData("name", "hey");

        }
    }
}