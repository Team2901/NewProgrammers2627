package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.hardware.ClawBot;

@Autonomous
public class RoccoAuto extends OpMode {
    ClawBot robot = new ClawBot();
    double targetDistanceInches = 4 * 12; // 4 feet
    double targetWheelRotations = targetDistanceInches / robot.wheelCircumferenceInches;
    double tagetMotorRotations = targetWheelRotations * robot.motorToWheelGearRatio;
    int targetMotorEncoderTicks = (int) (tagetMotorRotations * robot.motorEncoderTicksPerRotation);

    @Override
    public void init() {
        robot.init(hardwareMap, telemetry);
    }

    @Override
    public void loop() {
        robot.leftMotor.setTargetPosition(targetMotorEncoderTicks);
        robot.rightMotor.setTargetPosition(targetMotorEncoderTicks);

        robot.leftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.rightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        robot.leftMotor.setPower(1);
        robot.rightMotor.setPower(1);

        telemetry.addData("power",robot.leftMotor.getPower());
        telemetry.addData("mode",robot.leftMotor.getMode());
        telemetry.addData("target",robot.leftMotor.getTargetPosition());
        telemetry.addData("current",robot.leftMotor.getCurrentPosition());
        telemetry.addData("busy",robot.leftMotor.isBusy());
    }
}
