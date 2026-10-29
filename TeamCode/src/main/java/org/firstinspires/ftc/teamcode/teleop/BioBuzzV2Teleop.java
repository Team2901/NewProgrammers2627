package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.hardware.BioBuzzV2Robot;
import org.firstinspires.ftc.teamcode.utilities.Constants;
import org.firstinspires.ftc.teamcode.utilities.PoseUtilities;

/**
 * Field-centric teleop for the BioBuzz V2 robot.
 * <p>
 * Gamepad 1 controls:
 * <ul>
 *     <li>Left stick: drive in a field direction. Up drives toward field +Y (the BLUE side) and
 *     right drives toward field +X (away from the audience), whichever way the robot faces.</li>
 *     <li>Right stick: point it the way the robot should face; the robot turns to that field
 *     heading. Let go and the robot stops turning.</li>
 *     <li>A (hold): drive automatically to {@link #targetPose}.</li>
 *     <li>B: tell the Pinpoint the robot is at our alliance's audience starting pose.</li>
 * </ul>
 * The turret aims at the alliance's nearest goal the whole time.
 */
@TeleOp(group = "BioBuzzV2")
public class BioBuzzV2Teleop extends OpMode {

    BioBuzzV2Robot robot = new BioBuzzV2Robot();

    // Where holding A drives the robot to: the center of the field, facing field +Y.
    Pose2D targetPose = new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 90);

    @Override
    public void init() {
        // Reads the alliance the auto recorded, and sets up all the hardware.
        robot.init(hardwareMap, telemetry);
    }

    @Override
    public void loop() {

        // Refresh the robot state.
        robot.update();

        // Read where the robot is and which way it faces on the field.
        Pose2D currentPoseRelField = robot.getPinpointPositon();
        double robotHeadingRelField = currentPoseRelField.getHeading(AngleUnit.DEGREES);

        // Keep the turret pointed at our goal, whatever the driver is doing.
        robot.turret.aimAtClosestGoal(robot.getAlliance(), currentPoseRelField);

        // Drive.
        if (gamepad1.a) {
            // Holding A: drive automatically toward targetPose.
            robot.driveBase.moveTowardsRelField(targetPose, currentPoseRelField);
        } else {
            // Right stick sets the field heading to face. The stick's Y is negated because
            // pushing it up reads as negative.
            double rightStickMag = Math.hypot(-gamepad1.right_stick_y, gamepad1.right_stick_x);
            double counterClockwisePower;
            if (rightStickMag > 0.1) {
                // The stick's angle is the field heading to face: right is 0 (field +X),
                // up is 90 (field +Y).
                double rightStickAngle = Math.toDegrees(Math.atan2(-gamepad1.right_stick_y, gamepad1.right_stick_x));
                // How far the robot still has to turn to face that heading.
                double targetHeadingRelRobot = PoseUtilities.angleDiffDegrees(rightStickAngle, robotHeadingRelField);
                // Turn at full power, slowing down within 30 degrees.
                counterClockwisePower = PoseUtilities.getError(targetHeadingRelRobot, 1.0 / 30, 1, 0);
            } else {
                // Stick near the center (dead zone): don't turn.
                counterClockwisePower = 0;
            }
            // Left stick drives in field directions; its Y is negated because up reads as negative.
            robot.driveBase.movePowerRelField(gamepad1.left_stick_x, -gamepad1.left_stick_y, counterClockwisePower, robotHeadingRelField);
        }

        // Resets the Pinpoint position, e.g. after placing the robot at our alliance's
        // audience starting spot when testing without running auto first.
        if (gamepad1.b) {
            robot.setPinpointPositon(Constants.getStartingPose(robot.getAlliance(), Constants.StartingLocation.AUDIENCE));
        }

        // Report Telemetry
        telemetry.addData("alliance", robot.getAlliance());
        telemetry.addData("currentPoseRelField", PoseUtilities.poseToString(currentPoseRelField));
        telemetry.addData("poseDelta", PoseUtilities.poseToString(PoseUtilities.pose2DDelta(targetPose, currentPoseRelField)));
    }
}
