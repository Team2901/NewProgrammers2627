package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(group = "MsMargaret")
public class MsMargaretTeleop extends OpMode {
    int buttonPresses = 0;

    @Override
    public void init() {
        telemetry.addData("Name", "Ms Margaret");
    }

    @Override
    public void loop() {

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
