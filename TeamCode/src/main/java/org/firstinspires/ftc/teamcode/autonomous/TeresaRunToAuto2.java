package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Autonomous
public class TeresaRunToAuto2 extends OpMode {
    DcMotor frontLeft;
    DcMotor frontRight;
    DcMotor backLeft;
    DcMotor backRight;


    double targetDistanceInches = 4 * 12; // 4 feet
    double wheelRadiusInches = 1.88976378;
    double wheelCircumferenceInches = 2 * Math.PI * wheelRadiusInches;
    double targetWheelRotations = targetDistanceInches / wheelCircumferenceInches;
    double motorToWheelGearRatio = 1.0; // # of wheel gear teeth / # of motor gear teeth
    double targetMotorRotations = targetWheelRotations * motorToWheelGearRatio;
    double motorEncoderTicksPerRotation = ((((1+(46/17.0))))) * (1+(46/11.0)) * 28;
    int targetMotorEncoderTicks = (int) (targetMotorRotations * motorEncoderTicksPerRotation);


    @Override
    public void init() {

        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");

        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.FORWARD);

        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

    }

    @Override
    public void loop() {
        
        frontLeft.setTargetPosition(targetMotorEncoderTicks);
        frontRight.setTargetPosition(targetMotorEncoderTicks);
        backLeft.setTargetPosition(targetMotorEncoderTicks);
        backRight.setTargetPosition(targetMotorEncoderTicks);

        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        frontLeft.setPower(1);
        frontRight.setPower(1);
        backLeft.setPower(1);
        backRight.setPower(1);

        telemetry.addData("power", frontLeft.getPower());
        telemetry.addData("mode", frontLeft.getMode());
        telemetry.addData("target", frontLeft.getTargetPosition());
        telemetry.addData("current", frontLeft.getCurrentPosition());
        telemetry.addData("busy", frontLeft.isBusy());
        telemetry.addData("power", backLeft.getPower());
        telemetry.addData("mode", backLeft.getMode());
        telemetry.addData("target", backLeft.getTargetPosition());
        telemetry.addData("current", backLeft.getCurrentPosition());
        telemetry.addData("busy", backLeft.isBusy());

    }
}
