package com.d.utility;

import java.util.List;

import android.annotation.TargetApi;
import android.app.usage.UsageStats;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class CollectorMain {
	private final Context mContext;
	
	public static final String TAG = AppUsageCollector.class.getSimpleName();
	
	AppUsageCollector appUsageCollector;
	BatteryInfoCollector batteryInfoCollector;
	LocationCollector locationCollector;

	public CollectorMain(Context context)
	{
		this.mContext = context;
		appUsageCollector = new AppUsageCollector(mContext);
		batteryInfoCollector = new BatteryInfoCollector();
		locationCollector = new LocationCollector(mContext);
	}
	
	public String GetAppUsage()
	{
		String appUsageMessage = "";
		
		List<UsageStats> list = appUsageCollector.getUsages();
		/*UStats.getStats(mContext);
		List<UsageStats> list = UStats.getUsageStatsList(mContext);*/
		
		Log.d(TAG, String.valueOf("siize : " + list.size()));
		
		for (UsageStats stats : list)
		{
			String name = stats.getPackageName();
			long begin = (System.currentTimeMillis() - stats.getFirstTimeStamp()) / 1000;
			long length = (stats.getLastTimeStamp() - stats.getFirstTimeStamp()) / 1000;
			
			appUsageMessage = appUsageMessage + name + "(used " + begin + " sec. ago for " + length + " seconds)\n";
		}
		
		return appUsageMessage;
	}
	
	public String GetAppEvents()
	{
		String appUsageMessage = "";
		
	/*	List<String> list = appUsageCollector.getUsageEvents();
		
		for (String stats : list)
		{
			appUsageMessage = appUsageMessage + stats + "\n";
		}*/
		
		return appUsageMessage;
	}
	
	public String GetBatteryInfo()
	{
		String batteryInfoMessage = "";
		
		batteryInfoMessage = batteryInfoMessage + "Battery Voltage : " + batteryInfoCollector.voltage + "mV\n";
		batteryInfoMessage = batteryInfoMessage + "Battery Level : " + batteryInfoCollector.level + "\n";
		batteryInfoMessage = batteryInfoMessage + "Battery Scale : " + batteryInfoCollector.scale + "\n";
		batteryInfoMessage = batteryInfoMessage + "Battery Temperature : " + batteryInfoCollector.temperature + "¢ªC\n";
		batteryInfoMessage = batteryInfoMessage + "Battery Technology : " + batteryInfoCollector.technology + "\n";
		batteryInfoMessage = batteryInfoMessage + "Battery Plug Type : " + batteryInfoCollector.plugType + "\n";
		batteryInfoMessage = batteryInfoMessage + "Battery Health Type : " + batteryInfoCollector.healthType + "\n";
		batteryInfoMessage = batteryInfoMessage + "Battery Capacity : " + batteryInfoCollector.capacity + "%\n";
		
		return batteryInfoMessage;
	}
	
	public String GetLocation()
	{
		String locationMessage;
		
		if(locationCollector.canGetLocation())
		{
			locationCollector.getLocation();
			locationMessage = "(" + locationCollector.getLatitude() + ", " + locationCollector.getLongitude() +", "+ locationCollector.date + ")";
		}
		else
		{
			locationMessage = "Cannot get location";
		}
		
		return locationMessage;
	}
}
