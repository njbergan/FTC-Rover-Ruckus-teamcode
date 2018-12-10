package org.firstinspires.ftc.teamcode.opmodesNotebook;
    //java needs to know where this file is supposed to be located, as a backup in case a file was-
    //accidentally moved
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import static java.lang.Math.abs;
    //java needs to know what outside objects and files are used, so it can be prepared beforehand


/*
 * All the code aside from importing, goes within a class declaration, this tells Android Studio-
 * to run this using java after preparing the package and import lines
 */

@TeleOp(name = "TeleOp Shelby") //register this opmode to the phone's list of runnable files
@Disabled                       //remove this opmode from the phone's lisf of runnable files


public class TeleOp_Shelby_NOTEBOOK extends OpMode {
    /*the 'public' operator allows other code files to use certain parts of this code, if it were-
    * configured that way
    * the class declaration tells java that the runnable code goes within the brackets here
    * the class object name, must correspond to the file name
    * the extending of the OpMode object allows the class to be structured in a customized way,-
    * following the structure of the OpMode abstract class
    */

//*********** Experimental          Mecanums only
//*********** Rev Hub count: 2


    //TETRIX Motors		-recording the motor type as if this file ran autonomous

    private DcMotor motorLeft, motorLeft2,
            motorRight, motorRight2, motorLift;

    private Servo servoMarkerLimited;
    //private CRServo servoMarkerCR;

    private boolean mecanumDriveMode = true, coastMotors = true;
    private float mecanumStrafe = 0, dominantXJoystick = 0;


    /*
     * Code to run when the op mode is first enabled goes here
     * This is all automatic- it prepares the robot to run loop() without error
     */
    @Override
    public void init() {


//rev hub 1
        motorLeft = hardwareMap.dcMotor.get("motor_1");
        motorRight = hardwareMap.dcMotor.get("motor_2");
        motorLeft2 = hardwareMap.dcMotor.get("motor_3");
        motorRight2 = hardwareMap.dcMotor.get("motor_4");

        motorLift = hardwareMap.dcMotor.get("motor_5");

        servoMarkerLimited = hardwareMap.servo.get("servo_1");
        //servoMarkerCR = hardwareMap.crservo.get("servo_0");

        //so you don't have to wire red to black, to maintain program logic
        motorRight.setDirection(DcMotor.Direction.REVERSE);
        motorRight2.setDirection(DcMotor.Direction.REVERSE);
        //motorLift.setDirection(DcMotor.Direction.REVERSE); //(up makes the motor go negative in the code)

        //telemetry sends data to print onto both phones
        telemetry.addLine("Drive Base TeleOp\nInit Opmode");
    }


    /**
     * The next 2 lines ends the current state of Opmode, and starts Opmode.loop()
     * This enables control of the robot via the gamepad or starts autonomous functions
     */
    @Override
    public void loop() {

        telemetry.addLine("Loop OpMode\ndPadLeft: disable mecanum strafe is "+ !mecanumDriveMode +
                "\ndPadRight: enable mecanum strafe is "+ mecanumDriveMode +
                "\nX: brake motors is "+ !coastMotors +"\nY: coast motors is "+ coastMotors);

        telemetry.addData("LeftMTR  PWR: ", motorLeft.getPower());
        telemetry.addData("RightMTR PWR: ", motorRight.getPower());
//gamepad1		  -specifying the section of code giving control to gamepad1-this reduces confusion
        //else if's are required, so that a motor doesn't receive the power of multiple lines


        //allows for 3-speed joystick control


        if (abs(gamepad1.left_stick_x) > 0.15 || abs(gamepad1.right_stick_x) > 0.15) {
            //removes negatives from joystick values, to set variable to +/- for determing stick farther from zero
            dominantXJoystick = (abs(gamepad1.left_stick_x) - abs(gamepad1.right_stick_x));
            mecanumDriveMode = true;
        } else {
            mecanumDriveMode = false;
        }

        if (mecanumDriveMode) {     //when enabled, moors will only hit 100% when strafing and driving

            if (dominantXJoystick > 0) {
                mecanumStrafe = gamepad1.left_stick_x;
            } else if (dominantXJoystick < 0) {
                mecanumStrafe = gamepad1.right_stick_x;
            }

            if (gamepad1.left_bumper) {
                motorLeft.setPower((gamepad1.left_stick_y + -mecanumStrafe) / 2);
                motorLeft2.setPower((gamepad1.left_stick_y + mecanumStrafe) / 2);
                motorRight.setPower((gamepad1.right_stick_y + mecanumStrafe) / 2);
                motorRight2.setPower((gamepad1.right_stick_y + -mecanumStrafe) / 2);
            } else if (gamepad1.right_bumper) {
                motorLeft.setPower((gamepad1.left_stick_y + -mecanumStrafe) / 2 * 0.5);
                motorLeft2.setPower((gamepad1.left_stick_y + mecanumStrafe) / 2 * 0.5);
                motorRight.setPower((gamepad1.right_stick_y + mecanumStrafe) / 2 * 0.5);
                motorRight2.setPower((gamepad1.right_stick_y + -mecanumStrafe) / 2 * 0.5);
            } else {
                motorLeft.setPower((gamepad1.left_stick_y + -mecanumStrafe) / 2 * 0.75);
                motorLeft2.setPower((gamepad1.left_stick_y + mecanumStrafe) / 2 * 0.75);
                motorRight.setPower((gamepad1.right_stick_y + mecanumStrafe) / 2 * 0.75);
                motorRight2.setPower((gamepad1.right_stick_y + -mecanumStrafe) / 2 * 0.75);
            }
        } else if (!mecanumDriveMode) {
            if (gamepad1.left_bumper) {
                drive(gamepad1.left_stick_y * 0.8, gamepad1.right_stick_y * 0.8 );
            } else if (gamepad1.right_bumper) {
                drive(gamepad1.left_stick_y * 0.25, gamepad1.right_stick_y * 0.25);
            } else {
                drive(gamepad1.left_stick_y * 0.5, gamepad1.right_stick_y * 0.5);
            }
        }

        //button code to manipulate other code/robot
        if (gamepad1.x) {
            motorLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motorLeft2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motorRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motorRight2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            coastMotors = false;
        } else if (gamepad1.y) {
            motorLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            motorLeft2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            motorRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            motorRight2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            coastMotors = true;
        } else if (gamepad1.dpad_left) {
            mecanumDriveMode = false;
        } else if (gamepad1.dpad_right) {
            mecanumDriveMode = true;
        }

//gamepad2
        if (gamepad2.dpad_up) {
            motorLift.setPower(1);
        } else if (gamepad2.dpad_down) {
            motorLift.setPower(-1);
        } else {
            motorLift.setPower(0);
        }

        if (gamepad2.dpad_left) {
            servoMarkerLimited.setPosition(0.47);
        } else if (gamepad2.dpad_right) {
            servoMarkerLimited.setPosition(0.8);
        }
        //servoMarkerCR.setPower(gamepad2.right_stick_y);


//end of loop opmode programing
    }


    @Override
    public void stop() {
        telemetry.clearAll();
        telemetry.addLine("Stopped");
    }

    public void drive(double left, double right) {
        motorLeft.setPower(left);
        motorLeft2.setPower(left);
        motorRight.setPower(right);
        motorRight2.setPower(right);

    }

}
