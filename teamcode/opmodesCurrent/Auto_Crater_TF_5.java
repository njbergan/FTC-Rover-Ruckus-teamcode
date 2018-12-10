package org.firstinspires.ftc.teamcode.opmodesCurrent;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;

@Autonomous(name = "Auto Crater TF 4")

public class Auto_Crater_TF_5 extends LinearOpMode {

    public static final String TAG = "Vuforia VuMark Sample";
    //hardware vars
    private DcMotor motorLeft, motorLeft2,
            motorRight, motorRight2,
            motorLift;

    private Servo servoMarker;



    //encoder vars      formula:
    //(COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.1415)in terms of Math.PI

    //changed Math.PI to 3.1415
    //private static final double     COUNTS_20_PER_INCH = 560 / 3.1415,
    //COUNTS_60_PER_INCH = 1680 / 3.1415,
    //COUNTS_40_PER_INCH = 1120 / 3.1415,
    //COUNTS_40_PER_INCH_DRIVE = 1120 / 3.1415 * 5; //16.25
    //CPI_Drive_Train= 1120 / (Math.pi * 5.1725)

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



    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";

    /**
     * -2: instantized
     * -1: left
     * 0: center
     * 1: right
     */
    private int goldMineralEndLocation = -2;

    /*
     * IMPORTANT: You need to obtain your own license key to use Vuforia. The string below with which
     * 'parameters.vuforiaLicenseKey' is initialized is for illustration only, and will not function.
     * A Vuforia 'Development' license key, can be obtained free of charge from the Vuforia developer
     * web site at https://developer.vuforia.com/license-manager.
     *
     * Vuforia license keys are always 380 characters long, and look as if they contain mostly
     * random data. As an example, here is a example of a fragment of a valid key:
     *      ... yIgIzTqZ4mWjk9wd3cZO9T1axEqzuhxoGlfOOI2dRzKS4T0hQ8kT ...
     * Once you've obtained a license key, copy the string from the Vuforia web site
     * and paste it in to your code on the next line, between the double quotes.
     */
    private static final String VUFORIA_KEY = "AYNyvIr/////AAAAGab/vxrDZkmikc0DLSgRj7kjE9EJpHUguaSu4" +
            "fsWddhjNQWPOlsALguxr8DNg1GtlenLp2jPhZQu9bL6eYqXB8hA36CHxlGHhkaH4u1FlB+eUB5rq9Xtc8QrK1Tb" +
            "kKbE2dkRVycImituP55+E85iOAw46WNqw4z/TEzDvJhKvYjo5NEek31na+OiSyFgHTs4Cxm3aGmugGzY6mTXpma" +
            "cJ1D7WXQkpqv1v78kpct3qljRxLwRINyRyhdpCw1XkWs2S3f8UwwZaQLhbPUx45XwzN4F3/Fb8xjlEKeSVaDOTz" +
            "AOaLeTQyWD7UZhAiyCbLPV7W1aO9sszJBSPjFfeAa30sbJNAKOORxhlqbfqvXFEuLc";

    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    private VuforiaLocalizer vuforia;

    /**
     * {@link #tfod} is the variable we will use to store our instance of the Tensor Flow Object
     * Detection engine.
     */
    private TFObjectDetector tfod;



    @Override
    public void runOpMode() {

        // The TFObjectDetector uses the camera frames from the VuforiaLocalizer, so we create that
        // first.
        initVuforia();

        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            initTfod();
        } else {
            telemetry.addData("Sorry!", "This device is not compatible with TFOD");
        }

        /** Wait for the game to begin */
        telemetry.addData(">", "Press Play to start tracking");
        telemetry.update();
        //waitForStart();







        telemetry.addData("Time: ", this.time);


        motorLeft = hardwareMap.dcMotor.get("motor_1");
        motorRight = hardwareMap.dcMotor.get("motor_2");
        motorLeft2 = hardwareMap.dcMotor.get("motor_3");
        motorRight2 = hardwareMap.dcMotor.get("motor_4");

        motorLift = hardwareMap.dcMotor.get("motor_5");
        servoMarker = hardwareMap.servo.get("servo_1");


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



