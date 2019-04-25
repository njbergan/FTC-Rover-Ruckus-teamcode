package org.firstinspires.ftc.teamcode.opmodesNotebook;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;


@Autonomous(name="Auto Red Audience")
@Disabled
public class Auto_RedAud_3_NOTEBOOK extends LinearOpMode {

    public static final String TAG = "Vuforia VuMark Sample";
    //hardware vars
    private DcMotor motorLeft,  motorLeft2,
            motorRight, motorRight2,
            motorLift;

    private Servo servoLiftLeft, servoLiftRight;
//logical var
    //private boolean started_1 = false;  //to reset start time once


    private RelicRecoveryVuMark vuMark;

    //camera vars
    OpenGLMatrix lastLocation = null;
    /*
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    private VuforiaLocalizer vuforia;

    //encoder vars
    private static final double     COUNTS_60_PER_INCH = 1680 / 3.14,
                                    COUNTS_40_PER_INCH = 1120 / 16.25;
                    //this assumes all 40:1 andymark motors are outputting on 5.2" diameter wheels
    //(COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.1415)

    private final double    CRYPTOBOX_LEFT_COLUMN_DRIVE   = 47.5,  //48.3
                            CRYPTOBOX_RIGHT_COLUMN_DRIVE  = 30,  //30.5

                            CRYPTOBOX_CENTER_COLUMN_DRIVE = 39; //39.75
    private double          cryptoboxKeySpecificDrive  = 39;
                            //in a hectic season, making logic as simple as possible is favorable to
                            //easy programming

    @Override public void runOpMode() {

        telemetry.addData("Time: ", this.time);
        /*
         * To start up Vuforia, tell it the view that we wish to use for camera monitor (on the RC phone);
         * If no camera monitor is desired, use the parameterless constructor instead (commented out below).
         * OR...  Do Not Activate the Camera Monitor View, to save power
         * VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();
         */
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
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
        parameters.vuforiaLicenseKey = "AYNyvIr/////AAAAGab/vxrDZkmikc0DLSgRj7kjE9EJpHUguaSu4fsW" +
                "ddhjNQWPOlsALguxr8DNg1GtlenLp2jPhZQu9bL6eYqXB8hA36CHxlGHhkaH4u1FlB+eUB5rq9Xtc8Q" +
                "rK1TbkKbE2dkRVycImituP55+E85iOAw46WNqw4z/TEzDvJhKvYjo5NEek31na+OiSyFgHTs4Cxm3aG" +
                "mugGzY6mTXpmacJ1D7WXQkpqv1v78kpct3qljRxLwRINyRyhdpCw1XkWs2S3f8UwwZaQLhbPUx45Xwz" +
                "N4F3/Fb8xjlEKeSVaDOTzAOaLeTQyWD7UZhAiyCbLPV7W1aO9sszJBSPjFfeAa30sbJNAKOORxhlqbf" +
                " qvXFEuLc";
        /*
         * Then we indicate which camera on the RC that we wish to use.
         * You may chose the back (HiRes) camera (for greater range), but
         * for a competition robot, the front camera might be more convenient.
         */
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.FRONT;
        this.vuforia = ClassFactory.createVuforiaLocalizer(parameters);
        /*
         * Load the data set containing the VuMarks for Relic Recovery. There's only one trackable
         * in this data set: all three of the VuMarks in the game were created from this one template,
         * but differ in their instance id information.
         * @see VuMarkInstanceId
         */
        VuforiaTrackables relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        VuforiaTrackable relicTemplate = relicTrackables.get(0);
        relicTemplate.setName("relicVuMarkTemplate"); // can help in debugging; otherwise not necessary

        telemetry.addData(">", "Turn on 30s clock\nPress Play to start");
        telemetry.update();



//rev hub 1
        //motors
        motorLeft   = hardwareMap.dcMotor.get("motor_1");
        motorRight  = hardwareMap.dcMotor.get("motor_2");
        motorLeft2  = hardwareMap.dcMotor.get("motor_3");
        motorRight2 = hardwareMap.dcMotor.get("motor_4");
        //servos
        servoLiftLeft	= hardwareMap.servo.get("servo_2");
        servoLiftRight	= hardwareMap.servo.get("servo_3");
//rev hub 2
        //motors
        motorLift	= hardwareMap.dcMotor.get("motor_5");


        //so you don't have to wire red to black, while maintaining program logic
        motorRight.setDirection(DcMotor.Direction.REVERSE);
        motorRight2.setDirection(DcMotor.Direction.REVERSE);

