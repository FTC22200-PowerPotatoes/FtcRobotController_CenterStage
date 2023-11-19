package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;

@TeleOp(group = "DriveMode")
public class DriveMode extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {

        // Motor config
        DcMotor frontLeft = hardwareMap.dcMotor.get("frontLeft");
        DcMotor backLeft = hardwareMap.dcMotor.get("backLeft");
        DcMotor frontRight = hardwareMap.dcMotor.get("frontRight");
        DcMotor backRight = hardwareMap.dcMotor.get("backRight");
        DcMotor linearSlide1 = hardwareMap.dcMotor.get("linearSlide1");
        DcMotor linearSlide2 = hardwareMap.dcMotor.get("linearSlide2");
        DcMotor intakeMotor = hardwareMap.dcMotor.get("intakeMotor");


        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();
        if (isStopRequested()) return;

        while (opModeIsActive()) {
            double y = -gamepad1.left_stick_y; // For forwards/backwards movement
            double x = gamepad1.left_stick_x * 1.1; // The 1.1 multiplier is to counteract imperfect strafing
            double rx = gamepad1.right_stick_x; // Turning left/right




            double fL_Motor = Range.clip((y+x+rx), -0.99, 0.99); // fL = FrontLeft
            double bL_Motor = Range.clip((y-x+rx), -0.99, 0.99); // bL = BackLeft
            double fR_Motor = Range.clip((y-x-rx), -0.99, 0.99); // fR = FrontRight
            double bR_Motor = Range.clip((y+x-rx), -0.99, 0.99); // bR = backRight

            /*
             - While holding the right bumper on gamepad 1, the robot goes in a 'slow mode'
             - This can be useful in multiple scenarios, ex: attempting to place a game piece, aligning for supesending on the rigging, etc.
             - Values can be adjusted based on feedback from drivers
            */

            if (gamepad1.right_bumper) {
                fL_Motor = Range.clip((y+x+rx), -0.3, 0.3);// Using the clip feature in order to activate 'slow mode'
                bL_Motor = Range.clip((y-x+rx), -0.3, 0.3);// Using the clip feature in order to activate 'slow mode'
                fR_Motor = Range.clip((y-x-rx), -0.3,0.3); // Using the clip feature in order to activate 'slow mode'
                bR_Motor = Range.clip((y+x-rx), -0.3,0.3); // Using the clip feature in order to activate 'slow mode'

            }



            frontLeft.setPower(fL_Motor);
            backLeft.setPower(bL_Motor);
            frontRight.setPower(fR_Motor);
            backRight.setPower(bR_Motor);


            //todo: Add modular code


            if (gamepad2.left_trigger > 0.0) {
                linearSlide1.setPower(-gamepad2.left_trigger);
                linearSlide2.setPower(gamepad2.left_trigger);
            }
            if (gamepad2.right_trigger > 0.0) {
                linearSlide1.setPower(gamepad2.right_trigger);
                linearSlide2.setPower(-gamepad2.right_trigger);
            }
            else {
                linearSlide1.setPower(0.0);
                linearSlide2.setPower(0.0);
            }
            linearSlide1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            linearSlide2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

            while (gamepad2.a) {
                intakeMotor.setPower(1.0);
            }
            if (gamepad2.b) {
                intakeMotor.setPower(0.0);
            }


        }
    }
}