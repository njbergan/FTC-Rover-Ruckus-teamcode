package org.firstinspires.ftc.teamcode.opmodesCurrent;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;

//@Autonomous(name = "Auto Crater 8 tfec")    //TFOD External Webcam. Auto version 8.

public class Auto_Crater_TFec_8 extends LinearOpMode {

    public static final String TAG = "Vuforia VuMark Sample";
    private static final double
            CPR_20 = 560, CPI_DRIVE_TRAIN = CPR_20 / (4 * Math.PI), CPI_LIFT = CPR_20 * 4,
            CPR_60 = 1680, CPR_SHOULDER = CPR_60 * 9, CPI_RAIL = CPR_60;
    int timeOutCount = 0;

    //encoder vars      formula:
    //(COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.1415)in terms of Math.PI
    //hardware vars
    private DcMotor motorLeft, motorLeft2,
            motorRight, motorRight2, motorLift,
            motorShoulder, motorRail;


    //it takes 3 shaft rotations to move the actuator 1 inch this case uses a 1:4/3 gear dilation
    //gear ratio * cycles per rotation (28)


    //int relativeLayoutId;
    //View relativeLayout;
    private CRServo servoCrFlapper;
    private double gameTimeSnapShot = 0;

    @Override
    public void runOpMode() {

        telemetry.addData("Time: ", this.time);


        motorLeft = hardwareMap.dcMotor.get("motor_1");
        motorRight = hardwareMap.dcMotor.get("motor_2");
        motorLeft2 = hardwareMap.dcMotor.get("motor_3");
        motorRight2 = hardwareMap.dcMotor.get("motor_4");

        motorLift = hardwareMap.dcMotor.get("motor_5");
        motorShoulder = hardwareMap.dcMotor.get("motor_6");
        motorRail = hardwareMap.dcMotor.get("motor_7");

        servoCrFlapper = hardwareMap.crservo.get("servo_0");//negative to spit out marker


        //so you don't have to wire red to black, to maintain program logic
        motorLeft.setDirection(DcMotor.Direction.REVERSE);
        motorLeft2.setDirection(DcMotor.Direction.REVERSE);
        motorRight.setDirection(DcMotor.Direction.FORWARD);
        motorRight2.setDirection(DcMotor.Direction.FORWARD);
        motorLift.setDirection(DcMotor.Direction.FORWARD);


        //telemetry sends data to print onto both phones
        telemetry.addLine("Drive Base TeleOp\nInit Opmode");
        telemetry.addData(">", "Turn on 30s clock\nPress Play to start");
        telemetry.update();


        waitForStart();                         //init


        //resetStartTime();
        gameTimeSnapShot = this.time;


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


            //servoCrFlapper.setPower(-1); should deflap the marker
            encoderDrive(7.3, 1, 0, 0, 7.5, 0, 0, false);//was lift 7
            //drop
            //encoderDrive(2,1,1,1,0,false);
            //drive away from hook
            sleep(100);
            encoderDrive(2.2, 0.35, 4, 4, 0, 0, 0, true);// was strafe 3.125,6
            //strafe off hook
            sleep(100);
            encoderDrive(0.5, 0.4, -3, -3, 0, 0, 0, false);
            //backup to straighten
            sleep(100);
            encoderDrive(2, 0.2, 24, 24, 0, 0, 0, false);
            //drive to crater, knocking middle sample
            sleep(100);
            encoderDrive(1.2, 0.3, -10, -10, 0, 0, 0, false);
            //backup
            sleep(100);

            encoderDrive(2, 0.35, -17, 17, 0, 0, 0, false);//18
            //turn 45 degreesish
            sleep(100);
            //turn towards depot (but after driving from this turn, it will hit wall)
            encoderDrive(4, 0.35, 40, 40, 0, 0, 0, false);//was driving 44
            //drive to a few inches away from wall      down 4 on lift
            sleep(100);
            encoderDrive(2, 0.35, -8, 8, 0, 0, 0, false);//7
            //turn to face depot
            sleep(100);
            encoderDrive(4, 0.5, 40, 40, 0, 0, 0, false);
            //drive to depot            down 3 on lift
            servoCrFlapper.setPower(-1);
            sleep(1000);
            //encoderDrive(0.5, 0.5, -1, 1, 0,0,0, false);
            encoderDrive(7, 0.3, -70, -70, 0, 0, 0, false);
            //move servo to drop marker, and wait a second, assuming it can complete in that time
            //encoderDrive(5,0.5,5,-5,0,false);



            servoCrFlapper.setPower(0);
            sleep(100);
            //last line of autonomous logical code
        }
    }


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
                    motorLift.isBusy() || motorShoulder.isBusy() || motorRail.isBusy())) {

                if (this.time >= thisTimeOut) {
                    timeOutCount++;
                    break;
                }


                // Display data for the driver.
                telemetry.addData("GTime", (int) (this.time - gameTimeSnapShot));
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
