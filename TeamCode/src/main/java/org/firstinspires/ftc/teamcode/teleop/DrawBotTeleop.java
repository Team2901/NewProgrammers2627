package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.DrawBot;

/**
 * Teleop for the DrawBot.
 * <p>
 * Point the left stick the way you want the robot to go, and the pod turns to point that way.
 * Squeeze the right trigger to drive; the harder you squeeze, the faster it goes.
 * <p>
 * Point the right stick the way you want the robot to face, and it turns in place until it faces
 * that way; the further you push the stick, the faster it turns. The left stick wins if both
 * are pushed.
 * <p>
 * The pen starts up. Press dpad down to put it down and draw, and dpad up to lift it.
 * <pre>
 *     left stick:     direction to drive
 *     right trigger:  speed to drive
 *     right stick:    direction to face, and how fast to turn there
 *     dpad down:      put the pen down
 *     dpad up:        lift the pen
 * </pre>
 */
@TeleOp(group = "DrawBot")
public class DrawBotTeleop extends OpMode {

    // Ignore a stick when it is pushed less than this, so a resting stick doesn't steer or turn.
    static final double STICK_DEADZONE = 0.1;

    DrawBot robot = new DrawBot();

    @Override
    public void init() {
        robot.init(hardwareMap, telemetry);
        addControlsTelemetry();
    }

    @Override
    public void loop() {

        // Step 1. Read both sticks: Y is up and X is left, so the angle is 0 straight up and
        // counter-clockwise positive, the same as the steering angle and the heading.
        double leftJoyY = -gamepad1.left_stick_y;
        double leftJoyX = -gamepad1.left_stick_x;
        double leftJoyMag = Math.hypot(leftJoyY, leftJoyX);
        double leftJoyAngle = Math.toDegrees(Math.atan2(leftJoyX, leftJoyY));

        double rightJoyY = -gamepad1.right_stick_y;
        double rightJoyX = -gamepad1.right_stick_x;
        double rightJoyMag = Math.hypot(rightJoyY, rightJoyX);
        double rightJoyAngle = Math.toDegrees(Math.atan2(rightJoyX, rightJoyY));

        // Step 2. A stick only counts when it is pushed past the deadzone; otherwise its target is null.
        Double targetSteeringAngle = (leftJoyMag > STICK_DEADZONE) ? leftJoyAngle : null;
        Double targetTurnAngle = (rightJoyMag > STICK_DEADZONE) ? rightJoyAngle : null;

        // Step 3. Drive, turn, or stop.
        if (targetSteeringAngle != null) {
            robot.steerTowards(targetSteeringAngle, gamepad1.right_trigger);
        } else if (targetTurnAngle != null) {
            // Turn the robot to face the way the right stick points.
            // The further the stick is pushed, the faster the robot turns.
            robot.turnTowards(targetTurnAngle, Math.min(1.0, rightJoyMag));
        } else {
            // Neither stick pushed: hold the pod where it is and stop driving.
            robot.steerTowards(robot.getSteeringAngleDegrees(), 0);
        }

        // Step 4. Raise or lower the pen.
        if (gamepad1.dpad_up) {
            robot.penUp();
        } else if (gamepad1.dpad_down) {
            robot.penDown();
        }

        telemetry.addData("target steering angle", "%.1f", targetSteeringAngle);
        telemetry.addData("current steering angle", "%.1f", robot.getSteeringAngleDegrees());

        telemetry.addData("target heading angle", "%.1f", targetTurnAngle);
        telemetry.addData("current heading angle", "%.1f", robot.getHeadingAngleDegrees());

        telemetry.addData("pot voltage", "%.3f", robot.pot.getVoltage());
        telemetry.addData("turn power", "%.2f", robot.turnMotor.getPower());
        telemetry.addData("drive power", "%.2f %.2f", robot.greenDriveMotor.getPower(), robot.goldDriveMotor.getPower());
        telemetry.addData("pen", robot.penServo.getPosition() == DrawBot.PEN_DOWN_POSITION ? "down" : "up");

        addControlsTelemetry();
    }

    /**
     * Show the driver what each control does. Telemetry is cleared every loop, so call this each time.
     */
    private void addControlsTelemetry() {
        telemetry.addLine("Controls");
        telemetry.addData("  left stick", "direction to drive");
        telemetry.addData("  right trigger", "speed to drive");
        telemetry.addData("  right stick", "direction to face, push further to turn faster");
        telemetry.addData("  dpad up/down", "lift/lower the pen ");
        telemetry.addLine();
    }
}
