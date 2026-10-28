package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.hardware.MechanumBot;

@Autonomous(group = "MechanumBot")
public class MechanumBotAuto extends OpMode {

    MechanumBot robot = new MechanumBot();
    Pose2D redAudienceStartingPose = new Pose2D(DistanceUnit.INCH, -12, -72 + 9, AngleUnit.DEGREES, 90);

    Pose2D redFarStartingPose = new Pose2D(DistanceUnit.INCH,
            redAudienceStartingPose.getX(DistanceUnit.INCH),
            -redAudienceStartingPose.getY(DistanceUnit.INCH), AngleUnit.DEGREES,
            -redAudienceStartingPose.getHeading(AngleUnit.DEGREES));

    Pose2D blueAudienceStartingPose = new Pose2D(DistanceUnit.INCH,
            -redAudienceStartingPose.getX(DistanceUnit.INCH),
            redAudienceStartingPose.getY(DistanceUnit.INCH), AngleUnit.DEGREES,
            redAudienceStartingPose.getHeading(AngleUnit.DEGREES));

    Pose2D blueFarStartingPose = new Pose2D(DistanceUnit.INCH,
            blueAudienceStartingPose.getX(DistanceUnit.INCH),
            -blueAudienceStartingPose.getY(DistanceUnit.INCH), AngleUnit.DEGREES,
            -blueAudienceStartingPose.getHeading(AngleUnit.DEGREES));

    @Override
    public void init() {
        robot.init(hardwareMap, telemetry);
    }

    @Override
    public void init_loop() {
        super.init_loop();

        Pose2D startingPose = robot.pinpoint.getPosition();

        if (gamepad1.x) {
            startingPose = redAudienceStartingPose;
        } else if (gamepad1.b) {
            startingPose = blueAudienceStartingPose;
        } else if (gamepad1.y) {
            startingPose = redFarStartingPose;
        } else if (gamepad1.a) {
            startingPose = blueFarStartingPose;
        }
        robot.setPinpointPositon(startingPose);

        telemetry.addLine("Select Alliance:");
        telemetry.addLine("X = RED AUDIENCE");
        telemetry.addLine("B = BLUE AUDIENCE");
        telemetry.addLine("Y = RED FAR");
        telemetry.addLine("B = BLUE FAR");

        robot.poseTelemetry("currentPoseRelField", robot.pinpoint.getPosition());

        telemetry.update();
    }

    @Override
    public void loop() {
        robot.update();
        robot.poseTelemetry("currentPoseRelField", robot.pinpoint.getPosition());
    }
}
