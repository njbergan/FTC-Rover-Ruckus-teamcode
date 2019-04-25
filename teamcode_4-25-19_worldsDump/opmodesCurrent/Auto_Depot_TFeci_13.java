package org.firstinspires.ftc.teamcode.opmodesCurrent;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaRoverRuckus;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TfodRoverRuckus;

import java.util.List;

//@Autonomous(name = "Auto Depot 13")

public class Auto_Depot_TFeci_13 extends LinearOpMode {

    public static final String TAG = "Vuforia VuMark Sample";
    private static final double
            CPR_20 = 560, CPI_DRIVE_TRAIN = CPR_20 / (4 * Math.PI), CPI_LIFT = CPR_20 * 2.25,
            CPR_60 = 1680, CPR_SHOULDER = CPR_60 * 9, CPI_RAIL = CPR_60;
    private int timeOutCount = 0, goldSample; //1: left, 2: center, 3: right, -1: unknown

    //encoder vars      formula:
    //(COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.1415)in terms of Math.PI
    //hardware vars
    private DcMotor motorLeft, motorLeft2,
            motorRight, motorRight2, motorLift,
            motorShoulder, motorRail, motorFlapper;


    //it takes 3 shaft rotations to move the actuator 1 inch this case uses a 1:4/3 gear dilation
    //gear ratio * cycles per rotation (28)


    //int relativeLayoutId;
    //View relativeLayout;
    //private CRServo servoCrFlapper;
    private double gameTimeSnapShot = 0;

    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    //private static final String LABEL_SILVER_MINERAL = "Silver Mineral";

    private VuforiaRoverRuckus vuforiaRoverRuckus;
    private TfodRoverRuckus tfodRoverRuckus;

    @Override
    public void runOpMode() {
        telemetry.addLine("Wait for Camera to Init");
        telemetry.update();

        motorLeft = hardwareMap.dcMotor.get("motor_1");
        motorRight = hardwareMap.dcMotor.get("motor_2");
        motorLeft2 = hardwareMap.dcMotor.get("motor_3");
        motorRight2 = hardwareMap.dcMotor.get("motor_4");

        motorLift = hardwareMap.dcMotor.get("motor_5");
        motorShoulder = hardwareMap.dcMotor.get("motor_6");
        motorRail = hardwareMap.dcMotor.get("motor_7");
        motorFlapper = hardwareMap.dcMotor.get("motor_8");


        //servoCrFlapper = hardwareMap.crservo.get("servo_0");//negative to spit out marker


        //so you don't have to wire red to black, to maintain program logic
        motorLeft.setDirection(DcMotor.Direction.REVERSE);
        motorLeft2.setDirection(DcMotor.Direction.REVERSE);
        motorRight.setDirection(DcMotor.Direction.FORWARD);
        motorRight2.setDirection(DcMotor.Direction.FORWARD);
        motorLift.setDirection(DcMotor.Direction.FORWARD);


        //List recognitions;
        double goldMineralX = -1, silverMineral1X = -1, silverMineral2X = -1;

        vuforiaRoverRuckus = new VuforiaRoverRuckus();
        tfodRoverRuckus = new TfodRoverRuckus();
        //boolean markerIsGold;

        // Put initialization blocks here.
        vuforiaRoverRuckus.initialize("", hardwareMap.get(WebcamName.class, "Webcam1"), "teamwebcamcalibrationLive.xml",
                false, false, VuforiaLocalizer.Parameters.CameraMonitorFeedback.AXES,
                0, 0, 0, 0, 0, 0, true);
        tfodRoverRuckus.initialize(vuforiaRoverRuckus, 0.4f, true, true);

        //while (!opModeIsActive() && !isStopRequested()) {
        telemetry.addData(">", "Press Play to start");
        //    telemetry.addData("Time: ", (int) this.time);
        telemetry.update();
        //}
        //waitForStart();
        telemetry.addData("Current Time", this.time);
        //telemetry.addData("Testing", debugSteps == 1);
        if (!opModeIsActive() && !isStopRequested()) {
            //telemetry.addData("Testing", debugSteps == 2);
            tfodRoverRuckus.activate();//start a camera related process
            // Put run blocks here.
            gameTimeSnapShot = this.time;
            //while (!opModeIsActive() && this.time <= (5 + gameTimeSnapShot)) {//1 second,0.5, 1.5
            while (!opModeIsActive() && !isStopRequested()) {
                //telemetry.addData("Testing", debugSteps == 2);
                if (tfodRoverRuckus != null) {
                    // getUpdatedRecognitions() will return null if no new information is available since
                    // the last time that call was made.
                    List<Recognition> updatedRecognitions = tfodRoverRuckus.getUpdatedRecognitions();
                    if (updatedRecognitions != null) {
                        telemetry.addData("# Object Detected", updatedRecognitions.size());

                        if (updatedRecognitions.size() == 2) {  //when webcam see's 2 minerals:

                            goldMineralX = -1;
                            silverMineral1X = -1;
                            silverMineral2X = -1;
                            telemetry.update();

                            for (Recognition recognition : updatedRecognitions) {   //if it detects gold, set it's X, if it detects silver for the first time, assign it to OneX
                                if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                                    goldMineralX = recognition.getLeft();
                                } else if (silverMineral1X == -1) {
                                    silverMineral1X = recognition.getLeft();
                                } else if (goldMineralX == -1) {
                                    silverMineral2X = recognition.getLeft();

                                }
                            }
                            if ((goldMineralX != -1 && silverMineral1X != -1) || (silverMineral1X != -1 && silverMineral2X != -1)) {   //if gold and silver, or silver and silver, are found
                                if (goldMineralX < silverMineral1X && goldMineralX != -1) {
                                    telemetry.addData("Gold Mineral Position", "Center G:" + goldMineralX + " S1:" + silverMineral1X + " S2:" + silverMineral2X);
                                    goldSample = 2;
                                } else if (goldMineralX > silverMineral1X) {
                                    telemetry.addData("Gold Mineral Position", "Left G:" + goldMineralX + " S1:" + silverMineral1X + " S2:" + silverMineral2X);
                                    goldSample = 1;
                                } else {
                                    telemetry.addData("Gold Mineral Position", "Right G:" + goldMineralX + " S1:" + silverMineral1X + " S2:" + silverMineral2X);
                                    goldSample = 3;
                                }
                            }
                            sleep(100);
                        }
                        telemetry.update();
                    }
                }
            }
            waitForStart();
            tfodRoverRuckus.deactivate();
            vuforiaRoverRuckus.close();
            tfodRoverRuckus.close();
        }






