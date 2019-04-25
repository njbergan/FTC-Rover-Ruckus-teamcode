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


@Autonomous(name="Auto 3-2 Red Audience")
@Disabled
public class Auto_RedAud_3_2 extends LinearOpMode {

    public static final String TAG = "Vuforia VuMark Sample";


    private Auto_Class_3_2 robot = new Auto_Class_3_2();    //create an instance of the robot class program

    @Override public void runOpMode() {

        robot.Init();

        waitForStart();                         //transition from init to start

        resetStartTime();

        //telemetry sends data to print onto Driver Station phone
        telemetry.addLine("Drive Base TeleOp\nInit Opmode");


        while (!opModeIsActive()) {
            this.time = 0;
            resetStartTime();
            robot.glyphServos((byte) 2);
        }


        robot.cameraSense(44.5, 28.5, 38);


    //logical robot movements based on encoders

            while (opModeIsActive() && this.time <= 29.5) {                  //limits code to 30 seconds

                //autonomous encoder-based code starts after this line


                robot.motorSetModes(DcMotor.RunMode.STOP_AND_RESET_ENCODER);  //stop and reset encoders
                robot.motorSetModes(DcMotor.RunMode.RUN_USING_ENCODER);       //start encoders

                // Send telemetry message to signify robot waiting;
                telemetry.addData("Status", "Resetting Encoders");
                telemetry.update();

                // Send telemetry message to indicate successful Encoder reset
                telemetry.addData("Path0", "Starting at %7d :%7d",
                        robot.motorLeft.getCurrentPosition(),
                        robot.motorRight.getCurrentPosition());
                telemetry.update();

                // Step through each leg of the path,
                // Note: Reverse movement is obtained by setting a negative distance (not speed)
                //speed, leftInches, rightInches, motorLiftInches

                telemetry.addData("VuMark", "%s visible", robot.vuMark);

                robot.glyphServos((byte) 2);

                robot.encoderDrive(1, 0, 0, 5);    //lift glyph by 5 inches at 20% pwr

                robot.encoderDrive(0.4, robot.cryptoboxKeySpecificDrive,  robot.cryptoboxKeySpecificDrive, 0); //drive preset values at 25% pwr

                robot.encoderDrive(0.4, 15.3, -15.3, 0); //turn right for 16" at 25% | turn towards cryptobox
                robot.encoderDrive(0.6,  8,   8, 0); //drive towards cryptobox

                robot.glyphServos((byte) 1);

                robot.encoderDrive(1, -10.5, -10.5, 0);   //drive backward facing cryptobox, towards center
                robot.encoderDrive(0.4, 29, -29, 0); //turn to face center
                robot.encoderDrive(0.6, -15, -15, 0);   //back into glyph

                robot.encoderDrive(1, 2, 2, 0);

                robot.encoderDrive(1, 0, 0, -5);    //lift glyph by 5 inches at 20% pwr
                break;
            }
            //relicTrackables.deactivate();
            robot.motorLeft.setPower(0);
            robot.motorRight.setPower(0);
            robot.motorLeft2.setPower(0);
            robot.motorRight2.setPower(0);
            robot.motorLift.setPower(0);
            telemetry.addLine("Program is done");

    }


    //end of main

}
