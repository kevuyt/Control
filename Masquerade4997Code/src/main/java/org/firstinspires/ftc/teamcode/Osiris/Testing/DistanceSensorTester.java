package org.firstinspires.ftc.teamcode.Osiris.Testing;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Osiris.Robot.Osiris;

import MasqueradeLibrary.MasqResources.MasqLinearOpMode;

import static MasqueradeLibrary.MasqRobot.OpMode.TELEOP;
import static org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit.MM;

/**
 * Created by Keval Kataria on 3/22/2021
 */

@TeleOp
public class DistanceSensorTester extends MasqLinearOpMode {
    private Osiris robot = new Osiris();

    @Override
    public void runLinearOpMode() {
        robot.init(TELEOP);

        while (!isStopRequested()) {
            dash.create("Distance Reading: ", robot.distanceSensor.getDistance(MM));
            dash.update();
        }
    }
}