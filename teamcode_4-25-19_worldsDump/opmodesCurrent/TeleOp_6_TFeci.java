package org.firstinspires.ftc.teamcode.opmodesCurrent;
    //tells java where this file is located in, relative to the root (src) folder,
    //there can only be one file of this name, in this package, but the filename can exist elsewhere
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaRoverRuckus;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TfodRoverRuckus;

import java.util.List;
    //tells java what 'interfaces' will be used to simplify the programmer's workload

//@TeleOp(name = "TeleOp 6 TFeci")//lists this file as an Opmode to the phone as the name in quotes

public class TeleOp_6_TFeci extends LinearOpMode {
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




        }
    }


    //end of main, and start of assortment of methods
    //lDTs, rDTs, L", R" may be attainable with fastestDTs, longestDT", ratio(positive and/or negative)



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



}
