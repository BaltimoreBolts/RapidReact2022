package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.Servo;

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

  public CANSparkMax mIntakeMotor;
  public CANSparkMax mIndexMotor;
  public CANSparkMax mShooterMotor;
  public RelativeEncoder mShooterEncoder;
  
  public PowerDistribution mPowerDistribution;
  public Servo mCameraServo;

  public DigitalInput mNoCargoAtIntake;
  public boolean mCargoAtIntake;
  public DigitalInput mCargoBeforeShooter;

  public Joystick mStick;
  public XboxController mXbox;
  public double mSpeed = 0.0;
  public double mTwist = 0.0;

  public double wheelDia = 3.5; //inches
  public double gearRatio = 4.89610; //nearly 5:1

  public double autonStartTime;
  public double autonWaitTime = 2; //seconds to wait
  public double autonCurrentTime;
  public double autonFinalPos = -45; //inches to drive backwards
  
  public boolean mShootNow = false;
  public boolean mIntakeNow = false;
  public boolean mIntakeAndIndexNow = false;

  public double intakeStartTime;
  public double intakeCurrentTime;
  public double intakeTime = 2; //seconds to intake cargo

  public double shootHighPercent = 0.80;
  public double shootHighSpeed = 4200;
  public double shootLowPercent = 0.50;
  public double shootLowSpeed = 2500;
  public double shootPercent;
  public double shootSpeed;

  public double shootStartTime;
  public double shootCurrentTime;
  public double shootOneTime = 2; //seconds to fire 1 cargo
  public double shootTwoTime = 5; //seconds to fire 2 cargo
  public double shootTime = 0;
  
  @Override
  public void robotInit() {
    //Power Distribution -- must be at CAN ID 1
    mPowerDistribution = new PowerDistribution(1, ModuleType.kRev);
    mPowerDistribution.clearStickyFaults();
    mPowerDistribution.setSwitchableChannel(false);

    //Drive Motors
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

    //Sensors
    mNoCargoAtIntake = new DigitalInput(0); //TRUE = no cargo; FALSE = cargo!
    mCargoBeforeShooter = new DigitalInput(1); //TRUE = cargo!; FALSE = no cargo
    mCameraServo = new Servo(0);

    //Main Mechanism
    
    mIntakeMotor = new CANSparkMax(6, MotorType.kBrushless);
    mIndexMotor = new CANSparkMax(7, MotorType.kBrushless);
    mShooterMotor = new CANSparkMax(8, MotorType.kBrushless);

    mIntakeMotor.restoreFactoryDefaults();
    mIndexMotor.restoreFactoryDefaults();
    mShooterMotor.restoreFactoryDefaults();
    mIntakeMotor.setSmartCurrentLimit(40);
    mIndexMotor.setSmartCurrentLimit(40);
    mShooterMotor.setSmartCurrentLimit(40);
    mIndexMotor.setIdleMode(CANSparkMax.IdleMode.kCoast);
    mIndexMotor.setIdleMode(CANSparkMax.IdleMode.kCoast);
    mShooterMotor.setIdleMode(CANSparkMax.IdleMode.kCoast);

    mShooterEncoder = mShooterMotor.getEncoder();
    mIntakeMotor.setInverted(true);

    mIntakeMotor.burnFlash();
    mIndexMotor.burnFlash();
    mShooterMotor.burnFlash();
    
    mStick = new Joystick(0);
    mXbox = new XboxController(1);
    
    mIntakeNow = false;
    mShootNow = false;
    mIntakeAndIndexNow = false;
    shootPercent = 0;
    shootSpeed = 0;
  }


  @Override
  public void robotPeriodic() {
    mCargoAtIntake = !mNoCargoAtIntake.get(); //invert - TRUE=cargo!

    //Light control
    if (mCargoBeforeShooter.get()) {
        mPowerDistribution.setSwitchableChannel(true);
    }
    else {
        mPowerDistribution.setSwitchableChannel(false);
      }

    //push values to dashboard here
    SmartDashboard.putNumber("[DT] LT-EncPos", mLeftEncoder.getPosition());
    SmartDashboard.putNumber("[DT] RT-EncPos", mRightEncoder.getPosition());
    SmartDashboard.putNumber("[Shoot] RPM", mShooterEncoder.getVelocity());
    SmartDashboard.putBoolean("[Cargo] Intake", mCargoAtIntake);
    SmartDashboard.putBoolean("[Cargo] Index", mCargoBeforeShooter.get());
    SmartDashboard.putNumber("Servo Angle", mCameraServo.getAngle());
    SmartDashboard.putNumber("Shoot Time", shootTime);

  }


  @Override
  public void autonomousInit() {
    mRightEncoder.setPosition(0);
    mLeftEncoder.setPosition(0);
    mCameraServo.setAngle(70);
    autonStartTime = Timer.getFPGATimestamp();
  }


  @Override
  public void autonomousPeriodic() {
    autonCurrentTime = Timer.getFPGATimestamp();
    //wait x time
    if ((autonCurrentTime - autonStartTime) >= autonWaitTime) {
      //shoot cargo
      /*
      read camera to find good position?
      move robot if needed to correct position?
      spin up fly wheel to 0.75 (~4000 RPM)
      wait x seconds?
      move index motor to push cargo into flywheel
      stop both motors after x seconds
  
      wait for shoot routine to complete before moving off tarmac
      */

      //move off tarmac at least 45" backwards (negative position)
      if (mLeftEncoder.getPosition() > autonFinalPos){
        //System.out.println(mLeftEncoder.getPosition());
        mRobotDrive.arcadeDrive(-0.35, 0);
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

    mIntakeNow = false;
    mShootNow = false;
    mIntakeAndIndexNow = false;

    //set servo to 45deg angle up
    mCameraServo.setAngle(10);
  }

  @Override
  public void teleopPeriodic() {
    //Drive robot
    mSpeed = -mStick.getY() * ((mStick.getThrottle()*-0.5)+0.5);
    mTwist = mStick.getTwist() * ((mStick.getThrottle()*-0.5)+0.5);
    mRobotDrive.arcadeDrive(mSpeed, mTwist);

    //Reset encoder
    if (mStick.getRawButton(12)) {
      mRightEncoder.setPosition(0);
      mLeftEncoder.setPosition(0);
    }

    //Set camera servo to top
    if (mXbox.getPOV()==0) {
      mCameraServo.setAngle(70);
    }
    
    //Set camera servo to down
    if (mXbox.getPOV()==180) {
      mCameraServo.setAngle(10);
    }

    //Set camera servo to mid
    if (mXbox.getPOV()==270){
      mCameraServo.setAngle(45);
    }
    
    //If no cargo in hand -> take cargo all the way into index
    if (!mCargoAtIntake && !mCargoBeforeShooter.get() && mStick.getRawButton(1)) {
      mIntakeAndIndexNow = true;
      intakeStartTime = Timer.getFPGATimestamp();
    }

    if (mIntakeAndIndexNow){
      intakeCurrentTime = Timer.getFPGATimestamp();
      if (!mCargoBeforeShooter.get() && (intakeCurrentTime-intakeStartTime<intakeTime)){
        mIntakeMotor.set(0.5);
        mIndexMotor.set(0.5);
      }
      else {
        mIntakeMotor.stopMotor();
        mIndexMotor.stopMotor();
        mIntakeAndIndexNow = false;
      }
    }

    //If cargo in body, ONLY bring into intake
    if (!mCargoAtIntake && mCargoBeforeShooter.get() && mStick.getRawButton(1)){
      mIntakeNow = true;
      intakeStartTime = Timer.getFPGATimestamp();
    }

    if (mIntakeNow){
      intakeCurrentTime = Timer.getFPGATimestamp();
      if (!mCargoAtIntake && (intakeCurrentTime-intakeStartTime<intakeTime)){
        mIntakeMotor.set(0.5);
      }
      else {
        mIntakeMotor.stopMotor();
        mIntakeNow = false;
      }
    }

    //Shoot High (Xbox Y) - start fly wheel if cargo in place
    if (mCargoBeforeShooter.get()){

      //Low shoot -- Xbox A
      if (mXbox.getAButton()) {
        mShootNow = true;
        mShooterMotor.set(shootLowPercent);
        shootStartTime = Timer.getFPGATimestamp();
        shootSpeed = shootLowSpeed;
      }

      //High Shoot -- Xbox Y
      if (mXbox.getYButton()) {
        mShootNow = true;
        mShooterMotor.set(shootHighPercent);
        shootStartTime = Timer.getFPGATimestamp();
        shootSpeed = shootHighSpeed;
      }
      
      //decide whether to shoot one or two cargo
      if (mCargoAtIntake && mCargoBeforeShooter.get()) {
        shootTime = shootTwoTime;
      }
      else {
        shootTime = shootOneTime;
      }
    }

    //Shoot - wait until flywheel up to speed
    if (mShootNow && mShooterEncoder.getVelocity() >= shootSpeed){
      shootCurrentTime = Timer.getFPGATimestamp();
      if (shootCurrentTime - shootStartTime < shootTime) {
        mIndexMotor.set(0.5);
        mIntakeMotor.set(0.5);
      }
      else {
        mIndexMotor.stopMotor();
        mIntakeMotor.stopMotor();
        mShooterMotor.stopMotor();
        mShootNow = false;
      }
    }
    
  }

  @Override
  public void disabledInit() {}


  @Override
  public void disabledPeriodic() {}
}
