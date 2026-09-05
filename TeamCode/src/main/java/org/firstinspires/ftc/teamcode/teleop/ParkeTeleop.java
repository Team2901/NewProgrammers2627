package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(group = "Parke")
public class ParkeTeleop extends OpMode {
    @Override
    public void init() {
        telemetry.addData("Name", "Parke");
    }

    @Override
    public void loop() {
        telemetry.addData("Is daniel cute", gamepad1.left_stick_x);
        telemetry.addData("Is Daniel a cutie", gamepad2.right_stick_x);

        if (gamepad1.a) {
            telemetry.addData("hi", "hii");
        } else {
            telemetry.addData("h2", "hi2");
        }
    }
}
