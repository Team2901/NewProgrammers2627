package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Autonomous
public class TeresaRunToAuto extends OpMode {
    DcMotor leftMotor;
    DcMotor rightMotor;


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

        leftMotor = hardwareMap.get(DcMotor.class, "leftDrive");
        rightMotor = hardwareMap.get(DcMotor.class, "rightDrive");

        leftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        rightMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

    }

    @Override
    public void loop() {


        int targetPosition = 21280;
        leftMotor.setTargetPosition(targetMotorEncoderTicks);
        rightMotor.setTargetPosition(targetMotorEncoderTicks);

        leftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        leftMotor.setPower(1);
        rightMotor.setPower(1);

        telemetry.addData("power", leftMotor.getPower());
        telemetry.addData("mode", leftMotor.getMode());
        telemetry.addData("target", leftMotor.getTargetPosition());
        telemetry.addData("current", leftMotor.getCurrentPosition());
        telemetry.addData("busy", leftMotor.isBusy());

    }
}
