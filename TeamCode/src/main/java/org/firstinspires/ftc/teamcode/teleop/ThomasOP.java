package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(group = "Thomas")
public class ThomasOP extends OpMode {
    int buttonPresses = 0;
    DcMotor leftDrive;
    DcMotor rightDrive;

    @Override
    public void init() {

        telemetry.addData("name", "ThomasLeung");

        leftDrive = hardwareMap.get(DcMotor.class, "leftDrive");
        rightDrive = hardwareMap.get(DcMotor.class, "rightDrive");

        leftDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        rightDrive.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    @Override
    public void loop() {

        double moveMultiplier = 0.45;
        double forward = -gamepad1.right_stick_y;
        double right= gamepad1.right_stick_x;

        forward = forward * moveMultiplier;
        right = right * moveMultiplier;

        leftDrive.setPower(-gamepad1.right_stick_y + gamepad1.right_stick_x);
        rightDrive.setPower(-gamepad1.right_stick_y - gamepad1.right_stick_x);

        telemetry.addData("joyLx", gamepad1.left_stick_x);

        }
}