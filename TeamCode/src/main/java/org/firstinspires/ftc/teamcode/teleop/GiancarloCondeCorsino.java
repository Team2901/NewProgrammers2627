package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(group = "Giancarlo")
public class GiancarloCondeCorsino extends OpMode {
    DcMotor leftDrive;
    DcMotor rightDrive;

    @Override
    public void init() {
        telemetry.addData("Name", "Giancarlo");
        leftDrive = hardwareMap.get(DcMotor.class, "leftDrive");
        rightDrive = hardwareMap.get(DcMotor.class, "rightDrive");
        leftDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        rightDrive.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    @Override
    public void loop() {
        leftDrive.setPower(-gamepad1.right_stick_y);
        rightDrive.setPower(-gamepad1.right_stick_x);
    }
}

