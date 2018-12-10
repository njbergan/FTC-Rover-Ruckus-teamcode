package org.firstinspires.ftc.teamcode.opmodesCurrent;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

//import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
//import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
//import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
//import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
//import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

@Autonomous(name = "Auto Depot Shelby 4")

public class Auto_Depot_Shelby_4 extends LinearOpMode {

    public static final String TAG = "Vuforia VuMark Sample";
    //hardware vars
    private DcMotor motorLeft, motorLeft2,
            motorRight, motorRight2,
            motorLift;

    private Servo servoMarker;

    //private ColorSensor sensorColor;
    //private double colorDuration;

    //private RelicRecoveryVuMark vuMark;
    // private VuforiaLocalizer vuforia;

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

    @Override
    public void runOpMode() {

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

        // get a reference to the RelativeLayout so we can change the background
        // color of the Robot Controller app to match the hue detected by the RGB sensor.
        //int relativeLayoutId = hardwareMap.appContext.getResources().getIdentifier("RelativeLayout", "id", hardwareMap.appContext.getPackageName());
        //final View relativeLayout = ((Activity) hardwareMap.appContext).findViewById(relativeLayoutId);





        /*
         * To start up Vuforia, tell it the view that we wish to use for camera monitor (on the RC phone);
         * If no camera monitor is desired, use the parameterless constructor instead (commented out below).
         * OR...  Do Not Activate the Camera Monitor View, to save power
         * VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();
         */
        // int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        // VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
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
       /* parameters.vuforiaLicenseKey = "AYNyvIr/////AAAAGab/vxrDZkmikc0DLSgRj7kjE9EJpHUguaSu4fsW" +
                "ddhjNQWPOlsALguxr8DNg1GtlenLp2jPhZQu9bL6eYqXB8hA36CHxlGHhkaH4u1FlB+eUB5rq9Xtc8Q" +
                "rK1TbkKbE2dkRVycImituP55+E85iOAw46WNqw4z/TEzDvJhKvYjo5NEek31na+OiSyFgHTs4Cxm3aG" +
                "mugGzY6mTXpmacJ1D7WXQkpqv1v78kpct3qljRxLwRINyRyhdpCw1XkWs2S3f8UwwZaQLhbPUx45Xwz" +
                "N4F3/Fb8xjlEKeSVaDOTzAOaLeTQyWD7UZhAiyCbLPV7W1aO9sszJBSPjFfeAa30sbJNAKOORxhlqbf" +
                " qvXFEuLc";
                */
        /*
         * Then we indicate which camera on the RC that we wish to use.
         * Here we chose the back (HiRes) camera (for greater range), but
         * for a competition robot, the front camera might be more convenient.
         */
        // parameters.cameraDirection = VuforiaLocalizer.CameraDirection.FRONT;
        // this.vuforia = ClassFactory.createVuforiaLocalizer(parameters);
        /*
         * Load the data set containing the VuMarks for Relic Recovery. There's only one trackable
         * in this data set: all three of the VuMarks in the game were created from this one template,
         * but differ in their instance id information.
         * @see VuMarkInstanceId
         */
        // VuforiaTrackables relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        // VuforiaTrackable relicTemplate = relicTrackables.get(0);
        // relicTemplate.setName("relicVuMarkTemplate"); // can help in debugging; otherwise not necessary


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


                /*
                //colorDuration = this.time;
                while (opModeIsActive() && colorDuration >= (this.time - 3) ) {
                    // convert the RGB values to HSV values.
                    // multiply by the SCALE_FACTOR.
                    // then cast it back to int (SCALE_FACTOR is a double)
                    Color.RGBToHSV((int) (sensorColor.red() * SCALE_FACTOR),
                            (int) (sensorColor.green() * SCALE_FACTOR),
                            (int) (sensorColor.blue() * SCALE_FACTOR),
                            hsvValues);

                    // send the info back to driver station using telemetry function.
                    telemetry.addData("Red  ", sensorColor.red());
                    telemetry.addData("Blue ", sensorColor.blue());
                    telemetry.addData("Alpha", sensorColor.alpha());

                    if( sensorColor.blue() /2 > sensorColor.red()) {
                        relativeLayout.post(new Runnable() {
                            public void run() {
                                relativeLayout.setBackgroundColor(Color.RED);
                            }
                        });
                        sleep(500);
                        break;
                    } else if (sensorColor.red() /2 > sensorColor.blue()) {
                        relativeLayout.post(new Runnable() {
                            public void run() {
                                relativeLayout.setBackgroundColor(Color.BLUE);
                            }
                        });
                        sleep(500);
                        break;
                    }
                    */
                    /*
                    // change the background color to match the color detected by the RGB sensor.
                    // pass a reference to the hue, saturation, and value array as an argument
                    // to the HSVToColor method.
                    relativeLayout.post(new Runnable() {
                        public void run() {
                            relativeLayout.setBackgroundColor(Color.HSVToColor(0xff, values));
                        }
                    });
                    */
            //telemetry.update();

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


            encoderDrive(7, 0.35, 47, 47, 0, false);//was 50L,40R
            servoMarker.setPosition(0.8);
            sleep(1000);


            encoderDrive(5,0.5,-26,-26,0,false);
            //backup past samples towards lander
            encoderDrive(5,0.5,14,-14,0,false);
            //turn to backface opponent crater
            encoderDrive(8,0.5,-53,-53,-7,false);//53,51
            //drive opponent crater

            //<Noah>
            //encoderDrive( 10,  0.35, 30, 30,  0,  true);
            //Strafe to wall after depot deposit</Noah>


            //drive towards depot
            //encoderDrive(5, .35, -15, -15, 0, false);//servo controlled
            //back away from depot to make marker fall off
            //encoderDrive(6, 1, 20, 20, -8, false);
            //drive back into depot to push marker in




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

            //sleep(250);   // optional pause after each move in milliseconds
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
