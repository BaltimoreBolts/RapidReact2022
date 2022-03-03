package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.PowerDistribution;

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
  public PowerDistribution mPowerDistribution;

  public Joystick mStick;
  public double mSpeed = 0.0;
  public double mTwist = 0.0;

  public double wheelDia = 3.5; //inches
  public double gearRatio = 4.89610; //nearly 5:1

  public double autonStartTime;
  public double autonWaitTime;
  public double autonCurrentTime;
  public double autonFinalPos;
  
  @Override
  public void robotInit() {
    mPowerDistribution = new PowerDistribution(1, ModuleType.kRev);
    mPowerDistribution.clearStickyFaults();

    mLeftDriveMotor1 = new CANSparkMax(5, MotorType.kBrushless);
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
    mLeftDriveMotor1.setIdleMode(CANSparkMax.IdleMode.kBrake);
    mLeftDriveMotor2.setIdleMode(CANSparkMax.IdleMode.kBrake);
    mRightDriveMotor1.setIdleMode(CANSparkMax.IdleMode.kBrake);
    mRightDriveMotor2.setIdleMode(CANSparkMax.IdleMode.kBrake);

    mLeftDriveMotor1.follow(mLeftDriveMotor2);
    mRightDriveMotor1.follow(mRightDriveMotor2);

    mLeftEncoder = mLeftDriveMotor1.getEncoder();
    mRightEncoder = mRightDriveMotor1.getEncoder();
    
    //Convert raw encoder units to inches using PIxdia/gearRatio
    mLeftEncoder.setPositionConversionFactor((Math.PI * wheelDia)/gearRatio);
    mRightEncoder.setPositionConversionFactor((Math.PI * wheelDia)/gearRatio);

    mLeftDriveMotor1.setInverted(true);
    mLeftDriveMotor2.setInverted(true);

    mLeftDriveMotor1.burnFlash();
    mLeftDriveMotor2.burnFlash();
    mRightDriveMotor1.burnFlash();
    mRightDriveMotor2.burnFlash();

    mRobotDrive = new DifferentialDrive(mLeftMotors, mRightMotors);
    mStick = new Joystick(0);

    autonWaitTime = 2;
    autonFinalPos = -45;
  }


  @Override
  public void robotPeriodic() {
    //push values to dashboard here
    SmartDashboard.putNumber("[DT] Left Encoder Position", mLeftEncoder.getPosition());
    SmartDashboard.putNumber("[DT] Right Encoder Position", mRightEncoder.getPosition());
  }


  @Override
  public void autonomousInit() {
    mRightEncoder.setPosition(0);
    mLeftEncoder.setPosition(0);
    autonStartTime = Timer.getFPGATimestamp();
  }


  @Override
  public void autonomousPeriodic() {
    autonCurrentTime = Timer.getFPGATimestamp();
    //wait x time
    if ((autonCurrentTime - autonStartTime) >= autonWaitTime) {
      //shoot cargo

      //move off tarmac at least 45" backwards (negative position)
      if (mLeftEncoder.getPosition() > autonFinalPos){
        //System.out.println(mLeftEncoder.getPosition());
        mRobotDrive.arcadeDrive(-0.25, 0);
      }
      else {
        mRobotDrive.arcadeDrive(0, 0);
      }
    }
    else {
      mRobotDrive.arcadeDrive(0, 0);
    }
    
  }


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
  }

  @Override
  public void disabledInit() {}


  @Override
  public void disabledPeriodic() {}
}
