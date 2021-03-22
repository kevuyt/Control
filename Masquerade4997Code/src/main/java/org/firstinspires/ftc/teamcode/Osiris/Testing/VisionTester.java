package org.firstinspires.ftc.teamcode.Osiris.Testing;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Osiris.Autonomous.Vision.RingDetector;
import org.firstinspires.ftc.teamcode.Osiris.Autonomous.Vision.RingDetector.TargetZone;
import org.firstinspires.ftc.teamcode.Osiris.Robot.Osiris;
import org.opencv.core.Rect;

import MasqueradeLibrary.MasqMath.MasqVector;
import MasqueradeLibrary.MasqResources.MasqLinearOpMode;

import static MasqueradeLibrary.MasqResources.MasqUtils.getCenterPoint;
import static MasqueradeLibrary.MasqRobot.OpMode.AUTO;

/**
 * Created by Keval Kataria on 3/7/2021
 */

@TeleOp(name = "VisionTester", group = "Test")
public class VisionTester extends MasqLinearOpMode {
    private Osiris robot = new Osiris();
    RingDetector detector;
    double top = 662, left = 324, bottom = 208, right = 786;

    @Override
    public void runLinearOpMode() {
        robot.init(AUTO);
        detector = (RingDetector) robot.camera.detector;

        while(!opModeIsActive()) {
            TargetZone zone = detector.findZone();

            top -= 0.01 * gamepad1.left_stick_y;
            bottom += 0.01 * gamepad1.left_stick_y;
            right -= 0.01 * gamepad1.left_stick_x;
            left += 0.01 * gamepad1.left_stick_x;

            detector.setClippingMargins((int) top, (int) left, (int) bottom, (int) right);

            dash.create("Zone:", zone);
            dash.create("Control:", detector.getControl());
            dash.create("Top:", detector.getTop());
            dash.create("Bottom:", detector.getBottom());
            dash.create("");
            dash.create(top, left, bottom, right);
            dash.update();

            if(isStopRequested()) {
                robot.camera.stop();
                break;
            }
        }

        waitForStart();

        detector.switchDetection();

        while(opModeIsActive()) {
            Rect rect = detector.getFoundRect();
            MasqVector[] rings = detector.findRings();

            dash.create("Center Point X:", getCenterPoint(rect).x);
            dash.create("Height:", rect.height);

            if(rings[0] != null) {
                dash.create("Ring 1 X:", rings[0].getX());
                dash.create("Ring 1 Y:", rings[0].getY());
            }
            if(rings[1] != null) {
                dash.create("Ring 2 X:", rings[1].getX());
                dash.create("Ring 2 Y:", rings[1].getY());
            }
            dash.update();
        }

        robot.camera.stop();
    }
}