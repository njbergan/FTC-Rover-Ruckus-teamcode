package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.List;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaRoverRuckus;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TfodRoverRuckus;

//@TeleOp(name = "CameraTest")  //blocks to java
public class TFexternalCam2 extends LinearOpMode {
    //private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";

    private VuforiaRoverRuckus vuforiaRoverRuckus;
    private TfodRoverRuckus tfodRoverRuckus;

    /**
     * This function is executed when this Op Mode is selected from the Driver Station.
     */
    @Override
    public void runOpMode() {
        List recognitions;
        double goldMineralX;
        double goldMineralY;
        //double silverMineral2X;

        vuforiaRoverRuckus = new VuforiaRoverRuckus();
        tfodRoverRuckus = new TfodRoverRuckus();
        boolean markerIsGold;

        // Put initialization blocks here.
        vuforiaRoverRuckus.initialize("", hardwareMap.get(WebcamName.class, "Webcam1"), "teamwebcamcalibrationLive.xml",
                false, false, VuforiaLocalizer.Parameters.CameraMonitorFeedback.AXES,
                0, 0, 0, 0, 0, 0, true);
        tfodRoverRuckus.initialize(vuforiaRoverRuckus, 0.4f, true, true);
        telemetry.addData(">", "Press Play to start");
        telemetry.update();
        waitForStart();
        if (opModeIsActive()) {
            tfodRoverRuckus.activate();
            // Put run blocks here.
            while (opModeIsActive()) {
                if (tfodRoverRuckus != null) {
                    // getUpdatedRecognitions() will return null if no new information is available since
                    // the last time that call was made.
                    List<Recognition> updatedRecognitions = tfodRoverRuckus.getUpdatedRecognitions();
                    if (updatedRecognitions != null) {
                        telemetry.addData("# Object Detected", updatedRecognitions.size());
                        if (updatedRecognitions.size() == 1) {
                                    goldMineralX = -1;
                                    goldMineralY = -1;
                            for (Recognition recognition : updatedRecognitions) {
                                if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                                    goldMineralX = (int) recognition.getLeft();
                                    markerIsGold = true;
                                } else if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                                    markerIsGold = false;
                                }
                                if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                                    goldMineralY = (int) recognition.getBottom();
                                    markerIsGold = true;
                                } else if (recognition.getLabel().equals(LABEL_SILVER_MINERAL)) {
                                    markerIsGold = false;       //telemetry with this variable, then use for auto
                                }
                            }
                            telemetry.addData("Xdetection", "done, g:"+goldMineralX);
                            telemetry.addData("Ydetection", "done, g:"+goldMineralY);

                        }
                        telemetry.update();
                    }
                }
            }
            tfodRoverRuckus.deactivate();
        }

        vuforiaRoverRuckus.close();
        tfodRoverRuckus.close();
    }


}
