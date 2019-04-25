package org.firstinspires.ftc.teamcode.opmodesNotebook;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;


//this tells the compiler (a part of android studio that will download code files onto the phone)
//to package this file as an active/runnable opmode under the run-name of TeleOp_4.
@TeleOp(name="TeleOp_4")
@Disabled

/**
 * All the code aside from importing, goes within a class file - essentially telling Android Studio-
 * where the code starts and ends within a file
 */
public class TeleOp_Ri30H_4_NOTEBOOK extends OpMode {



//*********** Experimental
//*********** Rev Hub count: 2


    //Code to run when the program is selected

    private DcMotor motorLeft,          motorLeft2,
                    motorRight,         motorRight2,
                    motorLift;

    private Servo servoLiftLeft, servoLiftRight;


    private String gamepad_1_DriveMode = "";


    /*
     * Code to run when the op mode is first enabled (initialized) goes here
     * This is all automatic- it prepares the robot to run loop() opmode without error
     */
    @Override
    public void init() {

//rev hub 1
        motorLeft   = hardwareMap.dcMotor.get("motor_1");
        motorRight  = hardwareMap.dcMotor.get("motor_2");
        motorLeft2  = hardwareMap.dcMotor.get("motor_3");
        motorRight2 = hardwareMap.dcMotor.get("motor_4");

        servoLiftLeft	= hardwareMap.servo.get("servo_2"); //side closest to phone
        servoLiftRight	= hardwareMap.servo.get("servo_3");
//rev hub 2
        motorLift	    = hardwareMap.dcMotor.get("motor_5");


        //so you don't have to wire red to black, and to maintain program logic
        motorLeft.setDirection(DcMotor.Direction.REVERSE);
        motorLeft2.setDirection(DcMotor.Direction.REVERSE);
        motorLift.setDirection(DcMotor.Direction.REVERSE);

        glyphServos((byte) 2);

        //telemetry sends data to print onto both phones
        telemetry.addLine("Drive Base TeleOp\nInit Opmode");
    }


    /**
     * The above closing bracket ended the init code. The below 2 lines start Opmode.loop()
     * This enables control of the robot via the gamepad or starts autonomous functions
     */
    @Override
    public void loop() {

        telemetry.addLine("Loop OpMode");
        telemetry.addData("GP1-DriveMode: ", gamepad_1_DriveMode);
        telemetry.addData("LeftMTR  PWR: ", motorLeft.getPower());
        telemetry.addData("RightMTR PWR: ", motorRight.getPower());
        telemetry.addData("MTR Lift POS: ", motorLift.getTargetPosition());

//gamepad1		  -specifying the section of code giving control to gamepad1-this reduces confusion

        //allows for 3-speed joystick control


        //else if's are required, so that a motor doesn't receive the power of multiple lines
        //this is also the reason for variable usage

            gamepad_1_DriveMode = "A";
            if (gamepad1.left_bumper) {         //left bumper runs drive motors at 100% of joystick
                drive(gamepad1.left_stick_y, gamepad1.right_stick_y);
            } else if (gamepad1.right_bumper) { //right runs at 20% of joystick input speed
                drive(gamepad1.left_stick_y * 0.2, gamepad1.right_stick_y * 0.2);
            } else {                            //the default (no bumper value) running at 50% power
                drive(gamepad1.left_stick_y * 0.5, gamepad1.right_stick_y * 0.5);
            }
        //doesn't offer much change, mostly up for driver preference
        //this sets whether to brake or coast motors upon their power loss.
        if (gamepad1.x) {
            motorLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motorLeft2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motorRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motorRight2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        } else if (gamepad1.y) {
            motorLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            motorLeft2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            motorRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            motorRight2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        }

//gamepad2

        if (gamepad2.x) {
            motorLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        } else if (gamepad2.y) {
            motorLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        }

        //no modifications to drive speed, runing at typical 1:1 joystick to motor power values
        motorLift.setPower(gamepad2.left_stick_y);

        //servo programming
        if (gamepad2.left_bumper) {             //grab
            glyphServos((byte) 1);
        } else if (gamepad2.right_bumper) {    //open
            glyphServos((byte) 2);
        }

//end of loop opmode programing
    }

    @Override
    public void stop() {
        telemetry.clearAll();
        telemetry.addLine("Stopped");
    }

    public void drive(double left,double right) {
        motorLeft.setPower(left);
        motorLeft2.setPower(left);
        motorRight.setPower(right);
        motorRight2.setPower(right);

    }

    public void glyphServos(byte x) {
        //Futaba servos
        if (x == 2) {				//glyph release
            servoLiftLeft.setPosition(0.55);
            servoLiftRight.setPosition(0.325);
        } else if (x == 1) {				//glyph grab
            servoLiftLeft.setPosition(0.375);
            servoLiftRight.setPosition(0.45);
        }
    }

}
