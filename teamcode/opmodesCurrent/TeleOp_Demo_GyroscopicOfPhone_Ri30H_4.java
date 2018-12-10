/* Copyright (c) 2014 Qualcomm Technologies Inc

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Qualcomm Technologies Inc nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */


//The package and import lines load variable types and libraries, to connect the robot with the code
//the variable type allows for coding that variable in certain ways, along with using its library

//The opmode lines are importing different functions that sync with the phone
package org.firstinspires.ftc.teamcode.opmodesCurrent;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import android.hardware.*;
import android.os.Bundle;


/**
 * All the code aside from importing, goes within a class file - essentially telling Android Studio-
 * to run this using java
 */

//@TeleOp(name = "TeleOp_Gyroscopic_Of_Phone")


public class TeleOp_Demo_GyroscopicOfPhone_Ri30H_4 extends OpMode {


//*********** Experimental
//*********** Rev Hub count: 2


    //TETRIX Motors		-recording the motor type as if this file ran autonomous

    private DcMotor motorLeft, motorLeft2,
            motorRight, motorRight2,
            motorLift,
            motorJewel;

    private Servo servoLiftLeft, servoLiftRight,  //  servoJewel,
            servoJewel;

    //private byte robotDriveMode = 1,   //when true, robot moves with 1 joystick, when false-
    //both sticks are used.
    //gamepad_2_DriveMode = 0;

    private String gamepad_1_DriveMode = "";


    private SensorManager sensorManager;
    private Sensor sensorGyroscope;
    private SensorEventListener eventListenerGyroscope;




    /*
     * Code to run when the op mode is first enabled goes here
     * This is all automatic- it prepares the robot to run loop() without error
     */
    @Override
    public void init() {
    //public void init(Bundle SavedInstanceState) {

        //super.(savedInstanceState);
        //setContentView(R.layout.activity_main);
        //sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

//rev hub 1
        motorLeft = hardwareMap.dcMotor.get("motor_1");
        motorRight = hardwareMap.dcMotor.get("motor_2");
        motorLeft2 = hardwareMap.dcMotor.get("motor_3");
        motorRight2 = hardwareMap.dcMotor.get("motor_4");

        servoJewel = hardwareMap.servo.get("servo_4");
        servoLiftLeft = hardwareMap.servo.get("servo_2"); //stage left, side towards phone and wall
        servoLiftRight = hardwareMap.servo.get("servo_3");
//rev hub 2
        motorLift = hardwareMap.dcMotor.get("motor_5");
        motorJewel = hardwareMap.dcMotor.get("motor_6");


        //so you don't have to wire red to black, to maintain program logic
        motorLeft.setDirection(DcMotor.Direction.REVERSE);
        motorLeft2.setDirection(DcMotor.Direction.REVERSE);
        motorLift.setDirection(DcMotor.Direction.REVERSE);

        motorJewel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        //glyphServos((byte) 2);

        //telemetry sends data to print onto both phones
        telemetry.addLine("Drive Base TeleOp\nInit Opmode");
    }


    /**
     * The next 2 lines ends the current state of Opmode, and starts Opmode.loop()
     * This enables control of the robot via the gamepad or starts autonomous functions
     */
    @Override
    public void loop() {

        telemetry.addLine("Loop OpMode");
        telemetry.addData("DriveModes:_GP1: ", gamepad_1_DriveMode);
        telemetry.addData("LeftMTR  PWR: ", motorLeft.getPower());
        telemetry.addData("RightMTR PWR: ", motorRight.getPower());
        telemetry.addData("MTR Lift POS: ", motorLift.getTargetPosition());
        telemetry.addData("Servo Jewel:  ", servoJewel.getPosition());
//gamepad1		  -specifying the section of code giving control to gamepad1-this reduces confusion
        //else if's are required, so that a motor doesn't recieve the power of multiple lines


        //allows for 3-speed joystick control


        if (gamepad1.left_bumper) {
            drive(gamepad1.left_stick_y, gamepad1.right_stick_y);
        } else if (gamepad1.right_bumper) {
            drive(gamepad1.left_stick_y * 0.5, gamepad1.right_stick_y * 0.5);
        } else {
            drive(gamepad1.left_stick_y * 0.75, gamepad1.right_stick_y * 0.75);
        }


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

        if (gamepad2.dpad_left) {
            motorJewel.setPower(gamepad2.right_stick_y / 5);

            if (gamepad2.a) {
                servoJewel.setPosition(0.25);
            } else if (gamepad2.b) {
                servoJewel.setPosition(0.5);
            } else if (gamepad2.x) {
                servoJewel.setPosition(0.75);
            }
        } else {


            if (gamepad2.x) {
                motorJewel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                motorLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            } else if (gamepad2.y) {
                motorJewel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                motorLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            }


        }

        motorLift.setPower(gamepad2.left_stick_y);

        if (gamepad2.right_stick_button) {      //fully closed
            glyphServos((byte) 0);
        } else if (gamepad2.left_bumper) {     //grab
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

    public void drive(double left, double right) {
        motorLeft.setPower(left);
        motorLeft2.setPower(left);
        motorRight.setPower(right);
        motorRight2.setPower(right);

    }

    public void glyphServos(byte x) { //Futaba
        if (x == 1) {                        //servos init/fully closed (Actually fully open)
            servoLiftLeft.setPosition(0.35);  //0.375
            servoLiftRight.setPosition(0.5);    //0.6
        } else if (x == 2) {                //glyph grab (it is actually grab)
            servoLiftLeft.setPosition(0.55);        //0.55
            servoLiftRight.setPosition(0.325);            //0.325
        } else if (x == 0) {                //glyph release
            //servoLiftLeft.setPosition(0.275);   //0.375
            //servoLiftRight.setPosition(0.55);   //0.45, 0.6

            servoLiftLeft.setPosition(0.23);   //0.115
            servoLiftRight.setPosition(0.64);   //0.75
        }

        /*  Hitech
        if (x == 0) {						//servos init/fully closed
            servoLiftLeft.setPosition(0.115);  //(0.115, 0.75)
            servoLiftRight.setPosition(0.75);
        } else if (x == 1) {				//glyph release
            servoLiftLeft.setPosition(0.55);		//(0.55, 0.30)
            servoLiftRight.setPosition(0.325);		    //opposite and subtract 10, or diff:15
        } else if (x == 2) {				//glyph grab
            servoLiftLeft.setPosition(0.375);    //(0.35, 0.5)
            servoLiftRight.setPosition(0.45);
        }
         */

    }

}
