package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.utilities.Constants;
import org.firstinspires.ftc.teamcode.utilities.PoseUtilities;

public class Turret {

    public Telemetry telemetry;

    private static final double motorEncoderTicksPerRotation = ((((1 + (46 / 17.0))) * (1 + (46 / 11.0))) * 28); // from https://www.gobilda.com/5203-series-yellow-jacket-planetary-gear-motor-19-2-1-ratio-24mm-length-8mm-rex-shaft-312-rpm-3-3-5v-encoder/
    public static final double turretTicksPerRotation = motorEncoderTicksPerRotation * 5.0; // 5:1 from motor to turret

    private DcMotorEx pivotMotor;

    /**
     * Find the turret motor named "turret" and start counting its encoder from here, so the turret
     * must face forward when INIT is pressed. If the robot has no turret, a {@link MockDcMotor}
     * stands in so the rest of the code still runs. Call once from the OpMode's init.
     *
     * @param hardwareMap the OpMode's hardware map.
     * @param telemetry   the OpMode's telemetry, kept for reporting status.
     */
    public void init(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;

        try {
            pivotMotor = hardwareMap.get(DcMotorEx.class, "turret");
        } catch (IllegalArgumentException e) {
            pivotMotor = new MockDcMotor();
            telemetry.addLine("Can't find turret: making a mock");
        }

        // Encoder 0 is the turret facing forward, so it must be facing forward when INIT is pressed.
        pivotMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        pivotMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    /**
     * Per-loop update for the turret. Call once every loop; currently does nothing.
     */
    public void update() {

    }

    /**
     * Read the turret's angle from the motor encoder.
     *
     * @return the turret angle relative to the robot, in degrees: 0 faces the robot's front and
     * positive is counter-clockwise.
     */
    public double getTurretDegreesRelRobot() {
        return 360.0 * pivotMotor.getCurrentPosition() / turretTicksPerRotation;
    }

    /**
     * Set the turret power for one step of turning the turret to face a point on the field.
     * <p>
     * This does not wait; call it every loop. Does nothing if
     * the robot has no turret.
     * <p>
     * The turret can only turn 180 degrees each way from facing forward, so the error is taken
     * without wrapping around: a turret at 170 aiming at -170 turns the long way through 0
     * rather than into its hard stop.
     *
     * @param targetPoseRelField the point to face, in field coordinates; only its X and Y are
     *                           used, its heading is ignored.
     * @param currentPoseRelField the robot's pose in field coordinates.
     */
    public void aimTurretAtRelField(Pose2D targetPoseRelField, Pose2D currentPoseRelField) {
        // Step 1. Get the field heading from the robot to the target point.
        // pose2DDelta gives the X and Y from the robot to the target; atan2 turns them into a heading.
        Pose2D poseDeltaRelField = PoseUtilities.pose2DDelta(targetPoseRelField, currentPoseRelField);
        double targetHeadingRelField = Math.toDegrees(Math.atan2(
                poseDeltaRelField.getY(DistanceUnit.INCH),
                poseDeltaRelField.getX(DistanceUnit.INCH)));

        // Step 2. Turn that into a turret angle relative to the robot, -180 to 180.
        double targetTurretDegrees = PoseUtilities.angleDiffDegrees(targetHeadingRelField, currentPoseRelField.getHeading(AngleUnit.DEGREES));

        // Step 3. Power the turret toward it, slowing down within 30 degrees.
        double turretError = PoseUtilities.getError(targetTurretDegrees - getTurretDegreesRelRobot(), 1.0 / 30, 1, 0);
        pivotMotor.setPower(turretError);
    }

    /**
     * Set the turret power for one step of aiming at the alliance's goal on the robot's half of
     * the field (see {@link Constants#getGoalPose}). Call it every loop, like {@link #aimTurretAtRelField}.
     *
     * @param alliance            the alliance whose goal to aim at.
     * @param currentPoseRelField the robot's pose in field coordinates.
     */
    public void aimAtClosestGoal(Constants.Alliance alliance, Pose2D currentPoseRelField) {
        Pose2D goalPose = Constants.getGoalPose(alliance, currentPoseRelField);
        aimTurretAtRelField(goalPose, currentPoseRelField);
    }
}