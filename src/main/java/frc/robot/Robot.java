package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;


public class Robot extends TimedRobot {
  private final CANSparkMax mLeftDriveMotor1 = new CANSparkMax(1, MotorType.kBrushless);
  private final CANSparkMax mLeftDriveMotor2 = new CANSparkMax(2, MotorType.kBrushless);
  private final CANSparkMax mRightDriveMotor1 = new CANSparkMax(3, MotorType.kBrushless);
  private final CANSparkMax mRightDriveMotor2 = new CANSparkMax(4, MotorType.kBrushless);
  private final CANSparkMax[] mDriveMotors = {mLeftDriveMotor1, mLeftDriveMotor2, mRightDriveMotor1, mRightDriveMotor2};
  
  private final DifferentialDrive mRobotDrive = new DifferentialDrive(mRightDriveMotor1, mRightDriveMotor2);
  private final Joystick mStick = new Joystick(0);

  @Override
  public void robotInit() {
    mRightDriveMotor1.setInverted(true); // right side is reverse of left
    mRightDriveMotor2.setInverted(true);
    for (int i=0; i<4; i++) {
      // mDriveMotors[i].restoreFactoryDefaults();
      mDriveMotors[i].setSmartCurrentLimit(40);
      mDriveMotors[i].setIdleMode(CANSparkMax.IdleMode.kCoast);
      // mDriveMotors[i].burnFlash();
    }
    mLeftDriveMotor1.follow(mLeftDriveMotor2);
    mRightDriveMotor1.follow(mRightDriveMotor2);
  }

  @Override
  public void teleopPeriodic() {
    mRobotDrive.arcadeDrive(-mStick.getY(), mStick.getX());
  }
}
