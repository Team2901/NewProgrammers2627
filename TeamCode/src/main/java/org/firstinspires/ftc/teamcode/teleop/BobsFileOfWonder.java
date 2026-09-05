package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(group = "David")
public class BobsFileOfWonder extends OpMode {
    int buttonPresses = 0;
    boolean xButton = false;

    @Override
    public void init() {
        telemetry.addData("Bob", "Calvin is a Cutie");
    }

    @Override
    public void loop() {

        telemetry.addData("JoyLX", gamepad1.left_stick_x);
        telemetry.addData("JoyLY", gamepad1.left_stick_y);
        telemetry.addData("JoyRX", gamepad2.left_stick_x);
        telemetry.addData("JoyRY", gamepad2.left_stick_y);

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

        if (gamepad1.xWasPressed()) {
            xButton = true;
            telemetry.addData("X", xButton);
        }

        if (gamepad1.xWasReleased()) {
            xButton = false;
            telemetry.addData("X", xButton);
        }

    }
}