                               //init

        gameTimeSnapShot = this.time;


        //logical robot movements based on encoders

        if (opModeIsActive() && this.time <= (27.5 + gameTimeSnapShot) && !isStopRequested()) {                  //limits code to 30 seconds

            //autonomous encoder-based code starts after this line

            motorLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motorLeft2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motorRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motorRight2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motorLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

            motorSetModes(DcMotor.RunMode.STOP_AND_RESET_ENCODER);  //stop and reset encoders
            motorSetModes(DcMotor.RunMode.RUN_USING_ENCODER);       //start encoders

            // Send telemetry message to signify robot waiting;
            telemetry.addData("Status", "Resetting Encoders");
            telemetry.update();

            // Send telemetry message to indicate successful Encoder reset
            telemetry.addData("Path0", "Starting at %7d :%7d",
                    motorLeft.getCurrentPosition(),
                    motorRight.getCurrentPosition());
            telemetry.update();

            // Step through each leg of the path,
            // Note: Reverse movement is obtained by setting a negative distance (not speed)
            //speed, leftInches, rightInches, motorLiftInches


            //servoCrFlapper.setPower(-1); should deflap the marker
            encoderDrive(5, 0,0,1, 0, 0,
                    7, 0, 0, false);//was lift 7, 7.5. Time was 7.3
            //drop
            //encoderDrive(2,1,1,1,0,false);
            //drive away from hook
            sleep(100);
            encoderDrive(2.2, 0.35,0.35,0, 4, 4,
                    0, 0, 0, true);// was strafe 3.125,6
            //strafe off hook


            encoderDrive(2, 0, 0, 1, 0, 0, -4, 0, 0, false);
            //contract actuator to halfway


            sleep(100);
            encoderDrive(0.5, 0.4,0.4,0, -3, -3,
                    0, 0, 0, false);
            //backup to straighten
            sleep(100);





            if (goldSample == 3) {  //1: left, 2: center, 3: right, -1: unknown
                encoderDrive(0.9, 0.45,0.2,0, 180, 60, 0, 0, 0, false);//0.9
                //swerve to goldMineral         LS: 0.4     RS 0.2
                encoderDrive(1.5, 0.07,0.65,0, 60, 180,0, 0, 0, false);//0.95seconds,1.5,1.7
                //arc past the depot            LS: 0.05    RS 0.6
                encoderDrive(3,0.3,0.3,0,33.5,-33.5,0,0,0,false);//14,35
                //~180 degree tank turn
                //encoderDrive(1.5, 0.4, 0.4, 0, 6, 6, 0, 0, 0, false);
                //drive into the depot by 6"
                motorFlapper.setPower(-0.5);
                sleep(2000);
                //servoCrFlapper.setPower(0);//wait 1.5 sec, then turn it off
                //encoderDrive(7, 0.30,0.32,1, -68, -68, -4.0, 0, 0, false);
                encoderDrive(7, 0.30,0.32,1, -68, -68, 0, 0, 0, false);
                //back into the crater              LS-RS:0.25
            } else if (goldSample == 2) {
                encoderDrive(6.3, 0.35,0.35,0, 49, 49, 0,0,0, false);//was 50L,40R
                //drive straight into depot
                motorFlapper.setPower(-0.5);
                sleep(2000);
                //servoCrFlapper.setPower(0);
                encoderDrive(3,0.35,0.35,0,-32,-32,-0.75,0,0,false);//26
                //backup past samples towards lander
                encoderDrive(2,0.4,0.4,0,15,-15,-0.25,0,0,false);//14
                //turn to backface opponent crater
                //encoderDrive(8,0.3,0.3,1,-53,-53,-3.0,0,0,false);//53,51
                encoderDrive(8,0.3,0.3,1,-55,-53,0,0,0,false);//53,51
                //left speed taken from -53 to -55
                //drive to opponent crater

            } else {
            //} else if (goldSample == 1) {
                encoderDrive(0.9, 0.15,0.4,0, 60, 180,0, 0, 0, false);//0.9, 0.2_0.4
                //swerve to goldMineral
                encoderDrive(0.98, 0.6,0.15,0, 180, 60,0, 0, 0, false);
                                                        //0.95seconds,1.5_||_0.6_0.05
                //arc past the depot
                //encoderDrive(3,0.4,0.4,0,35,-35,0,0,0,false);//14
                //~180 degree tank turn
                //encoderDrive(1.5, 0.4, 0.4, 0, 6, 6, 0, 0, 0, false);
                //drive into the depot by 6"
                motorFlapper.setPower(-0.5);
                sleep(2000);
                //servoCrFlapper.setPower(0);//wait 1.5 sec, then turn it off
                //encoderDrive(7, 0.25,0.25,1, -68, -68, -4.0, 0, 0, false);
                encoderDrive(7, 0.25,0.25,1, -68, -68, 0, 0, 0, false);
                //encoderDrive(7, 0, 0, 1, 0, 0, -7.5, 0, 0, false);
                //back into the crater
            }






            motorFlapper.setPower(0);
            sleep(100);
            //last line of autonomous logical code
        }
    }


    //end of main, and start of assortment of methods
    //lDTs, rDTs, L", R" may be attainable with fastestDTs, longestDT", ratio(positive and/or negative)

    /**
     * function to control all motors of robot by encoder
     *
     * @param timeOut        seconds to complete encoder command
     * @param leftDTSpeed    speed of left motors of drivetrain
     * @param rightDTSpeed   speed of right motors of drivetrain
     * @param auxSpeed          speed of all motors for encoder command
     * @param mtrLeftInches  inches for left motors of drivetrain
     * @param mtrRightInches inches for right motors of drivetrain
     * @param mtrLiftInches  inches for lift motor to actuate
     */
    public void encoderDrive(double timeOut, double leftDTSpeed, double rightDTSpeed, double auxSpeed, double mtrLeftInches, double mtrRightInches,
                             double mtrLiftInches, double mtrShouderDeg, double mtrRailInches, boolean strafeVar) {
        //double driveTimeSnapshot = this.time;

        motorSetModes(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorSetModes(DcMotor.RunMode.RUN_USING_ENCODER);
        int newLeftTarget, newRightTarget, newMtrLiftTarget, newMtrShoulderTarget, newMtrRailTarget;
        // Ensure that the opmode is still active
        if (opModeIsActive()) {
            // Determine new target position, and pass to motor controller
            newLeftTarget = motorLeft.getCurrentPosition() + (int) (CPI_DRIVE_TRAIN * mtrLeftInches);
            newRightTarget = motorRight.getCurrentPosition() + (int) (CPI_DRIVE_TRAIN * mtrRightInches);
            newMtrLiftTarget = motorLift.getCurrentPosition() + (int) (CPI_LIFT * mtrLiftInches);
            newMtrShoulderTarget = motorShoulder.getCurrentPosition() + (int) (CPR_SHOULDER * mtrShouderDeg);
            newMtrRailTarget = motorRail.getCurrentPosition() + (int) (CPI_RAIL * mtrRailInches);

            motorSetTargetPos(newLeftTarget, newRightTarget, newMtrLiftTarget,
                    newMtrShoulderTarget, newMtrRailTarget, strafeVar);

            // Turn On RUN_TO_POSITION
            motorSetModes(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            motorLift.setPower(Math.abs(auxSpeed));
            drive(Math.abs(leftDTSpeed), Math.abs(rightDTSpeed));

            double thisTimeOut = this.time + timeOut;


            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (!isStopRequested() && opModeIsActive() && (motorLeft.isBusy() || motorRight.isBusy() ||
                    motorLift.isBusy() || motorShoulder.isBusy() || motorRail.isBusy())) {

                if (this.time >= thisTimeOut) {
                    timeOutCount++;
                    break;
                }


                // Display data for the driver.
                telemetry.addData("GTime", (int) (this.time - gameTimeSnapShot) + " GoldLoc"+ goldSample);
                telemetry.addData("Path1", "Running to %7d :%7d", newLeftTarget, newRightTarget);
                telemetry.addData("Path2", "Running at %7d :%7d",
                        motorLeft.getCurrentPosition(),
                        motorRight.getCurrentPosition());

                telemetry.addData("FLp.", motorLeft.getPower()+",FLpos."+motorLeft.getCurrentPosition()+",Ltar."+newLeftTarget);
                telemetry.addData("FRp.", motorRight.getPower()+",FRpos."+motorRight.getCurrentPosition()+"," +
                        "Rtar."+newRightTarget);
                telemetry.addData("BLp", motorLeft2.getPower()+",BLpos."+motorLeft2.getCurrentPosition());
                telemetry.addData("BRp", motorRight2.getPower()+",BRpos."+motorRight2.getCurrentPosition());
                telemetry.addData("Lmtr-PWR:" + motorLeft.getPower(), " Rmtr-PWR:" + motorRight.getPower());
                telemetry.addData("Lift-POS:", motorLift.getCurrentPosition()+"_"+newMtrLiftTarget + "PWR:" + motorLift.getPower());
                telemetry.addData("Num of cuts", timeOutCount);

                telemetry.update();
            }

            // Stop all motion;
            drive(0, 0);
            motorLift.setPower(0);
            motorShoulder.setPower(0);
            motorRail.setPower(0);

            // Turn off RUN_TO_POSITION
            motorSetModes(DcMotor.RunMode.RUN_USING_ENCODER);
            //sleep(250);
        }

    }

    public void drive(double left, double right) {
        motorLeft.setPower(left);
        motorLeft2.setPower(left);
        motorRight.setPower(right);
        motorRight2.setPower(right);
    }

    public void motorSetModes(DcMotor.RunMode modeName) {
        motorLeft.setMode(modeName);
        motorLeft2.setMode(modeName);
        motorRight.setMode(modeName);
        motorRight2.setMode(modeName);
        motorLift.setMode(modeName);
        motorShoulder.setMode(modeName);
        motorRail.setMode(modeName);
    }

    public void motorSetTargetPos(int targetLeft, int targetRight, int targetMtrLift,
                                  int targetMtrShoulder, int targetMtrRail, boolean strafe) {
        if (!strafe) {
            motorLeft.setTargetPosition(targetLeft);
            motorLeft2.setTargetPosition(targetLeft);
            motorRight.setTargetPosition(targetRight);
            motorRight2.setTargetPosition(targetRight);
        } else {
            motorLeft.setTargetPosition(-targetLeft);
            motorLeft2.setTargetPosition(targetLeft);
            motorRight.setTargetPosition(targetRight);
            motorRight2.setTargetPosition(-targetRight);
        }
        motorLift.setTargetPosition(targetMtrLift);
        motorShoulder.setTargetPosition(targetMtrShoulder);
        motorRail.setTargetPosition(targetMtrRail);
    }


}
