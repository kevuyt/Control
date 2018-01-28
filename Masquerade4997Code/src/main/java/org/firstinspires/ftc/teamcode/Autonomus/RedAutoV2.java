package org.firstinspires.ftc.teamcode.Autonomus;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import Library4997.MasqExternal.Direction;
import Library4997.MasqExternal.MasqExternal;
import Library4997.MasqWrappers.MasqLinearOpMode;

/**
 * Created by Archish on 1/28/18.
 */
@Autonomous(name = "RedAutoV2", group = "Autonomus")
public class RedAutoV2 extends MasqLinearOpMode implements Constants {
    public void runLinearOpMode() throws InterruptedException {
        robot.mapHardware(hardwareMap);
        robot.vuforia.initVuforia(hardwareMap);
        robot.initializeAutonomous();
        robot.initializeServos();
        robot.flipper.setPosition(0.7);
        while (!opModeIsActive()) {
            dash.create(robot.imu);
            dash.update();
        }
        waitForStart();
        robot.sleep(robot.getDelay());
        robot.vuforia.activateVuMark();
        String vuMark = readVuMark();
        runJewel();
        robot.blueRotator.setPosition(ROTATOR_BLUE_OUT);
        robot.sleep(500);
        robot.jewelArmBlue.setPosition(JEWEL_BLUE_OUT);
        robot.stop(robot.jewelColorBlue);
        robot.driveTrain.setClosedLoop(false);
        runVuMark(vuMark);
    }

    public void runJewel() {
        robot.jewelArmRed.setPosition(JEWEL_RED_OUT);
        robot.sleep(1500);
        if (robot.jewelColorRed.isRed()) robot.redRotator.setPosition(ROTATOR_RED_SEEN);
        else robot.redRotator.setPosition(ROTATOR_RED_NOT_SEEN);
        robot.sleep(1500);
        robot.jewelArmRed.setPosition(JEWEL_RED_IN);
        robot.sleep(1500);
    }

    public String readVuMark() {
        robot.waitForVuMark();
        return robot.vuforia.getVuMark();
    }

    public void runVuMark(String vuMark) {
        double startAngle = robot.imu.getHeading();
        robot.drive(25, POWER_OPTIMAL, Direction.BACKWARD);
        robot.drive(23, POWER_LOW, Direction.FORWARD);
        if (MasqExternal.VuMark.isCenter(vuMark)) robot.drive(DISTANCE_TO_CENTER_BOX, POWER_OPTIMAL, Direction.BACKWARD);
        else if (MasqExternal.VuMark.isLeft(vuMark)) robot.drive(DISTANCE_TO_LEFT_BOX_RED, POWER_OPTIMAL, Direction.BACKWARD);
        else if (MasqExternal.VuMark.isRight(vuMark)) robot.drive(DISTANCE_TO_RIGHT_BOX_RED, POWER_OPTIMAL, Direction.BACKWARD);
        else if (MasqExternal.VuMark.isUnKnown(vuMark)) robot.drive(DISTANCE_TO_CENTER_BOX, POWER_OPTIMAL, Direction.BACKWARD);
        double endAngle = robot.imu.getHeading();
        robot.turn(90 + (endAngle - startAngle), Direction.RIGHT);
        robot.drive(6, POWER_OPTIMAL, Direction.BACKWARD);
        robot.flipper.setPosition(0.3);
        robot.sleep(1000);
        robot.drive(10, POWER_LOW, Direction.FORWARD);
        robot.drive(10, POWER_OPTIMAL, Direction.BACKWARD);
        robot.drive(3, POWER_OPTIMAL, Direction.FORWARD);
        robot.flipper.setPosition(1);
        /*
        robot.drive(70, POWER_HIGH, Direction.FORWARD);
        robot.flipper.setPosition(0.7);
        robot.intake.setPower(0);
        robot.drive(70, POWER_HIGH, Direction.BACKWARD);
        robot.flipper.setPosition(0);
        robot.sleep(500);
        robot.drive(6, POWER_OPTIMAL, Direction.BACKWARD);
        robot.drive(6, POWER_OPTIMAL, Direction.FORWARD);*/
    }
}