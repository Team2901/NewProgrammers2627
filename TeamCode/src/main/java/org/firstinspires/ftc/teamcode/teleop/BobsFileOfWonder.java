package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(group = "David")
public class BobsFileOfWonder extends OpMode {
    public static final int TURN_RATIO = 2;
    public static final double SPEED_FACTOR = 1.25;
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

        rightDrive.setPower((gamepad1.right_stick_x/TURN_RATIO + gamepad1.right_stick_y)/SPEED_FACTOR);
        leftDrive.setPower((gamepad1.right_stick_y - (gamepad1.right_stick_x/TURN_RATIO))/SPEED_FACTOR);

        telemetry.addData("RPower", rightDrive);
        telemetry.addData("RPower", leftDrive);
        telemetry.addData("JoyRX", gamepad1.right_stick_x);
        telemetry.addData("JoyRY", -gamepad1.right_stick_y);

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


