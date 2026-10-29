package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.utilities.PoseUtilities;

public class MechanumDriveBase {

    public Telemetry telemetry;

    public DcMotorEx frontLeft;
    public DcMotorEx backLeft;
    public DcMotorEx frontRight;
    public DcMotorEx backRight;

    /**
     * Find the four drive motors, set their directions so positive power drives forward, and
     * reset their encoders. Call once from the OpMode's init.
     *
     * @param hardwareMap the OpMode's hardware map, used to find each motor by its configured name.
     * @param telemetry   the OpMode's telemetry, kept for reporting status.
     */
    public void init(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;

        frontLeft = hardwareMap.get(DcMotorEx.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
        backLeft = hardwareMap.get(DcMotorEx.class, "backLeft");
        backRight = hardwareMap.get(DcMotorEx.class, "backRight");

        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.FORWARD);

        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        frontLeft.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
    }

    /**
     * Per-loop update for the drive base. Call once every loop; currently does nothing.
     */
    public void update() {
    }

    /**
     * Set the drive motor powers for one step of a move toward a target pose on the field.
     * <p>
     * This does not wait for the robot to arrive. It looks at where the robot is now, sets the
     * motor powers once, and returns. Call it repeatedly in a loop; each call adjusts the power
     * as the robot gets closer, and the caller decides when the robot is close enough to stop.
     * <p>
     * The power for X, Y and heading each follows the DiffUtilities.getError profile: full power far
     * from the target, ramping down linearly once within 2 ft (or 30 degrees for heading), and
     * never below stall power.
     *
     * @param targetPoseRelField  the pose to move toward, in field coordinates: X and Y position
     *                            plus the heading the robot should end up facing.
     * @param currentPoseRelField the pose the robot is at in field coordinates
     */
    public void moveTowardsRelField(Pose2D targetPoseRelField, Pose2D currentPoseRelField) {

        // Step 1. Get the difference between the target pose and the current pose
        // Use pose2DDelta(targetPoseRelField, currentPose).
        // This is how far the robot still has to go in field X, field Y and heading.
        Pose2D poseDeltaRelField = PoseUtilities.pose2DDelta(targetPoseRelField, currentPoseRelField);

        // Step 2. Turn the X and Y differences into field relative X and Y powers
        // Call getError,
        // e.g. errorScaler 1.0 / 24 so the robot slows down within 2 ft of the target.
        double xPowerRelField = PoseUtilities.getError(poseDeltaRelField.getX(DistanceUnit.INCH), 1.0 / 24, 1, 0.01);
        double yPowerRelField = PoseUtilities.getError(poseDeltaRelField.getY(DistanceUnit.INCH), 1.0 / 24, 1, 0.01);

        // Step 3. Turn the heading difference into a counter-clockwise power
        // Call getError,
        // e.g. errorScaler 1.0 / 30 so the robot slows down within 30 degrees.
        double counterClockwisePower = PoseUtilities.getError(poseDeltaRelField.getHeading(AngleUnit.DEGREES), 1.0 / 30, 1, 0.01);

        // Step 4. Drive by calling movePowerRelField
        // Pass the powers from steps 3 and 4 and the robot's current heading.
        // It converts the field relative powers to robot relative ones and sets the motors.
        movePowerRelField(xPowerRelField, yPowerRelField, counterClockwisePower, currentPoseRelField.getHeading(AngleUnit.DEGREES));
    }

    /**
     * Drive the robot using powers given in the field's coordinate system (field-centric drive).
     * <p>
     * Field positive X is always away from the audience; field positive Y is always toward
     * the BLUE side. The robot moves in the requested field direction no matter which way it
     * is facing: the powers are converted to the robot's coordinate system with
     * {@link #translatePowerToRelRobot} and then applied with {@link #movePowerRelRobot}.
     *
     * @param xPowerRelField              power to apply in the field relative X direction, -1 to 1.
     * @param yPowerRelField              power to apply in the field relative Y direction, -1 to 1.
     * @param counterClockwisePower       power to apply to rotation, -1 to 1; positive turns counter-clockwise.
     * @param robotHeadingDegreesRelField robot heading in the field coordinate system, in degrees;
     *                                    0 faces field +X and positive is counter-clockwise.
     */
    public void movePowerRelField(double xPowerRelField, double yPowerRelField, double counterClockwisePower, double robotHeadingDegreesRelField) {
        // Step 1. Convert from Field coordinate system to robot coordinate system.
        double[] xyPowerRelRobot = translatePowerToRelRobot(xPowerRelField, yPowerRelField, robotHeadingDegreesRelField);

        // Step 2. Move that robot in that direction.
        movePowerRelRobot(xyPowerRelRobot[0], xyPowerRelRobot[1], counterClockwisePower);
    }

