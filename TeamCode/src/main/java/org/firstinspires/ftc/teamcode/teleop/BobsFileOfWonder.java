package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(group = "David")
public class BobsFileOfWonder extends OpMode {
    int buttonPresses = 0;
    DcMotor leftDrive;
    DcMotor rightDrive;

    @Override
    public void init() {
        telemetry.addData("Bob", "I think its working");

        leftDrive = hardwareMap.get(DcMotor.class, "leftDrive");
        rightDrive = hardwareMap.get(DcMotor.class, "rightDrive");

        leftDrive.setDirection(DcMotorSimple.Direction.FORWARD);
        rightDrive.setDirection(DcMotorSimple.Direction.REVERSE);
    }


    @Override
    public void loop() {

        leftDrive.setPower(-gamepad1.right_stick_y);
        rightDrive.setPower(-gamepad1.right_stick_y);

        telemetry.addData("JoyLX", gamepad1.left_stick_x);
        telemetry.addData("JoyLY", -gamepad1.left_stick_y);
        telemetry.addData("JoyRX", gamepad2.left_stick_x);
        telemetry.addData("JoyRY", -gamepad2.left_stick_y);

        if (gamepad1.aWasPressed()) {
            buttonPresses = buttonPresses + 1;
            telemetry.addData("APressed", "Happy");
        } else {
            telemetry.addData("ANotPressed", "Sad");
        }
        if (gamepad1.left_stick_x > 0.5) {
            telemetry.addData("Yes", "Yes");
        }

        telemetry.addData("Button Presses", buttonPresses);

        }

    }


