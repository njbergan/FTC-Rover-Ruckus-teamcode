package org.firstinspires.ftc.teamcode.opmodesCurrent;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.VuforiaRoverRuckus;
import org.firstinspires.ftc.robotcore.external.tfod.TfodRoverRuckus;

@Autonomous(name = "Auto Crater 6 TF")

public class Auto_Crater_TF_6 extends LinearOpMode {
    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";

    private VuforiaRoverRuckus vuforiaRoverRuckus;
    private TfodRoverRuckus tfodRoverRuckus;

    public static final String TAG = "Vuforia VuMark Sample";
    //hardware vars
    private DcMotor motorLeft, motorLeft2,
            motorRight, motorRight2,
            motorLift;

    private Servo servoMarker;


    private static final double CPI_DRIVE_TRAIN = 560 / (Math.PI * 4),//was 4.75"
    //CPI_SHOULDER = 1680 / (Math.PI * 6),
    //CPI_ELBOW = 1680 / Math.PI,
    CPI_LIFT = 103.6 * 4;//560 for a 20:1, was a dilation of 3
    //it takes 3 shaft rotations to move the actuator 1 inch this case uses a 1:4/3 gear dilation
    //gear ratio * cycles per rotation (28)


    //int relativeLayoutId;
    //View relativeLayout;

    int timeOutCount = 0;
    private double gameTimeSnapShot = 0;

    @Override
    public void runOpMode() {

        telemetry.addData("Time: ", this.time);


        motorLeft = hardwareMap.dcMotor.get("motor_1");
        motorRight = hardwareMap.dcMotor.get("motor_2");
        motorLeft2 = hardwareMap.dcMotor.get("motor_3");
        motorRight2 = hardwareMap.dcMotor.get("motor_4");

        motorLift = hardwareMap.dcMotor.get("motor_5");
        //servoMarker = hardwareMap.servo.get("servo_1");


        //so you don't have to wire red to black, to maintain program logic
        motorLeft.setDirection(DcMotor.Direction.REVERSE);
        motorLeft2.setDirection(DcMotor.Direction.REVERSE);
        motorRight.setDirection(DcMotor.Direction.FORWARD);
        motorRight2.setDirection(DcMotor.Direction.FORWARD);
        motorLift.setDirection(DcMotor.Direction.FORWARD);


        //telemetry sends data to print onto both phones
        telemetry.addLine("Drive Base TeleOp\nInit Opmode");


        //sensorColor = hardwareMap.get(ColorSensor.class, "color_1");
        // hsvValues is an array that will hold the hue, saturation, and value information.
        float hsvValues[] = {0F, 0F, 0F};

        // values is a reference to the hsvValues array.
        final float values[] = hsvValues;

        // sometimes it helps to multiply the raw RGB values with a scale factor
        // to amplify/attentuate the measured values.
        final double SCALE_FACTOR = 255;

        telemetry.addData(">", "Turn on 30s clock\nPress Play to start");
        telemetry.update();


        waitForStart();                         //init




        //relicTrackables.deactivate();


        //resetStartTime();
        gameTimeSnapShot = this.time;

        //relicTrackables.activate();


        //logical robot movements based on encoders

        if (opModeIsActive() && this.time <= (29.5 + gameTimeSnapShot)) {                  //limits code to 30 seconds

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

            // telemetry.addData("VuMark", "%s visible", vuMark);

            //servoMarker.setPosition(0.47);
            encoderDrive(3, 1, 0, 0, 8, false);//was lift 7
            //drop
            //encoderDrive(2,1,1,1,0,false);
            //drive away from hook
            sleep(100);
            encoderDrive(3, 0.35, 4, 4, 0, true);// was strafe 3.125,6
            //strafe off hook
            sleep(100);
            encoderDrive(1.5,0.4,-6,-6,0,false);
            //backup to straighten
            sleep(100);
            encoderDrive(8,0.2,24,24,0,false);
            //drive to crater, knocking middle sample
            sleep(100);
            encoderDrive(3,0.3,-10,-10,0, false);
            //backup
            sleep(100);

            encoderDrive(5,0.35,-17,17,0,false);//18
            //turn 45 degreesish
            sleep(100);
            //turn towards depot (but after driving from this turn, it will hit wall)
            encoderDrive(8,0.35,44,44,0,false);//was driving 45":11-15-18,43", 35", 41"
            //drive to a few inches away from wall      down 4 on lift
            sleep(100);
            encoderDrive(3,0.35,-7,7,0,false);//9, 7.5, 8, 7, 6
            //turn to face depot
            sleep(100);
            encoderDrive(8,0.5,40,40,0,false);
            //drive to depot            down 3 on lift
            //servoMarker.setPosition(0.8);
            sleep(1000);
            //encoderDrive(0.5, 0.5, -1, 1, 0, false);
            encoderDrive(12,0.5, -70, -70, -7, false);
            //move servo to drop marker, and wait a second, assuming it can complete in that time

            //last line of autonomous logical code
        }
    }
               /* relicTrackables.deactivate();
                drive(0,0);

                telemetry.addLine("Program is done");



        // Set the panel back to the default color
        relativeLayout.post(new Runnable() {
            public void run() {
                relativeLayout.setBackgroundColor(Color.WHITE);
            }
        });
*/


