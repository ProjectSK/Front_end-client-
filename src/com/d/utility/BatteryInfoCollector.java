package com.d.utility;

import java.util.Calendar;

import com.d.localdb.BatteryRecord;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public class BatteryInfoCollector {
/*	private final Intent mIntent;*/
	
	BatteryManager batteryManager;
	
	long capacity;
	
	long level;
	long scale;
	long voltage;
	float temperature;
	
	/*
	 * 분단위, 목표 시간에 맞추어 배터리 최적화
	 */
	long goaltime;
	String technology;
	String healthType;
	String plugType;
	
	
	private BroadcastReceiver bcr = new BroadcastReceiver() {
		int count = 0;

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			count++;
			if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
				update(intent);
			}
			if (action.equals(Intent.ACTION_BATTERY_LOW)) {
				update(intent);
			}
			if (action.equals(Intent.ACTION_BATTERY_OKAY)) {
				update(intent);
			}
			if (action.equals(Intent.ACTION_POWER_CONNECTED)) {
				update(intent);
			}
			if (action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
				update(intent);
			}
		}
	};
	
	public BatteryInfoCollector()
	{
	//	this.mIntent = intent;
		batteryManager = new BatteryManager();
		goaltime = 60 * 10; 
		previousTime = System.currentTimeMillis();
		previousRatio = 100 / (goaltime * 60 * 1000);
		previousState = 100;
		requestTime = 1000;
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
		requestTime = 1000;
		if( isGoalInRightRange(_goaltime)){
			goaltime = _goaltime;
		}
	}
	
	
	private long previousTime ;
	private double previousRatio;
	private long previousState ;
	private long requestTime ;
	
	public BatteryRecord getRecord(){
		return new BatteryRecord(Calendar.getInstance().getTime(), capacity, level, scale, voltage, temperature, healthType, plugType);
	}
	
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
		requestTime = (requestTime < 1000)? 1000 : requestTime;
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
