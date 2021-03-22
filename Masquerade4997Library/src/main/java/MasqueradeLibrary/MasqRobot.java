package MasqueradeLibrary;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.*;

import MasqueradeLibrary.MasqMath.*;
import MasqueradeLibrary.MasqMotion.MasqDriveTrain;
import MasqueradeLibrary.MasqOdometry.*;
import MasqueradeLibrary.MasqOdometry.MasqWayPoint.PointMode;
import MasqueradeLibrary.MasqResources.DashBoard;

import static MasqueradeLibrary.MasqOdometry.MasqWayPoint.PointMode.*;
import static MasqueradeLibrary.MasqResources.MasqUtils.*;
import static com.qualcomm.robotcore.util.Range.clip;
import static java.lang.Math.*;
import static java.util.Arrays.asList;

/**
 * Created by Keval Kataria on 3/15/2021
 */

public abstract class MasqRobot {
    public abstract void mapHardware();
    public abstract void init(OpMode opmode);

    public MasqDriveTrain driveTrain;
    public MasqPositionTracker tracker;
    private ElapsedTime timeoutClock = new ElapsedTime();
    protected DashBoard dash;

    public enum OpMode {
        AUTO, TELEOP
    }

    public void turnAbsolute(double angle, double timeout) {
        double error, power;

        turnController.reset();
        timeoutClock.reset();
        do {
            error = adjustAngle(angle - tracker.getHeading());
            power = clip(turnController.getOutput(error), -1, 1);

            driveTrain.setPower(power, -power);
            tracker.updateSystem();

            dash.create("KP: ", turnController.getConstants()[0]);
            dash.create("Power: " , power);
            dash.create("TargetAngle: ", angle);
            dash.create("Heading: ", tracker.getHeading());
            dash.create("AngleLeftToCover: ", error);
            dash.update();
        } while (opModeIsActive() && (adjustAngle(abs(error)) > 1) && timeoutClock.seconds() < timeout);
        driveTrain.setPower(0);
    }
    public void turnAbsolute(double angle) {turnAbsolute(angle, DEFAULT_TIMEOUT);}

    public void turnRelative(double angle, double timeout) {turnAbsolute(tracker.getHeading() + angle, timeout);}
    public void turnRelative(double angle) {turnAbsolute(tracker.getHeading() + angle);}

    public void xyPath(double timeout, MasqWayPoint... points) {
        List<MasqWayPoint> pointsWithRobot = new ArrayList<>(asList(points));
        pointsWithRobot.add(0, getCurrentWayPoint());
        MasqPIDController speedController = new MasqPIDController();
        MasqPIDController angleController = new MasqPIDController();
        int index = 1;
        ElapsedTime pointTimeout = new ElapsedTime();
        timeoutClock.reset();
        while (timeoutClock.seconds() < timeout && index < pointsWithRobot.size()) {
            MasqWayPoint target = pointsWithRobot.get(index);
            MasqVector current = new MasqVector("Current", tracker.getGlobalX(), tracker.getGlobalY());
            MasqVector initial = new MasqVector("Initial", pointsWithRobot.get(index - 1).getX(), pointsWithRobot.get(index - 1).getY());
            angleController.setKp(target.getAngularCorrectionSpeed());
            speedController.setKp(target.getDriveCorrectionSpeed());

            double speed = 1;
            speedController.reset();
            angleController.reset();
            pointTimeout.reset();
            while (pointTimeout.seconds() < target.getTimeout()&&
                    !current.equal(target.getTargetRadius(), target.getPoint()) && opModeIsActive() && speed > 0.1 + target.getMinVelocity()) {
                double heading = toRadians(tracker.getHeading());
                MasqVector headingUnitVector = new MasqVector("Heading Unit Vector", sin(heading), cos(heading));
                MasqVector lookahead = getLookAhead(initial, current, target.getPoint(), target.getLookAhead());
                MasqVector pathDisplacement = initial.displacement(target.getPoint()).setName("Path Displacement");

                if (initial.displacement(lookahead).getMagnitude() > pathDisplacement.getMagnitude()) {
                    if (index == pointsWithRobot.size() - 1) lookahead = target.getPoint();
                    else break;
                }
                MasqVector lookaheadDisplacement = current.displacement(lookahead).setName("Look Ahead Displacement");
                speed = speedController.getOutput(current.displacement(target.getPoint()).getMagnitude());
                speed = scaleNumber(speed, target.getMinVelocity(), target.getMaxVelocity());
                double pathAngle = adjustAngle(headingUnitVector.angleTo(lookaheadDisplacement));

                PointMode mode = target.getSwitchMode();
                boolean mechMode = (current.equal(target.getModeSwitchRadius(), target.getPoint()) && mode == SWITCH) ||
                        mode == MECH;

                if (mechMode) {
                    double turnPower = angleController.getOutput(adjustAngle(target.getH() - tracker.getHeading()));
                    driveTrain.setPowerMECH(toRadians(pathAngle), speed, turnPower);
                }
                else {
                    double powerAdjustment = angleController.getOutput(pathAngle);
                    double leftPower = speed + powerAdjustment;
                    double rightPower = speed - powerAdjustment;

                    int direction = 1;
                    if(abs(pathAngle) > 100) direction = -1;

                    driveTrain.setPower(direction * leftPower, direction * rightPower);
                }

                tracker.updateSystem();

                dash.create(tracker);
                dash.create("Distance Left", target.getPoint().displacement(current).getMagnitude());
                dash.create("Path Angle: ", pathAngle);
                dash.create(lookaheadDisplacement);
                dash.update();

                current.setX(tracker.getGlobalX());
                current.setY(tracker.getGlobalY());
            }
            target.getOnComplete().run();
            index++;
        }
        driveTrain.setPower(0);
    }
    public void xyPath(MasqWayPoint... points) {
        double timeout = 0;
        for(MasqWayPoint point : points) timeout += point.getTimeout();
        xyPath(timeout, points);
    }

    public void NFS(Gamepad c) {
        double move = -c.left_stick_y;
        double turn = c.right_stick_x;
        double left = move + turn;
        double right = move - turn;

        double max = max(left, right);
        if(max > 1.0) {
            left /= max;
            right /= max;
        }

        driveTrain.setPower(left, right);
    }

    public void TANK(Gamepad c) {driveTrain.setPower(-c.left_stick_y, -c.right_stick_y);}

    public void MECH(Gamepad c) {
        double x = c.left_stick_x;
        double y = -c.left_stick_y;
        double xR = c.right_stick_x;
        double angle = atan2(x, y);

        driveTrain.setPowerMECH(angle, hypot(x, y), xR);

        dash.create("Angle: ", angle);
    }
    public void MECH() {MECH(getLinearOpMode().getDefaultController());}

    public MasqWayPoint getCurrentWayPoint() {
        return new MasqWayPoint().setPoint(tracker.getGlobalX(), tracker.getGlobalY(), tracker.getHeading()).setName("Initial WayPoint");
    }
}