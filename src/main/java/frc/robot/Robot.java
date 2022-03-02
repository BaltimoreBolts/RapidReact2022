package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;


public class Robot extends TimedRobot {
  public CANSparkMax mLeftDriveMotor1;
  public CANSparkMax mRightDriveMotor1;
  public CANSparkMax mLeftDriveMotor2;
  public CANSparkMax mRightDriveMotor2;
  public DifferentialDrive mRobotDrive;
  public MotorControllerGroup mLeftMotors;
  public MotorControllerGroup mRightMotors;
  public RelativeEncoder mLeftEncoder;
  public RelativeEncoder mRightEncoder;

  public Joystick mStick;
  public double mSpeed = 0.0;
  public double mTwist = 0.0;
  
  @Override
  public void robotInit() {
    mLeftDriveMotor1 = new CANSparkMax(1, MotorType.kBrushless);
    mLeftDriveMotor2 = new CANSparkMax(2, MotorType.kBrushless);
    mRightDriveMotor1 = new CANSparkMax(3, MotorType.kBrushless);
    mRightDriveMotor2 = new CANSparkMax(4, MotorType.kBrushless);
    mLeftMotors = new MotorControllerGroup(mLeftDriveMotor1,mLeftDriveMotor2);
    mRightMotors = new MotorControllerGroup(mRightDriveMotor1, mRightDriveMotor2);

    mLeftDriveMotor1.restoreFactoryDefaults();
    mLeftDriveMotor2.restoreFactoryDefaults();
    mRightDriveMotor1.restoreFactoryDefaults();
    mRightDriveMotor2.restoreFactoryDefaults();
    mLeftDriveMotor1.setSmartCurrentLimit(40);
    mLeftDriveMotor2.setSmartCurrentLimit(40);
    mRightDriveMotor1.setSmartCurrentLimit(40);
    mRightDriveMotor2.setSmartCurrentLimit(40);
    mLeftDriveMotor1.setIdleMode(CANSparkMax.IdleMode.kCoast);
    mLeftDriveMotor2.setIdleMode(CANSparkMax.IdleMode.kCoast);
    mRightDriveMotor1.setIdleMode(CANSparkMax.IdleMode.kCoast);
    mRightDriveMotor2.setIdleMode(CANSparkMax.IdleMode.kCoast);

    mLeftDriveMotor1.follow(mLeftDriveMotor2);
    mRightDriveMotor1.follow(mRightDriveMotor2);

    mLeftEncoder = mLeftDriveMotor1.getEncoder();
    mRightEncoder = mRightDriveMotor1.getEncoder();
    
    //Convert raw encoder units to inches. Wheel DIA=3.5", Gear Ratio=4.89610:1
    mLeftEncoder.setPositionConversionFactor((Math.PI * 3.5)/4.89610);
    mRightEncoder.setPositionConversionFactor((Math.PI * 3.5)/4.89610);

    mLeftDriveMotor1.setInverted(true);
    mLeftDriveMotor2.setInverted(true);

    mRobotDrive = new DifferentialDrive(mLeftMotors, mRightMotors);
    mStick = new Joystick(0);
  }


  @Override
  public void robotPeriodic() {}


  @Override
  public void autonomousInit() {}


  @Override
  public void autonomousPeriodic() {}


  @Override
  public void teleopInit() {
    mRightEncoder.setPosition(0);
    mLeftEncoder.setPosition(0);
  }


  @Override
  public void teleopPeriodic() {
    mSpeed = -mStick.getY() * (mStick.getThrottle()+1)/2;
    mTwist = mStick.getTwist() * (mStick.getThrottle()+1)/2;
    mRobotDrive.arcadeDrive(mSpeed, mTwist);

    //System.out.println("Left:" +  mLeftEncoder.getPosition());
    //System.out.println("Right:" + mRightEncoder.getPosition());
  }

  @Override
  public void disabledInit() {}


  @Override
  public void disabledPeriodic() {}


  @Override
  public void testInit() {}


  @Override
  public void testPeriodic() {}


  @Override
  public void simulationInit() {}


  @Override
  public void simulationPeriodic() {}

}
