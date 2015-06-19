package com.d.utility;

import android.content.Intent;
import android.os.BatteryManager;

public class BatteryInfoCollector {
/*	private final Intent mIntent;*/
	
	BatteryManager batteryManager;
	
	long capacity;
	
	int level;
	int scale;
	int voltage;
	float temperature;
	
	/*
	 * 분단위, 목표 시간에 맞추어 배터리 최적화
	 */
	int goaltime;
	String technology;
	String healthType;
	String plugType;
	
	public BatteryInfoCollector()
	{
	//	this.mIntent = intent;
		batteryManager = new BatteryManager();
		goaltime = 60 * 10; 
		previousTime = System.currentTimeMillis();
		previousRatio = 100 / (goaltime * 60 * 1000);
		previousState = 100;
		requestTime = 100;
	}
	private boolean isGoalInRightRange(int goaltime){
		return 0 <= goaltime;
		
	}
	public BatteryInfoCollector(int _goaltime){
	//	this.mIntent = intent;
		batteryManager = new BatteryManager();
		goaltime = 60 * 10; 
		previousTime = System.currentTimeMillis();;
		previousRatio = 100 / (goaltime * 60 * 1000);
		previousState = 100;
		requestTime = 100;
		if( isGoalInRightRange(_goaltime)){
			goaltime = _goaltime;
		}
	}
	
	
	private long previousTime ;
	private double previousRatio;
	private long previousState ;
	private long requestTime ;
	
	public long batteryCalculator(){
		/* 수정요망 */
		
		long presentTime = System.currentTimeMillis();
		double presentRatio;
		long presentState = capacity;
		double goalRatio = 100.0 / (goaltime * 60 * 1000);
		double alpha = 0.5;
		double rate = 1.0;
		
		//밀리 세컨드당 베터리 소모율
		presentRatio = (double)(presentState - previousState) / (presentTime - previousTime);
		presentRatio = (presentRatio * alpha + previousRatio *( 1- alpha));
		
		
		if(presentRatio != 0)
			rate = goalRatio / presentRatio;
		
		requestTime = (long) (requestTime * rate);
		previousRatio = presentRatio;
		previousTime = presentTime;
		previousState = presentState;
		
		/*
		 * 최소치 보정
		 */
		requestTime = (requestTime < 100)? 100 : requestTime;
		// 최대치 보정
		requestTime = (requestTime > 60000)? 5000 : requestTime; 
		
		return requestTime;		
		
		
	}
	public long batteryCalculator(int goaltime_minute){
		long presentTime = System.currentTimeMillis();
		double presentRatio;
		long presentState = capacity;
		double goalRatio = 100.0 / (goaltime_minute * 60 * 1000);
		double alpha = 0.5;
		double rate = 1.0;
		
		//밀리 세컨드당 베터리 소모율
		presentRatio = (double)(presentState - previousState) / (presentTime - previousTime);
		presentRatio = (presentRatio * alpha + previousRatio *( 1- alpha));
		
		if(presentRatio != 0)
			rate = goalRatio / presentRatio;
		
		requestTime = (long) (requestTime * rate);
		previousRatio = presentRatio;
		previousTime = presentTime;
		previousState = presentState;
		
		return requestTime;
	}
	

	public void update(Intent mIntent)
	{
		int plug = mIntent.getIntExtra("plugged", 0);
		level = mIntent.getIntExtra("level", 0);
		scale = mIntent.getIntExtra("scale", 100);
		voltage = mIntent.getIntExtra("voltage", 0);
		temperature = mIntent.getIntExtra("temperature", 0) / 10.0f;
		technology = mIntent.getStringExtra("technology");
		int health = mIntent.getIntExtra("health", BatteryManager.BATTERY_HEALTH_UNKNOWN);
		
		switch(plug)
		{
		case 0:
			plugType = "Unplugged";
			break;
		case BatteryManager.BATTERY_PLUGGED_AC:
			plugType = "AC";
			break;
		case BatteryManager.BATTERY_PLUGGED_USB:
			plugType = "USB";
			break;
		case (BatteryManager.BATTERY_PLUGGED_AC|BatteryManager.BATTERY_PLUGGED_USB):
			plugType = "AC and USB";
			break;
		default:
			plugType = "Unknown";
			break;
		}
		
		if (health == BatteryManager.BATTERY_HEALTH_GOOD)
		{
			healthType = "Good";
		}
		else if (health == BatteryManager.BATTERY_HEALTH_OVERHEAT)
		{
			healthType = "OverHeat";
		}
		else if (health == BatteryManager.BATTERY_HEALTH_DEAD)
		{
			healthType = "Dead";
		}
		else if (health == BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE)
		{
			healthType = "Over voltage";
		}
		else if (health == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE)
		{
			healthType = "Unknown error";
		}
		else if (health == BatteryManager.BATTERY_HEALTH_COLD)
		{
			healthType = "Cold";
		}
		else
		{
			healthType = "Unknown";
		}
		
		capacity = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
		System.out.println(capacity);
	}
	@Override
	public String toString(){
	{
		String batteryInfoMessage = "";
		
		batteryInfoMessage = batteryInfoMessage + "Battery Voltage : " + voltage + "mV\n";
		batteryInfoMessage = batteryInfoMessage + "Battery Level : " + level + "\n";
		batteryInfoMessage = batteryInfoMessage + "Battery Scale : " + scale + "\n";
		batteryInfoMessage = batteryInfoMessage + "Battery Temperature : " + temperature + "˚C\n";
		batteryInfoMessage = batteryInfoMessage + "Battery Technology : " + technology + "\n";
		batteryInfoMessage = batteryInfoMessage + "Battery Plug Type : " + plugType + "\n";
		batteryInfoMessage = batteryInfoMessage + "Battery Health Type : " + healthType + "\n";
		batteryInfoMessage = batteryInfoMessage + "Battery Capacity : " + capacity + "%\n";
		batteryInfoMessage += "Request time : " + batteryCalculator() + "ms\n";		
		
		return batteryInfoMessage;
	}
		
	}
}
