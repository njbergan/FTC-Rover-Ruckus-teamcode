package org.firstinspires.ftc.teamcode.opmodesCurrent;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaRoverRuckus;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TfodRoverRuckus;

import java.util.List;

//@TeleOp(name = "Camera Test")

public class TFec_4_autoCopy_beforeWaitForStart extends LinearOpMode {


    private int timeOutCount = 0, goldSample; //1: left, 2: center, 3: right, -1: unknown

    //encoder vars      formula:
    //(COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.1415)in terms of Math.PI
    //hardware vars



    //it takes 3 shaft rotations to move the actuator 1 inch this case uses a 1:4/3 gear dilation
    //gear ratio * cycles per rotation (28)


    //int relativeLayoutId;
    //View relativeLayout;
    //private CRServo servoCrFlapper;
    private double gameTimeSnapShot = 0;
    private int debugSteps = 0;

    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    //private static final String LABEL_SILVER_MINERAL = "Silver Mineral";

    private VuforiaRoverRuckus vuforiaRoverRuckus;
    private TfodRoverRuckus tfodRoverRuckus;

    @Override
    public void runOpMode() {
        telemetry.addLine("Wait for Camera to Init");
        telemetry.update();

        telemetry.addData("Testing", ++debugSteps);


        //List recognitions;
        double goldMineralX = -1, silverMineral1X = -1, silverMineral2X = -1;

        vuforiaRoverRuckus = new VuforiaRoverRuckus();
        tfodRoverRuckus = new TfodRoverRuckus();
        //boolean markerIsGold;

        // Put initialization blocks here.
        vuforiaRoverRuckus.initialize("", hardwareMap.get(WebcamName.class, "Webcam1"), "teamwebcamcalibrationLive.xml",
                false, false, VuforiaLocalizer.Parameters.CameraMonitorFeedback.AXES,
                0, 0, 0, 0, 0, 0, true);
        tfodRoverRuckus.initialize(vuforiaRoverRuckus, 0.4f, true, true);

        //while (!opModeIsActive() && !isStopRequested()) {
            telemetry.addData(">", "Press Play to start");
        //    telemetry.addData("Time: ", (int) this.time);
            telemetry.update();
        //}
        //waitForStart();
        telemetry.addData("Current Time", this.time);
        //telemetry.addData("Testing", debugSteps == 1);
        if (!opModeIsActive()) {
            //telemetry.addData("Testing", debugSteps == 2);
            tfodRoverRuckus.activate();//start a camera related process
            // Put run blocks here.
            gameTimeSnapShot = this.time;
            //while (!opModeIsActive() && this.time <= (5 + gameTimeSnapShot)) {//1 second,0.5, 1.5
            while (!opModeIsActive()) {
                //telemetry.addData("Testing", debugSteps == 2);
                if (tfodRoverRuckus != null) {
                    // getUpdatedRecognitions() will return null if no new information is available since
                    // the last time that call was made.
                    List<Recognition> updatedRecognitions = tfodRoverRuckus.getUpdatedRecognitions();
                    if (updatedRecognitions != null) {
                        telemetry.addData("# Object Detected", updatedRecognitions.size());

                        if (updatedRecognitions.size() == 2) {  //when webcam see's 2 minerals:

                            goldMineralX = -1;
                            silverMineral1X = -1;
                            silverMineral2X = -1;
                            telemetry.update();

                            for (Recognition recognition : updatedRecognitions) {   //if it detects gold, set it's X, if it detects silver for the first time, assign it to OneX
                                if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                                    goldMineralX = recognition.getLeft();
                                } else if (silverMineral1X == -1) {
                                    silverMineral1X = recognition.getLeft();
                                } else if (goldMineralX == -1) {
                                    silverMineral2X = recognition.getLeft();

                                }
                            }
                            if ((goldMineralX != -1 && silverMineral1X != -1) || (silverMineral1X != -1 && silverMineral2X != -1)) {   //if gold and silver, or silver and silver, are found
                                if (goldMineralX < silverMineral1X && goldMineralX != -1) {
                                    telemetry.addData("Gold Mineral Position", "Center G:" + goldMineralX + " S1:" + silverMineral1X + " S2:" + silverMineral2X);
                                    goldSample = 2;
                                } else if (goldMineralX > silverMineral1X) {
                                    telemetry.addData("Gold Mineral Position", "Left G:" + goldMineralX + " S1:" + silverMineral1X + " S2:" + silverMineral2X);
                                    goldSample = 1;
                                } else {
                                    telemetry.addData("Gold Mineral Position", "Right G:" + goldMineralX + " S1:" + silverMineral1X + " S2:" + silverMineral2X);
                                    goldSample = 3;
                                }
                            }
                            sleep(100);
                        }
                        telemetry.update();
                    }
                }
            }
            waitForStart();
            tfodRoverRuckus.deactivate();
            vuforiaRoverRuckus.close();
            tfodRoverRuckus.close();
        }


        //init

        gameTimeSnapShot = this.time;


        sleep(6000);
        //last line of autonomous logical code

    }


}
