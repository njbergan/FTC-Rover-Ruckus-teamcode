
package org.firstinspires.ftc.teamcode.opmodesCurrent;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

//@TeleOp(name = "TeleOp_Sparkie_1a")


public class TeleOp_sparkie_1 extends OpMode {

    private DcMotor motorLeft,     motorLeft2,
                    motorRight,    motorRight2,
                    motorShoulder, motorElbow, motorFlapper, motorLift;

    @Override
    public void init() {

//rev hub 1
        motorLeft   = hardwareMap.dcMotor.get("motor_1");
        motorRight  = hardwareMap.dcMotor.get("motor_2");
        motorLeft2  = hardwareMap.dcMotor.get("motor_3");
        motorRight2 = hardwareMap.dcMotor.get("motor_4");

        motorShoulder   = hardwareMap.dcMotor.get("motor_5");
        motorElbow      = hardwareMap.dcMotor.get("motor_6");
        motorFlapper    = hardwareMap.dcMotor.get("motor_7");
        motorLift       = hardwareMap.dcMotor.get("motor_8");


        //so you don't have to wire red to black, to maintain program logic
        motorRight.setDirection(DcMotor.Direction.REVERSE);
        motorRight2.setDirection(DcMotor.Direction.REVERSE);
        motorShoulder.setDirection(DcMotor.Direction.REVERSE);


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
        telemetry.addData("LeftMTR  PWR: ", motorLeft.getPower());
        telemetry.addData("RightMTR PWR: ", motorRight.getPower());
//gamepad1		  -specifying the section of code giving control to gamepad1-this reduces confusion
        //else if's are required, so that a motor doesn't recieve the power of multiple lines


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

        motorShoulder.setPower(gamepad2.left_stick_y);
        motorElbow.setPower(gamepad2.right_stick_y * 0.7);

        if (gamepad2.left_bumper) {
            motorFlapper.setPower(1);
        } else if (gamepad2.right_bumper) {
            motorFlapper.setPower(-1);
        } else {
            motorFlapper.setPower(0);
        }

        if (gamepad2.dpad_up) {
            motorLift.setPower(1);
        } else if (gamepad2.dpad_down) {
            motorLift.setPower(-1);
        } else {
            motorLift.setPower(0);
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
}
