/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.firstinspires.ftc.teamcode.Auto_Class_Package_1;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
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




public class Auto_Class_3_2 extends LinearOpMode {



    private static final String TAG = "Vuforia VuMark Sample";
//hardware vars
    DcMotor motorLeft,  motorLeft2,
            motorRight, motorRight2,
            motorLift;

    private Servo servoLiftLeft, servoLiftRight;
//logical var
    //private boolean started_1 = false;  //to reset start time once

    double cryptoboxKeySpecificDrive = 0;

    RelicRecoveryVuMark vuMark;

//camera vars
    private OpenGLMatrix lastLocation = null;
    /*
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    private VuforiaLocalizer vuforia;

//encoder vars
    private static final double     COUNTS_60_PER_INCH = 1680 / 3.14;
    private static final double     COUNTS_40_PER_INCH = 1120 / 16.25;
            //(COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.1415)


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
         * Here we chose the back (HiRes) camera (for greater range), but
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

        waitForStart();                         //transition from init to start

        resetStartTime();
        relicTrackables.activate();

//rev hub 1
        //motors
        motorLeft = hardwareMap.dcMotor.get("motor_1");
        motorRight = hardwareMap.dcMotor.get("motor_2");
        motorLeft2 = hardwareMap.dcMotor.get("motor_3");
        motorRight2 = hardwareMap.dcMotor.get("motor_4");
        //servos
        servoLiftLeft = hardwareMap.servo.get("servo_2");
        servoLiftRight = hardwareMap.servo.get("servo_3");
//rev hub 2
        //motors
        motorLift = hardwareMap.dcMotor.get("motor_5");


        //so you don't have to wire red to black, to which maintains program logic
        motorRight.setDirection(DcMotor.Direction.REVERSE);
        motorRight2.setDirection(DcMotor.Direction.REVERSE);

        //telemetry sends data to print onto Driver Station phone
        telemetry.addLine("Drive Base TeleOp\nInit Opmode");


        while (!opModeIsActive()) {
            this.time = 0;
            resetStartTime();
            glyphServos((byte) 2);
        }




        //end of logical program
    }

    String format(OpenGLMatrix transformationMatrix) {
        return (transformationMatrix != null) ? transformationMatrix.formatAsTransform() : "null";
    }


    public void Init() {

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
         * Here we chose the back (HiRes) camera (for greater range), but
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
    }




    /**
     *
     * @param left Left cryptobox drive distance
     * @param right Right cryptobox drive distance
     * @param center Center and Default cryptobox drive distance
     */
    void cameraSense(double left, double right, double center) {
        VuforiaTrackables relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        VuforiaTrackable relicTemplate = relicTrackables.get(0);
        relicTemplate.setName("relicVuMarkTemplate"); // can help in debugging; otherwise not necessary
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
                    cryptoboxKeySpecificDrive = left;      //45.63
                    break;
                } else if (vuMark == RelicRecoveryVuMark.RIGHT) {
                    cryptoboxKeySpecificDrive = right;      //30.37
                    break;
                } else if (vuMark == RelicRecoveryVuMark.CENTER) {
                    cryptoboxKeySpecificDrive = center;         //38
                    break;
                }

            }
            else {

                cryptoboxKeySpecificDrive = center;         //38
                telemetry.addData("VuMark", "not visible");

            }

            telemetry.update();
        }
    }





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
            while (opModeIsActive() && (motorLeft.isBusy() || motorRight.isBusy() || motorLift.isBusy() )   ) {

                // Display it for the driver.
                telemetry.addData("Path1",  "Running to %7d :%7d", newLeftTarget,  newRightTarget);
                telemetry.addData("Path2",  "Running at %7d :%7d",
                        motorLeft.getCurrentPosition(),
                        motorRight.getCurrentPosition());
                telemetry.addData("Lmtr-PWR:" + motorLeft.getPower(), "Rmtr-PWR:" + motorRight.getPower());
                telemetry.addData("Lift-PWR:", motorLift.getCurrentPosition());

                telemetry.update();
            }

            // Stop all motion;
            drive(0,0);
            motorLift.setPower(0);


            // Turn off RUN_TO_POSITION
            motorSetModes(DcMotor.RunMode.RUN_USING_ENCODER);

            sleep(1000);   // optional pause after each move (sleeps for 1 second)
        }

    }
    /**
     * @param x   when: "(byte) [0,1,2]" - :closed, 1:released, 2:grab
     */
    public void glyphServos(byte x) {
        if (x == 0) {						//servos init
            servoLiftLeft.setPosition(0.115);
            servoLiftRight.setPosition(0.75);
        } else if (x == 1) {				//glyph release
            servoLiftLeft.setPosition(0.55);		//Left: 55 and 40	  Right: 30 and 45
            servoLiftRight.setPosition(0.30);		//opposite and subtract 10, or diff:15
        } else if (x == 2) {				//glyph grab
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
