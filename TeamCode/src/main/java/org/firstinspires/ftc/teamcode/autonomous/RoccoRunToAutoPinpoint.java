package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.hardware.ClawBot;

@Autonomous(group = "Rocco")
public class RoccoRunToAutoPinpoint extends OpMode {
    int buttonPresses = 0;
    ClawBot robot = new ClawBot();
    double targetXInches = 48;

    @Override
    public void init() {
        robot.init(hardwareMap, telemetry);
    }

    @Override
    public void loop() {
        robot.pinpoint.update();
        robot.leftMotor.setPower(1);
        robot.leftMotor.setPower(1);

        Pose2D currentPose = robot.pinpoint.getPosition();
        telemetry.addData("Heading", currentPose.getHeading(AngleUnit.DEGREES));
        telemetry.addData("X Coordinate", currentPose.getX(DistanceUnit.INCH));
        telemetry.addData("Y Coordinate", currentPose.getY(DistanceUnit.INCH));
    }
}
