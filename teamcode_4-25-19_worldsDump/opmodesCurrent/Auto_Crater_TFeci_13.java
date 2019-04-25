package org.firstinspires.ftc.teamcode.opmodesCurrent;
    //tells java where this file is located in, relative to the root (src) folder,
    //there can only be one file of this name, in this package, but the filename can exist elsewhere
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaRoverRuckus;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TfodRoverRuckus;

import java.util.List;
    //tells java what 'interfaces' will be used to simplify the programmer's workload

//@Autonomous(name = "Auto Crater 13")//lists this file as an Opmode to the phone as the name in quotes

public class Auto_Crater_TFeci_13 extends LinearOpMode {
    //tells java that the instructions start here,
    //and the file (class) will follow the LinearOpMode defined structure

    private static final double
            CPR_20 = 560, CPI_DRIVE_TRAIN = CPR_20 / (4 * Math.PI), CPI_LIFT = CPR_20 * 2.25,
            CPR_60 = 1680, CPR_SHOULDER = CPR_60 * 9, CPI_RAIL = CPR_60;
    private int timeOutCount = 0, goldSample; //1: left, 2: center, 3: right, -1: unknown

    /*create our motor encoder multipliers. Done by determining each motor's Counts Per Revolution
    *or Counts Per Inch. Then if on a wheel or gear that isn't 1 inch in diameter, a multiplier is added
    */

    //encoder vars      formula:
    //(COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.1415)in terms of Math.PI
    //hardware vars
    private DcMotor motorLeft, motorLeft2,
            motorRight, motorRight2, motorLift,
            motorShoulder, motorRail, motorFlapper;


    //it takes 3 shaft rotations to move the actuator 1 inch this case uses a 1:2/1.5 gear dilation
    //gear ratio * cycles per rotation (28)


    //private CRServo servoCrFlapper;   a servo was once used as a flapper
    private double gameTimeSnapShot = 0;//used for recording moments in time for calculations

    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    //private static final String LABEL_SILVER_MINERAL = "Silver Mineral";  once used

    private VuforiaRoverRuckus vuforiaRoverRuckus;//create our objects for TF obj recognition
    private TfodRoverRuckus tfodRoverRuckus;

    @Override
    public void runOpMode() {
        telemetry.addLine("Wait for Camera to Init");
        telemetry.update();//simple telemetry (data printed on the DriverStation, to instruct users)

        motorLeft = hardwareMap.dcMotor.get("motor_1");//connect the coded objects to the hardware map
        motorRight = hardwareMap.dcMotor.get("motor_2");
        motorLeft2 = hardwareMap.dcMotor.get("motor_3");
        motorRight2 = hardwareMap.dcMotor.get("motor_4");

        motorLift = hardwareMap.dcMotor.get("motor_5");
        motorShoulder = hardwareMap.dcMotor.get("motor_6");
        motorRail = hardwareMap.dcMotor.get("motor_7");
        motorFlapper = hardwareMap.dcMotor.get("motor_8");


        //servoCrFlapper = hardwareMap.crservo.get("servo_0");//negative to spit out marker


        //so you don't have to wire red to black, to maintain program logic (now illegal)
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
        if (!opModeIsActive()) {
            //telemetry.addData("Testing", debugSteps == 2);
            tfodRoverRuckus.activate();//start a camera related process
            // Put run blocks here.
            gameTimeSnapShot = this.time;
            //while (!opModeIsActive() && this.time <= (5 + gameTimeSnapShot)) {//1 second,0.5, 1.5
            while (!opModeIsActive()) {
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

        /*To better understand the comparisons of TFOD, you need to know that the camera is upside down
        *Then you need to stand outside the field at the corner, looking towards the lander
        *Then you need to take your left hand and cover up the leftmost mineral location, closing your left eye
        *Now you see what the robot see's, what a headache!
         */





        gameTimeSnapShot = this.time;//take a new snapshot (record the current game time)


        //logical robot movements based on encoders

        if (opModeIsActive() && this.time <= (27.5 + gameTimeSnapShot) && !isStopRequested()) {
            //limits code to 30 seconds (27.5+2 from TFOD)

            //autonomous encoder-based code starts after this line

            motorLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motorLeft2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motorRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motorRight2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motorLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            //set the motors to brake rather than coasting when arriving at no power input.

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
            encoderDrive(5.5, 0,0,1, 0, 0,
                    7.5, 0, 0, false);//was lift 7, timeont 7.3
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
                encoderDrive(0.9, 0.45,0.15,0, 180, 60, 0, 0, 0, false);//0.15,0.45,15",45"

                encoderDrive(0.6, 0.45,0.15,0, -180, -60, 0, 0, 0, false);//0.15,0.45,15",45"

                encoderDrive(2, 0.35,0.35,0, -16.75, 16.75, 0, 0, 0, false);//18

                encoderDrive(2.5, 0.25, 0.37, 0, 200, 240, 0, 0, 0, false);
                motorFlapper.setPower(-0.5);
                sleep(2000);
                //encoderDrive(5, 0.25,0.25,1, -68, -68, -4.0, 0, 0, false);
                encoderDrive(5, 0.25,0.25,1, -68, -68, 0, 0, 0, false);

                //timeout 7, now 5

            } else if (goldSample == 2) {
                encoderDrive(2, 0.3, 0.3, 0, 23.5, 23.5, 0, 0, 0, false);//25"
                //drop
                encoderDrive(1.2, 0.3, 0.3, 0, -10, -10, 0, 0, 0, false);
                //strafe off hook
                encoderDrive(2, 0.35, 0.35, 0, -15, 15, 0, 0, 0, false);//18
                //backup to straighten
                encoderDrive(2.5, 0.27, 0.36, 0, 200, 240, 0, 0, 0, false);
                motorFlapper.setPower(-0.5);        //LdtS 25, RdtS 36
                sleep(2000);
                //encoderDrive(5, 0.25, 0.25, 1, -70, -70, -4.0, 0, 0, false);
                encoderDrive(5, 0.25, 0.25, 1, -70, -70, 0, 0, 0, false);

                //mtrLeft 68, mtrRight 68
            } else {
            //} else if (goldSample == 1) {
                encoderDrive(1.9, 0.15,0.45,0, 60, 180,//0.15,0.45,15",45"
                        0, 0, 0, false);
                encoderDrive(2.5, 0.2, 0.2, 0, 40, 40, 0, 0, 0, false);
                motorFlapper.setPower(-0.5);
                sleep(2000);
                //encoderDrive(5, 0.25,0.25,1, -68, -68, -4.0, 0, 0, false);
                encoderDrive(5, 0.25,0.25,1, -68, -68, 0, 0, 0, false);

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
            while (opModeIsActive() && (motorLeft.isBusy() || motorRight.isBusy() ||
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
