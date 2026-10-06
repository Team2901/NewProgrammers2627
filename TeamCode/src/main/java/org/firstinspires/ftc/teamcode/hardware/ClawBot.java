package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ClawBot {

    public DcMotor leftMotor;
    public DcMotor rightMotor;
    public double wheelRadiusInches = 1.88976378;
    public double wheelCircumferenceInches = 2 * Math.PI * wheelRadiusInches;
    public double motorToWheelGearRatio = 1.0 / 1.0; // # of wheel gear teeth / # of motor gear teeth
    public double motorEncoderTicksPerRotation = ((((1 + (46 / 17.0))) * (1 + (46 / 11.0))) * 28); // from https://www.gobilda.com/5203-series-yellow-jacket-planetary-gear-motor-19-2-1-ratio-24mm-length-8mm-rex-shaft-312-rpm-3-3-5v-encoder/
    public void init(HardwareMap hardwareMap, Telemetry telemetry){

        leftMotor = hardwareMap.get(DcMotor.class, "leftDrive");
        rightMotor = hardwareMap.get(DcMotor.class, "rightDrive");

        leftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        rightMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    }