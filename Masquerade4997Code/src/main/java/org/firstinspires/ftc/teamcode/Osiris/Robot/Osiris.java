package org.firstinspires.ftc.teamcode.Osiris.Robot;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.Osiris.Autonomous.Vision.RingDetector;

import MasqueradeLibrary.MasqMath.MasqPIDController;
import MasqueradeLibrary.MasqMotion.*;
import MasqueradeLibrary.MasqOdometry.MasqPositionTracker;
import MasqueradeLibrary.MasqRobot;
import MasqueradeLibrary.MasqVision.MasqCamera;

import static MasqueradeLibrary.MasqResources.DashBoard.getDash;
import static MasqueradeLibrary.MasqResources.MasqUtils.*;
import static MasqueradeLibrary.MasqRobot.OpMode.AUTO;
import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.REVERSE;
import static org.openftc.easyopencv.OpenCvCameraRotation.SIDEWAYS_LEFT;

/**
 * Created by Keval Kataria on 9/12/2020
 */
public class Osiris extends MasqRobot {
    public MasqCamera camera;
    public MasqMotor intake, encoder1, encoder2, shooter;
    public RotatingClaw claw;
    public MasqServo flicker, hopper;
    private HardwareMap hardwareMap;

    @Override
    public void mapHardware() {
        driveTrain = new MasqDriveTrain(REVERSE);

        shooter = new MasqMotor("shooter");
        intake = new MasqMotor("intake", REVERSE);

        claw = new RotatingClaw();

        flicker = new MasqServo("flicker");
        hopper = new MasqServo("hopper");

        encoder1 = new MasqMotor("encoder1");
        encoder2 = new MasqMotor("encoder2", REVERSE);
        tracker = new MasqPositionTracker(intake, encoder1, encoder2, hardwareMap);

        dash = getDash();
    }

    @Override
    public void init(OpMode opmode) {
        hardwareMap = getHardwareMap();
        mapHardware();

        tracker.setXRadius(5.675);
        tracker.setTrackWidth(13.75);
        tracker.reset();
        setTracker(tracker);

        turnController = new MasqPIDController(0.04);

        driveTrain.setVelocityControl(true);
        driveTrain.resetEncoders();

        initServos();

        shooter.setVelocityControl(true);

        if(opmode == AUTO) initCamera();
    }

    public void initCamera() {
        RingDetector detector = new RingDetector();
        detector.setClippingMargins(662,324,208,786);
        camera = new MasqCamera(detector, hardwareMap);
        camera.start(SIDEWAYS_LEFT);
    }

    private void initServos() {
        claw.reset();
        flicker.scaleRange(0.045, 0.18);
        flicker.setPosition(0);
        hopper.scaleRange(0.05, 0.344);
        hopper.setPosition(0);
    }
    public int getRings() {return 0;} //Placeholder until I get distance sensor
}