    /**
     * Convert a power given in the field's coordinate system into the power to apply in the
     * robot's own coordinate system, given the robot's current heading on the field.
     * <p>
     * Field positive X is always away from the audience; field positive Y is always toward
     * the BLUE side.
     *
     * @param xDiffRelField               power to apply in the field relative X direction.
     * @param yDiffRelField               power to apply in the field relative Y direction.
     * @param robotHeadingDegreesRelField robot heading in the field coordinate system, in degrees.
     * @return a two-element array {xPowerRelRobot, yPowerRelRobot}: the forward and left power
     * to pass to {@link #movePowerRelRobot}.
     */
    public double[] translatePowerToRelRobot(double xDiffRelField, double yDiffRelField, double robotHeadingDegreesRelField) {
        // Step 1. Calculate the magnitude of the xy values.
        // We currently have a power to apply in the X direction and a power to apply in the Y direction.
        // Get the power to apply along the hypotenuse of the right triangle formed by X and Y values.
        // Hypotenuse equation. H = squareRoot(x**2 + y**2) which Math.hypot does for you.
        double xyMag = Math.hypot(xDiffRelField, yDiffRelField);

        // Step 2. Determine the target angle in field coordinate system using the X and Y values passed in.
        // The angle we have is the Target heading in the field coordinate system.
        // However the robot my already be at some angle already. May even be at the
        // target heading already..
        double xyHeadingRelField = Math.toDegrees(Math.atan2(yDiffRelField, xDiffRelField));

        // Step 3. Calculate the difference between the target angle and the robot's current heading in field coordinate system.
        double xyHeadingRelRobot = xyHeadingRelField - robotHeadingDegreesRelField;

        // Step 4. Determine how much of the power should be attributed to the Robot's X and Y directions.
        // To move forward on the field 2 ft, the robot may need to
        //    move 2ft forward if it is facing forward
        //    move -2ft forward if it is facing backward
        //    strafe +/- 2ft if it is turned 90 degrees from forward
        double xDiffRelRobot = xyMag * Math.cos(Math.toRadians(xyHeadingRelRobot));
        double yDiffRelRobot = xyMag * Math.sin(Math.toRadians(xyHeadingRelRobot));

        return new double[]{xDiffRelRobot, yDiffRelRobot};
    }

    /**
     * Drive the robot using powers given in the robot's own coordinate system.
     * <p>
     * Robot X is forward and robot Y is left, regardless of which way
     * the robot faces on the field. The three inputs are combined into a power for each of
     * the 4 drive motors. If any motor power falls outside -1 to 1, all four are scaled down
     * by the same amount so the robot still moves in the requested direction.
     *
     * @param xPowerRelRobot        power to apply in the robot relative X (forward) direction, -1 to 1.
     * @param yPowerRelRobot        power to apply in the robot relative Y (left) direction, -1 to 1.
     * @param counterClockwisePower power to apply to rotation, -1 to 1; positive turns counter-clockwise.
     */
    public void movePowerRelRobot(double xPowerRelRobot, double yPowerRelRobot, double counterClockwisePower) {
        // Step 1. Calculate the power needed for each motor
        double frontLeftPower = xPowerRelRobot - yPowerRelRobot - counterClockwisePower;
        double frontRightPower = xPowerRelRobot + yPowerRelRobot + counterClockwisePower;
        double backLeftPower = xPowerRelRobot + yPowerRelRobot - counterClockwisePower;
        double backRightPower = xPowerRelRobot - yPowerRelRobot + counterClockwisePower;

        // Step 2. Scale the power to remaining in [-1, 1].
        // If any power is outside -1 to 1, scale all four down by the same amount
        // so the robot still moves in the same direction.
        double max = Math.max(1.0, Math.max(
                Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower)),
                Math.max(Math.abs(backLeftPower), Math.abs(backRightPower))));

        // Step 3. Set power on all 4 motors.
        frontLeft.setPower(frontLeftPower / max);
        frontRight.setPower(frontRightPower / max);
        backLeft.setPower(backLeftPower / max);
        backRight.setPower(backRightPower / max);
    }
}