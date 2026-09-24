package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Autonomous
public class DanielAutonomous extends OpMode {
    DcMotor leftMotor;
    DcMotor rightMotor;

    double targetDistanceInches = 4 * 12;
    double wheelRadiusInches = 1.88976378;
    double wheelCircumferenceInches = 2 * Math.PI * wheelRadiusInches;
    double targetWheelRotations = targetDistanceInches / wheelCircumferenceInches;
    double motorToWheelRatio = 1.0/1.0;
    double targetMotorRotations = targetWheelRotations * motorToWheelRatio;
    double motorEncoderTicksPerRotation = 537.7;
    double targetMotorEncoderTicks = (int) (targetMotorRotations * motorEncoderTicksPerRotation);

    @Override
    public void init() {

        leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftMotor = hardwareMap.get(DcMotor.class, "leftDrive");
        rightMotor = hardwareMap.get(DcMotor.class, "rightDrive");

        leftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        rightMotor.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    @Override
    public void loop() {

        leftMotor.setTargetPosition();
        rightMotor.setTargetPosition(21280);

        leftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        leftMotor.setPower(1);
        rightMotor.setPower(1);

        telemetry.addData("power", leftMotor.getPower());
        telemetry.addData("mode",leftMotor.getMode());
        telemetry.addData("target", leftMotor.getTargetPosition());
        telemetry.addData("current", leftMotor.getCurrentPosition());
        telemetry.addData("busy", leftMotor.isBusy());
    }
}
