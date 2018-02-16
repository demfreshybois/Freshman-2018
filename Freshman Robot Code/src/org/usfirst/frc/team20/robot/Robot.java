/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package org.usfirst.frc.team20.robot;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.kauailabs.navx.frc.AHRS;
import com.kauailabs.navx.frc.AHRS.SerialDataType;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DriverStation;

public class Robot extends IterativeRobot {
	
 //global variables	
 Controller driverJoy;
 Controller operatorJoy;
 TalonSRX bl = new TalonSRX(1);
 TalonSRX br = new TalonSRX(2);
 TalonSRX fl = new TalonSRX(4);
 TalonSRX fr = new TalonSRX(3);
 Timer c = new Timer();
 private double teleSPD=.75;
 AHRS gyro = new AHRS(SerialPort.Port.kMXP);
 
 //initialize controllers
 @Override
 public void robotInit() {
  driverJoy = new Controller(0);
  operatorJoy = new Controller(1);
  bl.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0, 1000);
  bl.setSensorPhase(false);
  br.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0, 1000);
  br.setSensorPhase(false);
  fl.follow(bl);
  fr.follow(br);
 }
 
 //starts the timer at the start of auto 
 @Override
 public void autonomousInit() {
  c.start();
 }
 
 //begin the auto phase
 @Override
 public void autonomousPeriodic(){
	 //commented out so we could develop a navX auto
	 
     //timeBasedAuto();
	 encoderBasedAuto();
	 navXBasedAuto();
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
  br.set(ControlMode.PercentOutput, right*teleSPD);
  */
  
  //Arcade Mode
  bl.set(ControlMode.PercentOutput, (left - rightTurn + leftTurn)*teleSPD); //set speed values with SPD constant specified at the top
  br.set(ControlMode.PercentOutput, (-left - rightTurn + leftTurn)*teleSPD);
  
  System.out.println("X"+gyro.getDisplacementX()); //forward and backward
  System.out.println("Y"+gyro.getDisplacementY()); //left and right
  System.out.println("Z"+gyro.getDisplacementZ()); //up and down
  System.out.println("Yaw"+gyro.getYaw());
 }
 
 public void timeBasedAuto() {
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
	     br.set(ControlMode.PercentOutput, 0.25);
	    }else{
	    bl.set(ControlMode.PercentOutput, 0); //STAHPP
	    br.set(ControlMode.PercentOutput, 0);
	    }
	    
	    //drop the cube
	    break;
	    
	   case 1://forward drive and turn left when starting from the right side (rightDesired==true)
	    timeStamp=6;//six seconds will get us across the line and next to the switch
	    if(c.get() < timeStamp){//while the game timer is less than our time stamp
	     bl.set(ControlMode.PercentOutput, -0.25); //move forward
	     br.set(ControlMode.PercentOutput, 0.25);
	    }else{
	    bl.set(ControlMode.PercentOutput, 0); //STAHPP
	    br.set(ControlMode.PercentOutput, 0);
	    }
	    
	    //turn the robot 90 degrees left (can we use encoders to figure out how much/how long we need to turn?)
	    bl.set(ControlMode.PercentOutput, .25);
	    br.set(ControlMode.PercentOutput, .25);
	    
	    //drop the cube
	    break;
	    
	   case 2://forward drive and turn right when starting from the left side (rightDesired==false)
	    
	    timeStamp=6;
	    if(c.get() < timeStamp){//while the game timer is less than our time stamp
	     bl.set(ControlMode.PercentOutput, -0.25); //move forward
	     br.set(ControlMode.PercentOutput, 0.25);
	    }else{
	    bl.set(ControlMode.PercentOutput, 0); //STAHPP
	    br.set(ControlMode.PercentOutput, 0);
	    }
	    
	  //turn the robot 90 degrees left (can we use encoders to figure out how much/how long we need to turn?)
	    bl.set(ControlMode.PercentOutput, -.25);
	    br.set(ControlMode.PercentOutput, -.25);
	    
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
 public void navXBasedAuto() {
	 int autoChoice=0;
	 
	 double x=gyro.getDisplacementX();
	 double y=gyro.getDisplacementY();
	 double z=gyro.getDisplacementZ();
	 //meters to feet
	 double conversionFactor=3.28084;
	 //in feet
	 double distanceStamp=11.0;
	 
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
		   if(x < distanceStamp){
			   bl.set(ControlMode.PercentOutput, -0.25); //move forward
			   br.set(ControlMode.PercentOutput, 0.25);
		   }else{
			   bl.set(ControlMode.PercentOutput, 0); //STAHPP
			   br.set(ControlMode.PercentOutput, 0);
		   }
	    break;
	    
	   case 1://forward drive and turn left when starting from the right side (rightDesired==true)

	    break;
	    
	   case 2://forward drive and turn right when starting from the left side (rightDesired==false)
	   
	    break;
	    
	   case 3://drive to the exchange zone and drop preload when starting from left position
		   break;
	    
	   case 4://drive to the exchange zone and drop preload when starting from middle position
		   break;
	    
	   case 5://drive to the exchange zone and drop preload when starting from right position
		   break;
	  }
 }
 
 public void encoderBasedAuto() {
	 
	 //TEST PRINT
	  System.out.println("Encoder left: " + bl.getSelectedSensorPosition(0));
	  System.out.println("Encoder right: " + br.getSelectedSensorPosition(0)); 
	  
	  
	  
	  int autoChoice=0;
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

	    break;
	    
	   case 1://forward drive and turn left when starting from the right side (rightDesired==true)

	    break;
	    
	   case 2://forward drive and turn right when starting from the left side (rightDesired==false)
	   
	    break;
	    
	   case 3://drive to the exchange zone and drop preload when starting from left position
		   break;
	    
	   case 4://drive to the exchange zone and drop preload when starting from middle position
		   break;
	    
	   case 5://drive to the exchange zone and drop preload when starting from right position
		   break;
	  }
 }
}