        //telemetry sends data to print onto Driver Station phone
        telemetry.addLine("Drive Base TeleOp\nInit Opmode");


        waitForStart();             //transition from init to start

        resetStartTime();
        relicTrackables.activate();


        while (opModeIsActive() && this.time < 5) {

            //telemetry
            telemetry.addData("Time: ", this.time);
            telemetry.addData("LeftCurrentPWR: ", motorLeft.getPower());
            telemetry.addData("RightCurrentPWR: ", motorRight.getPower());
            telemetry.addLine("\n");
            //vuforia

            telemetry.addLine("Program running successfully");
            /*
             * See if any of the instances of {@link relicTemplate} are currently visible.
             * {@link RelicRecoveryVuMark} is an enum which can have the following values:
             * UNKNOWN, LEFT, CENTER, and RIGHT. When a VuMark is visible, something other than
             * UNKNOWN will be returned by {@link RelicRecoveryVuMark#from(VuforiaTrackable)}.
             */
            RelicRecoveryVuMark vuMark = RelicRecoveryVuMark.from(relicTemplate);
            if (vuMark != RelicRecoveryVuMark.UNKNOWN) {

                /* Found an instance of the template. In the actual game, you will probably
                 * loop until this condition occurs, then move on to act accordingly depending
                 * on which VuMark was visible. */
                telemetry.addData("VuMark", "%s visible", vuMark);

                /* For fun, we also exhibit the navigational pose. In the Relic Recovery game,
                 * it is perhaps unlikely that you will actually need to act on this pose information, but
                 * we illustrate it nevertheless, for completeness. */
                OpenGLMatrix pose = ((VuforiaTrackableDefaultListener)relicTemplate.getListener()).getPose();
                telemetry.addData("Pose", format(pose));

                /* We further illustrate how to decompose the pose into useful rotational and
                 * translational components */
                if (pose != null) {
                    VectorF trans = pose.getTranslation();
                    Orientation rot = Orientation.getOrientation(pose, AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES);

                    // Extract the X, Y, and Z components of the offset of the target relative to the robot
                    double tX = trans.get(0);
                    double tY = trans.get(1);
                    double tZ = trans.get(2);

                    // Extract the rotational components of the target relative to the robot
                    double rX = rot.firstAngle;
                    double rY = rot.secondAngle;
                    double rZ = rot.thirdAngle;
                }


                if (vuMark == RelicRecoveryVuMark.LEFT) {
                    cryptoboxKeySpecificDrive = CRYPTOBOX_LEFT_COLUMN_DRIVE;      //45.63, 44.5, 45
                    break;
                } else if (vuMark == RelicRecoveryVuMark.RIGHT) {
                    cryptoboxKeySpecificDrive = CRYPTOBOX_RIGHT_COLUMN_DRIVE;      //30.37, 28.5, 31
                    break;
                } else if (vuMark == RelicRecoveryVuMark.CENTER) {
                    cryptoboxKeySpecificDrive = CRYPTOBOX_CENTER_COLUMN_DRIVE;         //38, N/A, 39, 37.25
                    break;
                }

            }
            else {

                cryptoboxKeySpecificDrive = CRYPTOBOX_CENTER_COLUMN_DRIVE;
                telemetry.addData("VuMark", "not visible");

            }

            telemetry.update();
        }


        //logical robot movements based on encoders

