package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.utilities.Constants;
import org.firstinspires.ftc.teamcode.utilities.PoseUtilities;

public class BioBuzzV2Robot {
    public Telemetry telemetry;

    private Constants.Alliance alliance = null;

    private IMU imu;
    private static final RevHubOrientationOnRobot.UsbFacingDirection imuUsbFacingDirection = RevHubOrientationOnRobot.UsbFacingDirection.UP;
    private static final RevHubOrientationOnRobot.LogoFacingDirection imuLogoDirection = RevHubOrientationOnRobot.LogoFacingDirection.RIGHT;

    private GoBildaPinpointDriver pinpoint;
    private static final double pinpointXOffsetMM = 0;
    private static final double pinpointYOffsetMM = 0;

    public MechanumDriveBase driveBase = new MechanumDriveBase();
    public Turret turret = new Turret();

    /**
     * Set up the robot: read the alliance, then initialize the IMU, Pinpoint, drive base and turret.
     * Call once from the OpMode's init.
     *
     * @param hardwareMap the OpMode's hardware map, used to find each device by its configured name.
     * @param telemetry   the OpMode's telemetry, kept for reporting status.
     */
    public void init(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;

        initAlliance();
        initImu(hardwareMap);
        initPinpoint(hardwareMap);
        driveBase.init(hardwareMap, telemetry);
        turret.init(hardwareMap, telemetry);
    }

    /**
     * Refresh the Pinpoint's position reading and update the drive base. Call once every loop,
     * before reading the robot's position.
     */
    public void update() {
        pinpoint.update();
        driveBase.update();
    }

    // Alliance

    /**
     * Read the alliance the last auto recorded on the blackboard, defaulting to RED if none has.
     */
    public void initAlliance() {
        // The auto records the alliance it ran as; default to RED if no auto has picked one.
        alliance = (Constants.Alliance) OpMode.blackboard.get("alliance");
        if (alliance == null) {
            telemetry.addData("alliance", "not found, defaulting to RED");
            alliance = Constants.Alliance.RED;
        } else {
            telemetry.addData("alliance", alliance);
        }
    }

    /**
     * Get the alliance the robot is playing as.
     *
     * @return the alliance read by {@link #initAlliance} or last set with {@link #setAlliance}.
     */
    public Constants.Alliance getAlliance() {
        return alliance;
    }

    /**
     * Set the alliance the robot is playing as, and record it on the blackboard so the next
     * OpMode (e.g. teleop after auto) picks it up.
     *
     * @param alliance the alliance to play as.
     */
    public void setAlliance(Constants.Alliance alliance) {
        this.alliance = alliance;
        OpMode.blackboard.put("alliance", alliance);
    }

    // IMU

    /**
     * Find the IMU named "imu" and initialize it for how the Control Hub is mounted on the robot.
     *
     * @param hardwareMap the OpMode's hardware map.
     */
    private void initImu(HardwareMap hardwareMap) {
        imu = hardwareMap.get(IMU.class, "imu");

        IMU.Parameters parameters;
        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(imuLogoDirection, imuUsbFacingDirection);
        parameters = new IMU.Parameters(orientationOnRobot);
        boolean success = imu.initialize(parameters);

        if (!success && telemetry != null) {
            telemetry.addData("IMU initialized", false);
        }
    }

    /**
     * Reset the IMU heading so the way the robot faces now reads as 0.
     */
    public void resetImuHeading() {
        imu.resetYaw();
    }

    /**
     * Read the robot's heading from the IMU.
     *
     * @return the robot's heading in degrees, -180 to 180; 0 is the way the robot faced when
     * the IMU was last reset, and positive is counter-clockwise.
     */
    public double getImuHeading() {
        return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
    }

    // Pinpoint

    /**
     * Find the Pinpoint odometry computer named "pinpoint" and configure its pod offsets, pod type
     * and encoder directions. The position is left as it was, so a starting pose set by auto carries over.
     *
     * @param hardwareMap the OpMode's hardware map.
     */
    private void initPinpoint(HardwareMap hardwareMap) {

        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");

        /*
         *  Set the odometry pod positions relative to the point that you want the position to be measured from.
         *
         *  The X pod offset refers to how far sideways from the tracking point the X (forward) odometry pod is.
         *  Left of the center is a positive number, right of center is a negative number.
         *
         *  The Y pod offset refers to how far forwards from the tracking point the Y (strafe) odometry pod is.
         *  Forward of center is a positive number, backwards is a negative number.
         */
        pinpoint.setOffsets(pinpointXOffsetMM, pinpointYOffsetMM, DistanceUnit.MM); //these are tuned for 3110-0002-0001 Product Insight #1

        /*
         * Set the kind of pods used by your robot. If you're using goBILDA odometry pods, select either
         * the goBILDA_SWINGARM_POD, or the goBILDA_4_BAR_POD.
         * If you're using another kind of odometry pod, uncomment setEncoderResolution and input the
         * number of ticks per unit of your odometry pod.  For example:
         *     pinpoint.setEncoderResolution(13.26291192, DistanceUnit.MM);
         */
        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);

        /*
         * Set the direction that each of the two odometry pods count. The X (forward) pod should
         * increase when you move the robot forward. And the Y (strafe) pod should increase when
         * you move the robot to the left.
         */
        pinpoint.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD,
                GoBildaPinpointDriver.EncoderDirection.FORWARD);

        telemetry.addData("pinpoint position", PoseUtilities.poseToString(pinpoint.getPosition()));
    }

    /**
     * Get the robot's position on the field from the Pinpoint, as of the last {@link #update}.
     *
     * @return the robot's pose in field coordinates.
     */
    public Pose2D getPinpointPositon() {
        return pinpoint.getPosition();
    }

    /**
     * Tell the Pinpoint where the robot is on the field, e.g. its starting pose.
     *
     * @param pose the robot's pose in field coordinates.
     */
    public void setPinpointPositon(Pose2D pose) {
        pinpoint.setPosition(pose);
    }

}