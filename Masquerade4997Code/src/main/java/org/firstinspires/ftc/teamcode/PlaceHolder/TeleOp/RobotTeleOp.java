package org.firstinspires.ftc.teamcode.PlaceHolder.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.PlaceHolder.Robot.PlaceHolder;

import Library4997.MasqWrappers.MasqLinearOpMode;

/**
 * Created by Keval Kataria on 11/9/2020
 */
@TeleOp(name = "RobotTeleOp", group = "PlaceHolder")
public class RobotTeleOp extends MasqLinearOpMode {
    private PlaceHolder robot = new PlaceHolder();

    @Override
    public void runLinearOpMode() throws InterruptedException {
        robot.init(hardwareMap);

        while(!opModeIsActive()) {
            dash.create("Initialized");
            dash.update();
        }

        waitForStart();

        while(opModeIsActive()) {
            robot.MECH(controller1);
            robot.intake.setVelocity(controller1.rightTrigger()-controller1.leftTrigger());
            if(controller1.y()) robot.shooter.setVelocity(-1);
            else robot.shooter.setVelocity(0);
        }
    }
}