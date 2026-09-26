package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Autonomous
public class DanielAutonomous extends OpMode {
    DcMotor frontRight;
    DcMotor frontLeft;
    DcMotor backLeft;
    DcMotor backRight;


    double targetDistanceInches = 4 * 12;
    double wheelRadiusInches = 1.88976378;
    double wheelCircumferenceInches = 2 * Math.PI * wheelRadiusInches;
    double targetWheelRotations = targetDistanceInches / wheelCircumferenceInches;
    double motorToWheelRatio = 1.0/1.0;
    double targetMotorRotations = targetWheelRotations * motorToWheelRatio;
    double motorEncoderTicksPerRotation = 537.7;
    int targetMotorEncoderTicks = (int) (targetMotorRotations * motorEncoderTicksPerRotation);

    @Override
    public void init() {

        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");

        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.FORWARD);

    }

    @Override
    public void loop() {

        frontRight.setTargetPosition(targetMotorEncoderTicks);
        frontLeft.setTargetPosition(targetMotorEncoderTicks);
        backLeft.setTargetPosition(targetMotorEncoderTicks);
        backRight.setTargetPosition(targetMotorEncoderTicks);

        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        frontRight.setPower(1);
        frontLeft.setPower(1);
        backRight.setPower(1);
        backLeft.setPower(1);

        telemetry.addData("power", frontRight.getPower());
        telemetry.addData("mode", frontRight.getMode());
        telemetry.addData("target", frontRight.getTargetPosition());
        telemetry.addData("current", frontRight.getCurrentPosition());
        telemetry.addData("busy", frontRight.isBusy());
    }
}