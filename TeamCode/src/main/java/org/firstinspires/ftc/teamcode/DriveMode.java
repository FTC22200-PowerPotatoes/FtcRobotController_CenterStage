package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;

import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@TeleOp(group = "DriveMode")
public class DriveMode extends LinearOpMode {

    //Declare variables
    double gripPosition, contPower;
    Servo gripServo;

    final double rev = 1200; //one revolution = 5.11inch
    double MIN_POSITION = 0.1, MAX_POSITION = 0.6;
    double airPos;
    boolean isGripperOpen = true;

    boolean isAirplaneOn = true;

    DistanceSensor lDistSensor;
    DistanceSensor rDistSensor;



    public void runOpMode() throws InterruptedException {

        // Motor config
        DcMotor frontLeft = hardwareMap.dcMotor.get("frontLeft");
        DcMotor backLeft = hardwareMap.dcMotor.get("backLeft");
        DcMotor frontRight = hardwareMap.dcMotor.get("frontRight");
        DcMotor backRight = hardwareMap.dcMotor.get("backRight");
        DcMotor linearSlide1 = hardwareMap.dcMotor.get("linearSlide1");
        DcMotor intakeMotor = hardwareMap.dcMotor.get("intakeMotor");
        DcMotor hangMec = hardwareMap.dcMotor.get("hang");

        //Servo Config
        CRServo gripServo = hardwareMap.get(CRServo.class, "airplane");
        Servo output = hardwareMap.servo.get("outTake");

        //SensorConfig
        lDistSensor = hardwareMap.get(DistanceSensor.class, "lDistSensor");
        rDistSensor = hardwareMap.get(DistanceSensor.class, "rDistSensor");

        //SetMotor Direction
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

            if (gamepad2.a) {
                intakeMotor.setPower(0.55);
            } else if (gamepad2.b) {
                intakeMotor.setPower(-0.8);
            } else {
                intakeMotor.setPower(0.0);
            }

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

            //Stop Motor if BackButtonPressed

            // todo: check touch sensor pressed stop robot



            //Drone Launcher
            //Release drone
            if (gamepad1.y) {
               gripServo.setPower(1);
            }
            else {
                gripServo.setPower(0.0);
            }

            //Sets the angle of the drone launcher at 67degrees
            if (gamepad1.dpad_up) {
                // Calculate the target encoder position based on the desired angle
                int targetPosition = (int) (rev);

                // Set the target position for the motor
                hangMec.setTargetPosition(targetPosition);

                // Set the motor to run to the target position
                hangMec.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                // Start the motor
                hangMec.setPower(1.0);

                // Wait until the motor reaches the target position
                while (hangMec.isBusy()) {}

                // Stop the motor after reaching the target position
                hangMec.setPower(0.0);
                hangMec.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            }

            // Update the gripper position
            output.setPosition(Range.clip(gripPosition, MAX_POSITION, MIN_POSITION));

            // Delay to avoid loop spamming
            sleep(20);

            // Telemetry for front distance sensor
            // Telemetry for left distance sensor
            telemetry.addData("Left Distance (cm)", String.format("%.01f cm", lDistSensor.getDistance(DistanceUnit.CM)));

            // Telemetry for right distance sensor
            telemetry.addData("Right Distance (cm)", String.format("%.01f cm", rDistSensor.getDistance(DistanceUnit.CM)));

            telemetry.update();


        }
    }
}