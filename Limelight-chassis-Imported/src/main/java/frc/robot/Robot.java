package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup; 
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.Compressor; 
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.DoubleSolenoid; 
import static edu.wpi.first.wpilibj.DoubleSolenoid.Value.*;
import edu.wpi.first.wpilibj.MotorSafety;
import edu.wpi.first.wpilibj.motorcontrol.PWMMotorController;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
    rightSpeedGroup.setInverted(true);
   
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {}

  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }
  //InitializeMotors
  
  private final WPI_TalonFX rightmotor1 = new WPI_TalonFX(4);
  private final WPI_TalonFX rightmotor2 = new WPI_TalonFX(2);
  private final WPI_TalonFX leftmotor1 = new WPI_TalonFX(3);
  private final WPI_TalonFX leftmotor2 = new WPI_TalonFX(1);
  public final PWMSparkMax intake = new PWMSparkMax(5);
  public final PWMSparkMax transport = new PWMSparkMax(6);
  public final CANSparkMax canon = new CANSparkMax(20,MotorType.kBrushless);
  public final CANSparkMax canonb = new CANSparkMax(21,MotorType.kBrushless);
  public final CANSparkMax canonf = new CANSparkMax(22,MotorType.kBrushless);

  
  // //SpeedControllerGroups
  private final MotorControllerGroup rightSpeedGroup = new MotorControllerGroup(rightmotor1, rightmotor2);
  private final MotorControllerGroup leftSpeedGroup = new MotorControllerGroup(leftmotor1, leftmotor2);
  
  // 
  DifferentialDrive drivetrain = new DifferentialDrive(leftSpeedGroup, rightSpeedGroup);
  
  // Controles
  XboxController stick = new XboxController(1);
  XboxController extra = new XboxController(2);
  
  
  //Pneumatica
  private final Compressor pcmCompressor = new Compressor(0, PneumaticsModuleType.CTREPCM);
  private final DoubleSolenoid sol = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 0, 1);
  private final DoubleSolenoid sol1 = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 2, 3);




  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {}

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
  Update_Limelight_Tracking();
    
  // Conduccion
  drivetrain.arcadeDrive(stick.getLeftY(), stick.getRightX());
  
  ///canon.set(extra.getRightX()/4);
  // Activar la neumatica en pares
  if (extra.getAButton()){
    

    sol.set(DoubleSolenoid.Value.kForward);
    sol1.set(DoubleSolenoid.Value.kForward);
 }
 else if (extra.getBButton()){
   sol.set(DoubleSolenoid.Value.kReverse);
   sol1.set(DoubleSolenoid.Value.kReverse);
   
 }
  if (extra.getXButton()){
    intake.set(-0.60);
    transport.set(-0.85);
  } 
  else if (extra.getYButton()){
    intake.set(0);
    transport.set(0);
  }
  
  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}
  //Posible asesino ***********
  
  public void Update_Limelight_Tracking()
  {
    NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
    NetworkTableEntry ty = table.getEntry("ty");
    double targetOffsetAngle_Vertical = ty.getDouble(0.0);
    double tv = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tv").getDouble(0);
    double tx = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tx").getDouble(0);
    double ta = NetworkTableInstance.getDefault().getTable("limelight").getEntry("ta").getDouble(0);

    //Apaga led limelight
    NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(1);

    // how many degrees back is your limelight rotated from perfectly vertical?
    double limelightAngle = 0.0;

    //inicializar en 0 el motor del engrane del cañon 
    
    if (extra.getLeftBumper()){
      NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(3);
      if (tv == 1){
        if (tx > 2 && tx <= 27){
          canon.set(0.05);    
            }
        if (tx < -2 && tx >= -27){
          canon.set(-0.05);
          }
           
        if (tx < 1.95 && tx > -1.95) {
          canon.set(0);
          }
        }  
      
      if (targetOffsetAngle_Vertical >= -20.5 && targetOffsetAngle_Vertical <= -17 ){
        canonb.set(-0.75);
        canonf.set(0.6);
          }
      if (targetOffsetAngle_Vertical > -17 && targetOffsetAngle_Vertical <= -14 ){
        canonb.set(-0.6);
        canonf.set(0.55);
          }
      if (targetOffsetAngle_Vertical > -14 && targetOffsetAngle_Vertical <= -11 ){
        canonb.set(-0.55);
        canonf.set(0.5);
          }
      if (targetOffsetAngle_Vertical > -11 && targetOffsetAngle_Vertical <= -8 ){
        canonb.set(-0.45);
        canonf.set(0.6);
          }
      if (targetOffsetAngle_Vertical > -8 && targetOffsetAngle_Vertical <= -5 ){
        canonb.set(-0.4);
        canonf.set(0.7);
          }
      if (targetOffsetAngle_Vertical > -5 && targetOffsetAngle_Vertical <= -2 ){
        //para lower hub
          canonb.set(-0.4);
          canonf.set(0.3);
          }
    }
    else{
      canon.set(0);
      canonb.set(0);
      canonf.set(0);
    }
     
  }
}
