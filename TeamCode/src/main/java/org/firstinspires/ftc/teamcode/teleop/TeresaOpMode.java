package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(group = "Teresa")
public class TeresaOpMode extends OpMode {
    int buttonPresses = 0;
    DcMotor leftMotor;
    DcMotor rightMotor;


    @Override
    public void init() {
        telemetry.addData("name", "chickfila");
        leftMotor = hardwareMap.get(DcMotor.class, "leftDrive");
        rightMotor = hardwareMap.get(DcMotor.class, "rightDrive");
        leftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        rightMotor.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    @Override
    public void loop() {
        leftMotor.setPower(gamepad1.left_stick_y);
        rightMotor.setPower(gamepad1.left_stick_y);
        telemetry.addData("JoyLX", gamepad1.left_stick_x);
        telemetry.addData("JoyLY", gamepad1.left_stick_y);
        telemetry.addData("JoyRX", gamepad1.right_stick_x);
        telemetry.addData("JoyRY", gamepad1.right_stick_y);
        telemetry.addData("left drive power", leftMotor.getPower());
        telemetry.addData("right drive power", rightMotor.getPower());
        if (gamepad1.a) {
            telemetry.addData("name", "hey");


        }
    }
}