        if (opModeIsActive() && this.time <= 29.5) {                  //limits code to 30 seconds

            //autonomous encoder-based code starts after this line


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

            telemetry.addData("VuMark", "%s visible", vuMark);

            glyphServos((byte) 2);

            encoderDrive(1, 0, 0, 5);    //lift glyph by 5 inches at 20% pwr

            encoderDrive(0.3, cryptoboxKeySpecificDrive,  cryptoboxKeySpecificDrive, 0); //drive preset values at 25% pwr

            encoderDrive(0.3, 11, -11, 0); //turn right for 16, 15.3, 12, 11.5, 12 inches at 25% | turn towards cryptobox

            encoderDrive(0.3,  8.75,   8.75, 0); //drive towards cryptobox, 8, 8.5

            glyphServos((byte) 1);


            encoderDrive(0.3, -10.5, -10.5, 0);   //drive backward facing cryptobox, towards center
            encoderDrive(0.3, 24, -24, 0); //turn to face center, 29, 31, 23.5
            encoderDrive(0.3, -14, -14, 0);   //back into glyph

            encoderDrive(1, 2, 2, -5);      //drive forward, thus not touching the glyph
            // lift glyph by -5 inches at 20% pwr


            //break;  //you don't want to repeat the entire process again, because the starting pos. is different
        }
        //relicTrackables.deactivate();
        motorLeft.setPower(0);
        motorRight.setPower(0);
        motorLeft2.setPower(0);
        motorRight2.setPower(0);
        motorLift.setPower(0);
        telemetry.addLine("Program is done");
     /*
        while (!opModeIsActive()) {
            //resetStartTime();

            motorLeft.setPower(0);
            motorRight.setPower(0);
            motorLeft2.setPower(0);
            motorRight2.setPower(0);
            motorLift.setPower(0);
            telemetry.addLine("Robot is done and waiting");

        }
    */
        //idle();
    }

    String format(OpenGLMatrix transformationMatrix) {
        return (transformationMatrix != null) ? transformationMatrix.formatAsTransform() : "null";
    }


    //end of main, and start of assortment of methods

    /**
     * @param speed the speed as a decimal negative/positive doesn't matter
     * @param leftInches the amount the left motor must travel
     * @param rightInches the amount the right motor must travel
     *
     * Method to perform a relative move, based on encoder counts.
     *  Encoders are not reset as the move is based on the current position.
     *  Move will stop if any of three conditions occur:
     *  1) Move gets to the desired position
     *  2) Move runs out of time
     *  3) Driver stops the opmode running.
     */
    public void encoderDrive(double speed, double leftInches, double rightInches, double glyphLiftInches) {
        int newLeftTarget, newRightTarget, newLiftTarget;
        // Ensure that the opmode is still active
        if (opModeIsActive()) {
            // Determine new target position, and pass to motor controller
            newLeftTarget  =  motorLeft.getCurrentPosition() + (int)(COUNTS_40_PER_INCH * leftInches);
            newRightTarget = motorRight.getCurrentPosition() + (int)(COUNTS_40_PER_INCH * rightInches);
            newLiftTarget  =  motorLift.getCurrentPosition() + (int)(COUNTS_60_PER_INCH * glyphLiftInches);

            motorSetTargetPos(newLeftTarget, newRightTarget, newLiftTarget);

            // Turn On RUN_TO_POSITION
            motorSetModes(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            motorLift.setPower(Math.abs(speed));
            drive(Math.abs(speed), Math.abs(speed));

            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (opModeIsActive() && (motorLeft2.isBusy() || motorRight2.isBusy() || motorLift.isBusy() )   ) {

                // Display it for the driver.
                telemetry.addData("Path1",  "Running to %7d :%7d", newLeftTarget,  newRightTarget);
                telemetry.addData("Path2",  "Running at %7d :%7d",
                        motorLeft2.getCurrentPosition(),
                        motorRight2.getCurrentPosition());
                telemetry.addData("Lmtr-PWR:" + motorLeft2.getPower(), "Rmtr-PWR:" + motorRight2.getPower());
                telemetry.addData("Lift-PWR:", motorLift.getCurrentPosition());

                telemetry.update();
            }

            // Stop all motion;
            drive(0,0);
            motorLift.setPower(0);


            // Turn off RUN_TO_POSITION
            motorSetModes(DcMotor.RunMode.RUN_USING_ENCODER);

            sleep(750);   // optional pause after each move (sleeps for 1 second)
        }

    }
    /**
     * @param x   when: "(byte) [0,1,2]" - :closed, 1:released, 2:grab
     */
    public void glyphServos(byte x) {
        if (x == 0) {						//servos init
            servoLiftLeft.setPosition(0.115);
            servoLiftRight.setPosition(0.75);
        } else
        if (x == 2) {				//glyph release
            servoLiftLeft.setPosition(0.55);		//Left: 55 and 40	  Right: 30 and 45
            servoLiftRight.setPosition(0.30);		//opposite and subtract 10, or diff:15
        } else if (x == 1) {				//glyph grab
            servoLiftLeft.setPosition(0.35);    //40, 35 is tighter
            servoLiftRight.setPosition(0.5);    //45, 50 is tighter
        }
        sleep(1000);
    }
    public void drive(double left,double right) {
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
    public void motorSetTargetPos(int targetLeft, int targetRight, int targetGlyphLift) {
        motorLeft.setTargetPosition(targetLeft);
        motorLeft2.setTargetPosition(targetLeft);
        motorRight.setTargetPosition(targetRight);
        motorRight2.setTargetPosition(targetRight);
        motorLift.setTargetPosition(targetGlyphLift);
    }

}
