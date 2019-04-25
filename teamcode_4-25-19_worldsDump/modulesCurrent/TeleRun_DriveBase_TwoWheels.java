package org.firstinspires.ftc.teamcode.modulesCurrent;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

//@TeleOp(name = "TeleRunDriveBaseTwoWheels")

public class TeleRun_DriveBase_TwoWheels {

    public static void main() {
        Module_DriveBase_TwoWheels robot = new Module_DriveBase_TwoWheels();
        robot.init();
        robot.loop();
    }

}