    //end of main, and start of assortment of methods

    /**
     * function to control all motors of robot by encoder
     *
     * @param timeOut        seconds to complete encoder command
     * @param speed          speed of all motors for encoder command
     * @param mtrLeftInches  inches for left motors of drivetrain
     * @param mtrRightInches inches for right motors of drivetrain
     * @param mtrLiftInches  inches for lift motor to actuate
     */
    public void encoderDrive(double timeOut, double speed, double mtrLeftInches, double mtrRightInches,
                             double mtrLiftInches, boolean strafeVar) {
        //double driveTimeSnapshot = this.time;

        motorSetModes(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorSetModes(DcMotor.RunMode.RUN_USING_ENCODER);
        int newLeftTarget, newRightTarget, newMtrLiftTarget;
        // Ensure that the opmode is still active
        if (opModeIsActive()) {
            // Determine new target position, and pass to motor controller
            newLeftTarget = motorLeft.getCurrentPosition() + (int) (CPI_DRIVE_TRAIN * mtrLeftInches);
            newRightTarget = motorRight.getCurrentPosition() + (int) (CPI_DRIVE_TRAIN * mtrRightInches);
            newMtrLiftTarget = motorLift.getCurrentPosition() + (int) (CPI_LIFT * mtrLiftInches);

            motorSetTargetPos(newLeftTarget, newRightTarget, newMtrLiftTarget, strafeVar);

            // Turn On RUN_TO_POSITION
            motorSetModes(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            motorLift.setPower(Math.abs(speed));
            drive(Math.abs(speed), Math.abs(speed));

            double thisTimeOut = this.time + timeOut;


            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (opModeIsActive() && (motorLeft.isBusy() || motorRight.isBusy() ||
                    motorLift.isBusy())) {

                if (this.time >= thisTimeOut) {
                    /*
                    relativeLayout.post(new Runnable() {
                        public void run() {
                            relativeLayout.setBackgroundColor(Color.YELLOW);
                        }
                    });
                    */
                    timeOutCount++;
                    telemetry.addData("Num of cuts", timeOutCount);
                    break;
                }


                // Display data for the driver.
                telemetry.addData("GTime", (int)(this.time -gameTimeSnapShot));
                telemetry.addData("Path1", "Running to %7d :%7d", newLeftTarget, newRightTarget);
                telemetry.addData("Path2", "Running at %7d :%7d",
                        motorLeft.getCurrentPosition(),
                        motorRight.getCurrentPosition());
                telemetry.addData("Lmtr-PWR:" + motorLeft.getPower(), " Rmtr-PWR:" + motorRight.getPower());
                telemetry.addData("Lift-POS:", motorLift.getCurrentPosition() + "PWR:" + motorLift.getPower());
                //telemetry.addData("Servo1-POS:", servoMarker.getPosition()+ "__TO:"+ timeOut);

                telemetry.update();
            }

            // Stop all motion;
            drive(0, 0);
            motorLift.setPower(0);

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
    }

    public void motorSetTargetPos(int targetLeft, int targetRight, int targetMtrLift, boolean strafe) {

        if (!strafe) {
            motorLeft.setTargetPosition(targetLeft);
            motorLeft2.setTargetPosition(targetLeft);
            motorRight.setTargetPosition(targetRight);
            motorRight2.setTargetPosition(targetRight);
            motorLift.setTargetPosition(targetMtrLift);
        } else if (strafe) {
            motorLeft.setTargetPosition(-targetLeft);
            motorLeft2.setTargetPosition(targetLeft);
            motorRight.setTargetPosition(targetRight);
            motorRight2.setTargetPosition(-targetRight);
            motorLift.setTargetPosition(targetMtrLift);
        }
    }
}
