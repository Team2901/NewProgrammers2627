package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(group = "Thomas")
public class ThomasOP extends OpMode {
    int buttonPresses = 0;

    @Override
    public void init() {
        telemetry.addData("name", "ThomasLeung");
    }

    @Override
    public void loop() {

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
