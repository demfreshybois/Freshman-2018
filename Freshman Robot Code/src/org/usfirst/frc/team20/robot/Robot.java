/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package org.usfirst.frc.team20.robot;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;

public class Robot extends IterativeRobot {
	
 //global wariables	
 Controller driverJoy;
 Controller operatorJoy;
 TalonSRX bl = new TalonSRX(1);
 TalonSRX br = new TalonSRX(2);
 TalonSRX fl = new TalonSRX(4);
 TalonSRX fr = new TalonSRX(3);
 Timer c = new Timer();
 private double teleSPD=.25;
 
 //encoder initialization
 Encoder encLeft; {
	 encLeft = new Encoder(0, 1, false, Encoder.EncodingType.k4X);
	 encLeft.reset(); //sets the encoder to 0
	 int leftEncDist =  encLeft.get(); //sets input to a workable wariable
 	} 
 Encoder encRight; {
	 encRight = new Encoder(0, 1, true, Encoder.EncodingType.k4X);
	 encRight.reset();
	 int rightEncDist =  encRight.get();
 	}
 
 //initialize controllers
 @Override
 public void robotInit() {
  driverJoy = new Controller(0);
  operatorJoy = new Controller(1);
 }
 
 //starts the timer at the start of auto 
 @Override
 public void autonomousInit() {
  c.start();
 }
 
 //begin the auto phase
 @Override
 public void autonomousPeriodic(){
  
  int autoChoice=0;
  double timeStamp;
  String gameData = DriverStation.getInstance().getGameSpecificMessage();
  
  //field input
  if(gameData.charAt(0) == 'L'){ //first switch is on the left side
	  autoChoice=1;//runs third auto
  }
  else if (gameData.charAt(0) == 'R'){
	  autoChoice=2;//runs second auto
  }
  else{
	  autoChoice=0;//runs first auto as default
  }
  
  switch(autoChoice) {
   case 0://forward drive into the switch from middle position
    timeStamp=5.25;//this will get us across the line;
    if(c.get() < timeStamp){//while the game timer is less than our time stamp
     bl.set(ControlMode.PercentOutput, -0.25); //move forward
     fl.set(ControlMode.PercentOutput, -0.25);
     fr.set(ControlMode.PercentOutput, 0.25);
     br.set(ControlMode.PercentOutput, 0.25);
    }else{
    bl.set(ControlMode.PercentOutput, 0); //STAHPP
    fl.set(ControlMode.PercentOutput, 0);
    fr.set(ControlMode.PercentOutput, 0);
    br.set(ControlMode.PercentOutput, 0);
    }
    
    //drop the cube
    break;
    
   case 1://forward drive and turn left when starting from the right side (rightDesired==true)
    timeStamp=6;//six seconds will get us across the line and next to the switch
    if(c.get() < timeStamp){//while the game timer is less than our time stamp
     bl.set(ControlMode.PercentOutput, -0.25); //move forward
     fl.set(ControlMode.PercentOutput, -0.25);
     fr.set(ControlMode.PercentOutput, 0.25);
     br.set(ControlMode.PercentOutput, 0.25);
    }else{
    bl.set(ControlMode.PercentOutput, 0); //STAHPP
    fl.set(ControlMode.PercentOutput, 0);
    fr.set(ControlMode.PercentOutput, 0);
    br.set(ControlMode.PercentOutput, 0);
    }
    
    //turn the robot 90 degrees left (can we use encoders to figure out how much/how long we need to turn?)
    bl.set(ControlMode.PercentOutput,.25);
    fl.set(ControlMode.PercentOutput,.25);
    fr.set(ControlMode.PercentOutput,.25);
    br.set(ControlMode.PercentOutput,.25);
    
    //drop the cube
    break;
    
   case 2://forward drive and turn right when starting from the left side (rightDesired==false)
    
    timeStamp=6;
    if(c.get() < timeStamp){//while the game timer is less than our time stamp
     bl.set(ControlMode.PercentOutput, -0.25); //move forward
     fl.set(ControlMode.PercentOutput, -0.25);
     fr.set(ControlMode.PercentOutput, 0.25);
     br.set(ControlMode.PercentOutput, 0.25);
    }else{
    bl.set(ControlMode.PercentOutput, 0); //STAHPP
    fl.set(ControlMode.PercentOutput, 0);
    fr.set(ControlMode.PercentOutput, 0);
    br.set(ControlMode.PercentOutput, 0);
    }
    
  //turn the robot 90 degrees left (can we use encoders to figure out how much/how long we need to turn?)
    bl.set(ControlMode.PercentOutput,-.25);
    fl.set(ControlMode.PercentOutput,-.25);
    fr.set(ControlMode.PercentOutput,-.25);
    br.set(ControlMode.PercentOutput,-.25);
    
    //drop the cube
    break;
    
   case 3://drive to the exchange zone and drop preload when starting from left position
	   break;
    
   case 4://drive to the exchange zone and drop preload when starting from middle position
	   break;
    
   case 5://drive to the exchange zone and drop preload when starting from right position
	   break;
  }
 }
 
 
 @Override
 public void teleopPeriodic() {
  
  //Joysticks and Back Buttons
  double left = driverJoy.getLeftYAxis();
  double right = driverJoy.getRightYAxis();
  double leftTurn = -(driverJoy.getLeftTriggerAxis()); //Left/Right Joystick Looking Things
  double rightTurn= -(driverJoy.getRightTriggerAxis());   //These are inverted to accomodate for correct turning -Liam
  
  //Tank Mode (Unactivated)
  /*
  bl.set(ControlMode.PercentOutput, -left*teleSPD);    
  fl.set(ControlMode.PercentOutput, -left*teleSPD);
  fr.set(ControlMode.PercentOutput, right*teleSPD);
  br.set(ControlMode.PercentOutput, right*teleSPD);
  */
  
  //Arcade Mode
  bl.set(ControlMode.PercentOutput, (left - rightTurn + leftTurn)*teleSPD); //set speed values with SPD constant specified at the top
  fl.set(ControlMode.PercentOutput, (left - rightTurn + leftTurn)*teleSPD);
  fr.set(ControlMode.PercentOutput, (-left - rightTurn + leftTurn)*teleSPD);
  br.set(ControlMode.PercentOutput, (-left - rightTurn + leftTurn)*teleSPD);
 }
}
