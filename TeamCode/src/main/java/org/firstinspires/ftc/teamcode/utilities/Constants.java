package org.firstinspires.ftc.teamcode.utilities;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

/**
 * Definitions shared between OpModes, such as which alliance the robot is playing for and
 * where things are on the field.
 */
public class Constants {

    public enum Alliance {
        RED,
        BLUE
    }

    /// Where the robot starts autonomous: by the audience wall, or at the far end of the field.
    public enum StartingLocation {
        AUDIENCE,
        FAR
    }

    public static final Pose2D RED_AUDIENCE_GOAL_POSE = new Pose2D(DistanceUnit.INCH, -12, -12, AngleUnit.DEGREES, 90);
    public static final Pose2D RED_FAR_GOAL_POSE = new Pose2D(DistanceUnit.INCH, -12, 12, AngleUnit.DEGREES, -90);
    public static final Pose2D BLUE_AUDIENCE_GOAL_POSE = new Pose2D(DistanceUnit.INCH, 12, -12, AngleUnit.DEGREES, 90);
    public static final Pose2D BLUE_FAR_GOAL_POSE = new Pose2D(DistanceUnit.INCH, 12, 12, AngleUnit.DEGREES, -90);

    /**
     * The goal to aim at: our alliance's goal on whichever half of the field the robot is in.
     *
     * @param alliance           the alliance we are playing for.
     * @param robotPoseRelField  the robot's current pose in field coordinates.
     * @return the far goal if the robot is in the far half (Y above 0), otherwise the audience goal.
     */
    public static Pose2D getGoalPose(Alliance alliance, Pose2D robotPoseRelField) {
        boolean inFarHalf = robotPoseRelField.getY(DistanceUnit.INCH) > 0;
        if (alliance == Alliance.RED) {
            return inFarHalf ? RED_FAR_GOAL_POSE : RED_AUDIENCE_GOAL_POSE;
        } else {
            return inFarHalf ? BLUE_FAR_GOAL_POSE : BLUE_AUDIENCE_GOAL_POSE;
        }
    }

    // Auto starting poses. The other three are mirrors of red audience, so only it needs tuning.
    public static final Pose2D RED_AUDIENCE_STARTING_POSE = new Pose2D(DistanceUnit.INCH, -12, -72 + 9, AngleUnit.DEGREES, 90);

    public static final Pose2D RED_FAR_STARTING_POSE = new Pose2D(DistanceUnit.INCH,
            RED_AUDIENCE_STARTING_POSE.getX(DistanceUnit.INCH),
            -RED_AUDIENCE_STARTING_POSE.getY(DistanceUnit.INCH), AngleUnit.DEGREES,
            -RED_AUDIENCE_STARTING_POSE.getHeading(AngleUnit.DEGREES));

    public static final Pose2D BLUE_AUDIENCE_STARTING_POSE = new Pose2D(DistanceUnit.INCH,
            -RED_AUDIENCE_STARTING_POSE.getX(DistanceUnit.INCH),
            RED_AUDIENCE_STARTING_POSE.getY(DistanceUnit.INCH), AngleUnit.DEGREES,
            RED_AUDIENCE_STARTING_POSE.getHeading(AngleUnit.DEGREES));

    public static final Pose2D BLUE_FAR_STARTING_POSE = new Pose2D(DistanceUnit.INCH,
            BLUE_AUDIENCE_STARTING_POSE.getX(DistanceUnit.INCH),
            -BLUE_AUDIENCE_STARTING_POSE.getY(DistanceUnit.INCH), AngleUnit.DEGREES,
            -BLUE_AUDIENCE_STARTING_POSE.getHeading(AngleUnit.DEGREES));

    /**
     * The pose the robot starts autonomous at.
     *
     * @param alliance          the alliance we are playing for.
     * @param startingLocation  which end of the field the robot starts at.
     * @return the matching starting pose, in field coordinates.
     */
    public static Pose2D getStartingPose(Alliance alliance, StartingLocation startingLocation) {
        if (alliance == Alliance.RED) {
            return startingLocation == StartingLocation.FAR ? RED_FAR_STARTING_POSE : RED_AUDIENCE_STARTING_POSE;
        } else {
            return startingLocation == StartingLocation.FAR ? BLUE_FAR_STARTING_POSE : BLUE_AUDIENCE_STARTING_POSE;
        }
    }
}
