package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(group = "Daniel")
public class DanielTeleop extends OpMode {

    DcMotor leftDrive;
    DcMotor rightDrive;

    @Override
    public void init() {
        telemetry.addData("Name", "Daniel");
        leftDrive = hardwareMap.get(DcMotor.class, "leftDrive");
        rightDrive = hardwareMap.get(DcMotor.class, "rightDrive");
        leftDrive.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    int buttonPresses = 0;

    @Override

    public void loop() {
        leftDrive.setPower(-gamepad1.right_stick_y);
        rightDrive.setPower(-gamepad1.right_stick_y);

        telemetry.addData("JoyLX", gamepad1.left_stick_x);
        telemetry.addData("JoyLY", -gamepad1.left_stick_y);
        telemetry.addData("JoyRX", gamepad1.right_stick_x);
        telemetry.addData("JoyRY", -gamepad1.right_stick_y);

        if (gamepad1.aWasPressed()) {
            buttonPresses = buttonPresses + 1;
            telemetry.addData("message", "a is pressed");
        } else {
            telemetry.addData("message", "a is depressed");
        }

    }
}