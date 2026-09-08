package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(group = "Parke")
public class ParkeTeleop extends OpMode {
    int bButtonPresses = 0;
    DcMotor leftDrive;
    DcMotor rightDrive;

    public void init() {
        telemetry.addData("Name", "Parke");
        leftDrive = hardwareMap.get(DcMotor.class,"leftDrive");
        rightDrive = hardwareMap.get(DcMotor.class,"rightDrive");
        leftDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        rightDrive.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    @Override
    public void loop() {
        double moveMultiplier = 0.5;
        if (gamepad1.right_bumper) {
            moveMultiplier = 1;
        }
        double foreward = -gamepad1.right_stick_y;
        double right = gamepad1.right_stick_x;

        foreward = foreward * moveMultiplier;
        right = right * moveMultiplier;

        leftDrive.setPower(foreward + right);
        rightDrive.setPower(foreward - right);

        telemetry.addData("JoyLX", gamepad1.left_stick_x);
        telemetry.addData("JoyLY", -gamepad1.left_stick_y);
        telemetry.addData("JoyRX", gamepad1.right_stick_x);
        telemetry.addData("JoyRY", -gamepad1.right_stick_y);

        telemetry.addData("BButton", bButtonPresses);

        if (gamepad1.bWasPressed()) {
            bButtonPresses = bButtonPresses + 1;
        }
    }
}
