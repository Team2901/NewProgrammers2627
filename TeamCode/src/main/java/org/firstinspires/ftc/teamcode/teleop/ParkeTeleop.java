package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(group = "Parke")
public class ParkeTeleop extends OpMode {
    public static final double SPEEDFACTOR = 1.25;
    public static final int TURNRATIO = 2;
    int buttonPresses = 0;
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

        leftDrive.setPower((gamepad1.right_stick_x/TURNRATIO-gamepad1.right_stick_y)/SPEEDFACTOR);
        rightDrive.setPower((-gamepad1.right_stick_x/TURNRATIO-gamepad1.right_stick_y)/SPEEDFACTOR);

        telemetry.addData("JoyLX", gamepad1.left_stick_x);
        telemetry.addData("JoyLY", -gamepad1.left_stick_y);
        telemetry.addData("JoyRX", gamepad1.right_stick_x);
        telemetry.addData("JoyRY", -gamepad1.right_stick_y);

        if (gamepad1.bWasPressed()) {
            buttonPresses = buttonPresses + 1;
        }
        if (gamepad1.b == true) {
            telemetry.addData("B Button Presses", buttonPresses);
        } else {
            telemetry.addData("B Button Presses", buttonPresses);
        }

    }
}
