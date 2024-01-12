package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(group = "DriveMode")
public class DriveMode extends LinearOpMode {

    double gripPosition, contPower;
    double MIN_POSITION = 0.1, MAX_POSITION = 0.6;
    boolean isGripperOpen = true;


    public void runOpMode() throws InterruptedException {

        // Motor config
        DcMotor frontLeft = hardwareMap.dcMotor.get("frontLeft");
        DcMotor backLeft = hardwareMap.dcMotor.get("backLeft");
        DcMotor frontRight = hardwareMap.dcMotor.get("frontRight");
        DcMotor backRight = hardwareMap.dcMotor.get("backRight");
        DcMotor linearSlide1 = hardwareMap.dcMotor.get("linearSlide1");
        DcMotor intakeMotor = hardwareMap.dcMotor.get("intakeMotor");
        Servo output = hardwareMap.servo.get("outTake");
        DcMotor hangMec = hardwareMap.dcMotor.get("hang");
        Servo airplane = hardwareMap.servo.get("airplane");
        /*
        TouchSensor touch1;
        TouchSensor touch2;

        touch1 = hardwareMap.get(TouchSensor.class, "touch1");
        touch2 = hardwareMap.get(TouchSensor.class, "touch2");

        */


        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        linearSlide1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        waitForStart();
        if (isStopRequested()) return;

        while (opModeIsActive()) {
            double y = -gamepad1.left_stick_y; // For forwards/backwards movement
            double x = gamepad1.left_stick_x * 1.1; // The 1.1 multiplier is to counteract imperfect strafing
            double rx = gamepad1.right_stick_x; // Turning left/right

            double fL_Motor = Range.clip((y + x + rx), -0.89, 0.89); // fL = FrontLeft
            double bL_Motor = Range.clip((y - x + rx), -0.89, 0.89); // bL = BackLeft
            double fR_Motor = Range.clip((y - x - rx), -0.89, 0.89); // fR = FrontRight
            double bR_Motor = Range.clip((y + x - rx), -0.89, 0.89); // bR = backRight

            /*
             - While holding the right bumper on gamepad 1, the robot goes in a 'slow mode'
             - This can be useful in multiple scenarios, ex: attempting to place a game piece, aligning for supesending on the rigging, etc.
             - Values can be adjusted based on feedback from drivers
            */

            if (gamepad1.left_trigger > 0.0) {
                fL_Motor = Range.clip((y + x + rx), -0.3, 0.3); // Using the clip feature in order to activate 'slow mode'
                bL_Motor = Range.clip((y - x + rx), -0.3, 0.3); // Using the clip feature in order to activate 'slow mode'
                fR_Motor = Range.clip((y - x - rx), -0.3, 0.3); // Using the clip feature in order to activate 'slow mode'
                bR_Motor = Range.clip((y + x - rx), -0.3, 0.3); // Using the clip feature in order to activate 'slow mode'
            }

            frontLeft.setPower(fL_Motor);
            backLeft.setPower(bL_Motor);
            frontRight.setPower(fR_Motor);
            backRight.setPower(bR_Motor);

            //todo: Add modular code
            if (gamepad1.right_bumper) {
                hangMec.setPower(1.0);
            }
            else if (gamepad1.left_bumper) {
                hangMec.setPower(-1.0);
            }
            else {
                hangMec.setPower(0.0);
            }
            if (gamepad2.left_trigger > 0.0) {
                linearSlide1.setPower(-gamepad2.left_trigger);
            } else if (gamepad2.right_trigger > 0.0) {
                linearSlide1.setPower(gamepad2.right_trigger);
            } else {
                linearSlide1.setPower(0.0);
            }
            if (gamepad2.x) {
                airplane.setPosition(0.0);
            }
            if (gamepad2.a) {
                intakeMotor.setPower(0.55);
            } else if (gamepad2.b) {
                intakeMotor.setPower(-0.8);
            } else {
                intakeMotor.setPower(0.0);
            }
            /*
            if (touch1.isPressed() && touch2.isPressed()) {
                brake(0.0);
            }
            */

            if (gamepad2.y) {
                // Toggle the gripper state only once per button press
                if (!isGripperOpen) {
                    gripPosition = MAX_POSITION;
                    isGripperOpen = true;
                } else {
                    gripPosition = MIN_POSITION;
                    isGripperOpen = false;
                }

                // Update the gripper position
                output.setPosition(gripPosition);

                // Wait a short time to avoid rapid toggling
                sleep(200);
            }

            // Update the gripper position continuously while the button is held
            if (gamepad2.left_bumper && gripPosition < MAX_POSITION) {
                gripPosition += 0.07;
            } else if (gamepad2.right_bumper && gripPosition > MIN_POSITION) {
                gripPosition -= 0.07;
            }

            // Update the gripper position
            output.setPosition(Range.clip(gripPosition, MAX_POSITION, MIN_POSITION));

            // Delay to avoid loop spamming
            sleep(20);


        }
    }
}
