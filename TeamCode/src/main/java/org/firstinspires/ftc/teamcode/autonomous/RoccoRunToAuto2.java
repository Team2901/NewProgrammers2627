package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

@Autonomous
public class RoccoRunToAuto2 extends OpMode {
    DcMotor leftFrontMotor;
    DcMotor rightFrontMotor;
    DcMotor leftBackMotor;
    DcMotor rightBackMotor;

    double targetDistanceInches = 4 * 12; // 4 feet
    double wheelRadiusInches = 1.88976378;
    double wheelCircumferenceInches = 2 * Math.PI * wheelRadiusInches;
    double targetWheelRotations = targetDistanceInches / wheelCircumferenceInches;
    double motorToWheelGearRatio = 1.0/1.0; // # of wheel gear teeth / # of motor gear teeth
    double targetMotorRotations = targetWheelRotations * motorToWheelGearRatio;
    double motorEncoderTicksPerRotation = ((((1+(46/17.0))) * (1+(46/11.0))) * 28); // 537.7
    // from https://www.gobilda.com/5203-series-yellow-jacket-planetary-gear-motor-19-2-1-ratio-24mm-length-8mm-rex-shaft-312-rpm-3-3-5v-encoder/
    int targetMotorEncoderTicks = (int) (targetMotorRotations * motorEncoderTicksPerRotation);
    GoBildaPinpointDriver pinpoint;


    @Override
    public void init() {

        leftFrontMotor = hardwareMap.get(DcMotor.class, "frontLeft");
        rightFrontMotor = hardwareMap.get(DcMotor.class, "frontRight");
        leftBackMotor = hardwareMap.get(DcMotor.class, "backLeft");
        rightBackMotor = hardwareMap.get(DcMotor.class, "backRight");

        leftFrontMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFrontMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        leftBackMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        rightBackMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        leftFrontMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFrontMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftBackMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightBackMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
        configurePinpoint();

        if(gamepad1.a) {
            //this is the middle of the field (not a legal starting position)
            pinpoint.setPosition(new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0));
        }
    }

    private void configurePinpoint() {
        /*these are tuned for 3110-0002-0001 Product Insight #1*/
        pinpoint.setOffsets(-84.0, -168.0, DistanceUnit.MM);

        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        pinpoint.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD,
                GoBildaPinpointDriver.EncoderDirection.FORWARD);
    }

    @Override
    public void loop() {

        leftFrontMotor.setTargetPosition(targetMotorEncoderTicks);
        rightFrontMotor.setTargetPosition(targetMotorEncoderTicks);
        leftBackMotor.setTargetPosition(targetMotorEncoderTicks);
        rightBackMotor.setTargetPosition(targetMotorEncoderTicks);

        leftFrontMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightFrontMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftBackMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightBackMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        leftFrontMotor.setPower(1);
        rightFrontMotor.setPower(1);
        leftBackMotor.setPower(1);
        rightBackMotor.setPower(1);

        pinpoint.update();
        Pose2D pose2D = pinpoint.getPosition();

        telemetry.addData("X coordinate (IN)", pose2D.getX(DistanceUnit.INCH));
        telemetry.addData("Y coordinate (IN)", pose2D.getY(DistanceUnit.INCH));
        telemetry.addData("Heading angle (DEGREES)", pose2D.getHeading(AngleUnit.DEGREES));

        telemetry.addData("power", leftFrontMotor.getPower());
        telemetry.addData("mode", leftFrontMotor.getMode());
        telemetry.addData("target", leftFrontMotor.getTargetPosition());
        telemetry.addData("current", leftFrontMotor.getCurrentPosition());
        telemetry.addData("busy", leftFrontMotor.isBusy());


        telemetry.addData("wheelCircumferenceInches",wheelCircumferenceInches);
        telemetry.addData("targetWheelRotations",targetWheelRotations);
        telemetry.addData("targetMotorRotations",targetMotorRotations);
        telemetry.addData("targetMotorEncoderTicks",targetMotorEncoderTicks);
    }
}
