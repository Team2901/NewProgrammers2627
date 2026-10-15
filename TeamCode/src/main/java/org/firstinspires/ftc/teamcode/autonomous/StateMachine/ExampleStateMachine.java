package org.firstinspires.ftc.teamcode.autonomous.StateMachine;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.DecodeHardware;

import java.util.ArrayList;
import java.util.Arrays;

@TeleOp
public class ExampleStateMachine extends OpMode {
    public DecodeHardware robot = new DecodeHardware();
    boolean started = true;
    boolean firstGate = false;
    boolean secondGate = false;
    boolean thirdGate = false;
    boolean fourthGate = false;
    class FirstExample extends MyState{

        @Override
        void enter() {
            telemetry.addLine("I've entered");
            telemetry.addData("Current State: ", exampleMachine.getState());
        }
        @Override
        void leaving() {
            super.leaving();
            telemetry.update();
        }
        @Override
        boolean isEnded() {
            return firstGate;
        }
    }
    class SecondExample extends MyState{

        @Override
        void enter() {
            telemetry.addData("Orientation: ", robot.imu.getRobotYawPitchRollAngles());
            telemetry.addData("Current State: ", exampleMachine.getState());
        }

        @Override
        void leaving() {
            super.leaving();
            telemetry.update();
        }

        @Override
        boolean isEnded() {
            return secondGate;
        }
    }
    class ThirdExample extends MyState{

        @Override
        void enter() {
            telemetry.addLine("Hi - Bob");
            telemetry.addData("Current State: ", exampleMachine.getState());
        }
        @Override
        void leaving() {
            super.leaving();
            telemetry.update();
        }
        @Override
        boolean isEnded() {
            return thirdGate;
        }
    }
    class FourthExample extends MyState{

        @Override
        void enter() {
            telemetry.addLine("Bye and no thank you. Have a bad day.");
            telemetry.addData("Current State: ", exampleMachine.getState());
        }
        @Override
        void leaving() {
            super.leaving();
            telemetry.update();
        }
        @Override
        boolean isEnded() {
            return fourthGate;
        }
    }
    StateMachine exampleMachine = new StateMachine(new ArrayList<>(Arrays.asList(new FirstExample(), new SecondExample(), new ThirdExample(), new FourthExample(), new End())));
    @Override
    public void init() {
        robot.init(this.hardwareMap, telemetry);
    }

    @Override
    public void loop() {
        if(started){
            exampleMachine.start();
            started = false;
        }
        if(fourthGate){
            telemetry.addData("Current State: ", exampleMachine.getState());
            telemetry.update();
            fourthGate = false;
        }
        if(gamepad1.aWasPressed()){
            firstGate = true;
        }
        if(gamepad1.bWasPressed()){
            secondGate = true;
        }
        if(gamepad1.yWasPressed()){
            thirdGate = true;
        }
        if(gamepad1.xWasPressed()){
            fourthGate = true;
        }
        exampleMachine.update();
    }
}
