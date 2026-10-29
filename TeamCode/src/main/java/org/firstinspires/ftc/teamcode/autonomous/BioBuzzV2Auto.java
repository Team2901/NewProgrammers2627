package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.hardware.BioBuzzV2Robot;
import org.firstinspires.ftc.teamcode.utilities.Constants;
import org.firstinspires.ftc.teamcode.utilities.PoseUtilities;

@Autonomous(group = "BioBuzzV2")
public class BioBuzzV2Auto extends OpMode {

    BioBuzzV2Robot robot = new BioBuzzV2Robot();
    Constants.StartingLocation startingLocation = Constants.StartingLocation.AUDIENCE;

    @Override
    public void init() {
        robot.init(hardwareMap, telemetry);
    }

    @Override
    public void init_loop() {
        super.init_loop();

        Constants.Alliance alliance = robot.getAlliance();
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

        telemetry.addData("alliance", robot.getAlliance());
        telemetry.addData("startingLocation", startingLocation);
        telemetry.addData("currentPoseRelField", PoseUtilities.poseToString(robot.getPinpointPositon()));

        telemetry.update();
    }

    @Override
    public void loop() {
        robot.update();
        telemetry.addData("currentPoseRelField", PoseUtilities.poseToString(robot.getPinpointPositon()));
    }
}