        if (opModeIsActive()) {
            /** Activate Tensor Flow Object Detection. */
            if (tfod != null) {
                tfod.activate();
            }

            while (opModeIsActive()) {
                if (tfod != null) {
                    // getUpdatedRecognitions() will return null if no new information is available since
                    // the last time that call was made.
                    List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                    if (updatedRecognitions != null) {
                        telemetry.addData("# Object Detected", updatedRecognitions.size());
                        if (updatedRecognitions.size() == 3) {
                            int goldMineralX = -1,  silverMineralOneX = -1,   silverMineralTwoX = -1,
                                    goldMineralY = -1, silverMineralOneY = -1, silverMineralTwoY = -1;
                            for (Recognition recognition : updatedRecognitions) {
                                if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                                    goldMineralX = (int) recognition.getLeft();
                                } else if (silverMineralOneX == -1) {
                                    silverMineralOneX = (int) recognition.getLeft();
                                } else if (silverMineralTwoX == -1) {
                                    silverMineralTwoX = (int) recognition.getLeft();
                                }
                                if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                                    goldMineralY = (int) recognition.getBottom();
                                } else if (silverMineralOneY == -1) {
                                    silverMineralOneY = (int) recognition.getBottom();
                                } else if (silverMineralTwoY == -1) {
                                    silverMineralTwoY = (int) recognition.getBottom();
                                }
                                telemetry.addData("Xdetection", "done, g:"+goldMineralX+" s1:"+ silverMineralOneX+" s2:"+silverMineralTwoX);
                                telemetry.addData("Ydetection", "done, g:"+goldMineralY+" s1:"+ silverMineralOneY+" s2:"+silverMineralTwoY);

                            }
                            if (goldMineralX != -1 && silverMineralOneX != -1 && silverMineralTwoX != -1) {
                                if (goldMineralX < silverMineralOneX && goldMineralX < silverMineralTwoX) {
                                    //if the gold horizontal value is less than both silver minerals, print left position
                                    telemetry.addData("Gold Mineral Position", "Left");
                                    goldMineralEndLocation = -1;
                                } else if (goldMineralX > silverMineralOneX && goldMineralX > silverMineralTwoX) {
                                    //if the gold horizontal value is greater than than both silver minerals, print left position
                                    telemetry.addData("Gold Mineral Position", "Right");
                                    goldMineralEndLocation = 1;
                                } else if (goldMineralX > silverMineralOneX && goldMineralX < silverMineralTwoX) {
                                    telemetry.addData("Gold Mineral Position", "Center");
                                    goldMineralEndLocation = 0;
                                } else {
                                    telemetry.addData("Gold Mineral Position", "SEARCHING");
                                }
                            }
                        }
                        telemetry.update();
                    }
                }
            }
        }

        if (tfod != null) {
            tfod.shutdown();
        }


        //resetStartTime();
        gameTimeSnapShot = this.time;

        //relicTrackables.activate();


        //logical robot movements based on encoders

        if (opModeIsActive() && this.time - 6 <= (29.5 + gameTimeSnapShot)) {                  //limits code to 30 seconds and doesn't start until TF is done

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



            servoMarker.setPosition(0.47);
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

            if (goldMineralEndLocation == 0 || goldMineralEndLocation == -2) {
                encoderDrive(8,0.2,24,24,0,false);
                //drive to crater, knocking middle sample
                sleep(100);
                encoderDrive(3,0.3,-10,-10,0, false);
                //backup
                sleep(100);
            } else if (goldMineralEndLocation == -1) {
                encoderDrive(4, 0.3, 5, 5, 0, true);
                encoderDrive(8,0.2,24,24,0,false);
                //drive to crater, knocking middle sample
                sleep(100);
                encoderDrive(3,0.3,-10,-10,0, false);
                //backup
                sleep(100);
                encoderDrive(4, 0.3, -5, -5, 0, true);
                sleep(100);
            } else if (goldMineralEndLocation == 1) {
                encoderDrive(4, 0.3, -5, -5, 0, true);
                encoderDrive(8,0.2,24,24,0,false);
                //drive to crater, knocking middle sample
                sleep(100);
                encoderDrive(3,0.3,-10,-10,0, false);
                //backup
                sleep(100);
                encoderDrive(4, 0.3, 5, 5, 0, true);
                sleep(100);
            }

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
            servoMarker.setPosition(0.8);
            sleep(1000);
            //encoderDrive(0.5, 0.5, -1, 1, 0, false);
            encoderDrive(12,0.5, -70, -70, -7, false);
            //move servo to drop marker, and wait a second, assuming it can complete in that time
            //encoderDrive(5,0.5,5,-5,0,false);

            //servoMarker.setPosition(0.6);
            //encoderDrive(7, 0.75, 40, 40, -6, false);//was 50L,40R
            //servoMarker.setPosition(1);
            //sleep(1000);

            //encoderDrive(5,1,-30,-30,0,false);
            //backup past samples towards lander
            //encoderDrive(5,0.5,14,-14,0,false);
            //turn to backface opponent crater
            //encoderDrive(7,1,-40,-40,0,false);
            //drive opponent crater





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
                telemetry.addData("Servo1-POS:", servoMarker.getPosition()+ "__TO:"+ timeOut);

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


    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = CameraDirection.BACK;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the Tensor Flow Object Detection engine.
    }

    /**
     * Initialize the Tensor Flow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);
    }


}
