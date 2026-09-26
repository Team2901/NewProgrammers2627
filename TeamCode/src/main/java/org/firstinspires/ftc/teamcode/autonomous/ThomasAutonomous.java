package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class ThomasAutonomous extends OpMode {
    DcMotor leftFrontDrive;
    DcMotor rightFrontDrive;
    DcMotor LeftBackDrive;
    DcMotor rightBackDrive;
    double targetDistanceInches = 4 * 12; // 4 feet
    double wheelRadiusInches = 1.88976378;
    double wheelCircumferenceInches = 2 * Math.PI * wheelRadiusInches;
    double targetWheelRotations = targetDistanceInches / wheelCircumferenceInches;
    double motorToWheelGearRatio = 1.0; // # of wheel gear teeth / # of motor gear teeth
    double targetMotorRotations = targetWheelRotations * motorToWheelGearRatio;
    double motorEncoderTicksPerRotation = ((((1 + (46 / 17.0))) * (1 + (46 / 11.0))) * 28); //537.7 usage
    int targetMotorEncoderTicks = (int) (targetMotorRotations * motorEncoderTicksPerRotation);


    @Override
    public void init() {
        telemetry.addData("name", "ThomasLeung");

        leftFrontDrive = hardwareMap.get(DcMotor.class, "leftDrive");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "rightDrive");

        leftFrontDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotorSimple.Direction.FORWARD);

        leftFrontDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFrontDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    @Override
    public void loop() {

        leftFrontDrive.setTargetPosition(targetMotorEncoderTicks);
        rightFrontDrive.setTargetPosition(targetMotorEncoderTicks);

        leftFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        leftFrontDrive.setPower(1);
        rightFrontDrive.setPower(1);

        telemetry.addData("Power", leftFrontDrive.getPower());
        telemetry.addData("mode", leftFrontDrive.getPower());
        telemetry.addData("target", leftFrontDrive.getTargetPosition());
        telemetry.addData("current", leftFrontDrive.getCurrentPosition());
        telemetry.addData("busy", leftFrontDrive.isBusy());

        telemetry.addData("wheelCircumferenceInches", wheelCircumferenceInches);
        telemetry.addData("targetWheelRotations", targetWheelRotations);
        telemetry.addData("targetWheelRotations", targetMotorRotations);
        telemetry.addData("targetMotorEncoderTicks", targetMotorEncoderTicks);
    }
}