package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.hardware.MechanumBot;
import org.firstinspires.ftc.teamcode.utilities.Constants;

@Autonomous(group = "MechanumBot")
public class MechanumBotAuto extends OpMode {

    MechanumBot robot = new MechanumBot();
    Constants.StartingLocation startingLocation = Constants.StartingLocation.AUDIENCE;

    @Override
    public void init() {
        robot.init(hardwareMap, telemetry);
    }

    @Override
    public void init_loop() {
        super.init_loop();

        Constants.Alliance alliance = robot.alliance;
        if (gamepad1.x) {
            alliance = Constants.Alliance.RED;
            startingLocation = Constants.StartingLocation.AUDIENCE;
        } else if (gamepad1.a) {
            alliance = Constants.Alliance.BLUE;
            startingLocation = Constants.StartingLocation.AUDIENCE;
        } else if (gamepad1.y) {
            alliance = Constants.Alliance.RED;
            startingLocation = Constants.StartingLocation.FAR;
        } else if (gamepad1.b) {
            alliance = Constants.Alliance.BLUE;
            startingLocation = Constants.StartingLocation.FAR;
        }

        if (startingLocation != null) {
            robot.setPinpointPositon(Constants.getStartingPose(alliance, startingLocation));
        }

        // Record the alliance so the teleop that follows knows which goals to aim at.
        robot.setAlliance(alliance);

        telemetry.addLine("Select Alliance:");
        telemetry.addLine("X = RED AUDIENCE");
        telemetry.addLine("A = BLUE AUDIENCE");
        telemetry.addLine("Y = RED FAR");
        telemetry.addLine("B = BLUE FAR");

        telemetry.addData("alliance", robot.alliance);
        telemetry.addData("startingLocation", startingLocation);
        robot.poseTelemetry("currentPoseRelField", robot.pinpoint.getPosition());

        telemetry.update();
    }

    @Override
    public void loop() {
        robot.update();
        robot.poseTelemetry("currentPoseRelField", robot.pinpoint.getPosition());
    }
}
