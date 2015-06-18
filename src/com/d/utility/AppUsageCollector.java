package com.d.utility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import android.annotation.TargetApi;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

public class AppUsageCollector extends Service {
	private final Context mContext;
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("M-d-yyyy HH:mm:ss");
    public static final String TAG = AppUsageCollector.class.getSimpleName();
	
	UsageStatsManager usageStatsManager;
	
	List<UsageStats> stats;
	
	Calendar calendar;
	
	long currentForegroundStartTime;
	String currentForegroundPackageName = null;
	
	public AppUsageCollector(Context context)
	{
		mContext = context;
		usageStatsManager=(UsageStatsManager)mContext.getSystemService("usagestats");
		calendar = Calendar.getInstance();
		getUsages();
	}
	
	public List<UsageStats> getUsages()
	{
		Calendar endCal = calendar;
		Calendar startCal = calendar;
		endCal.add(Calendar.YEAR, 1);
		long endTime = calendar.getTimeInMillis();
		startCal.add(Calendar.YEAR, -2);
		long startTime = calendar.getTimeInMillis();
		
		Log.d(TAG, "Range start:" + dateFormat.format(startTime) );
        Log.d(TAG, "Range end:" + dateFormat.format(endTime));		
		
		stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_YEARLY, startTime, endTime);
		
		Log.d(TAG, String.valueOf(stats.size()));
		return stats;
	}
	
	public List<String> getUsageEvents()
	{
		UsageStatsManager usm = (UsageStatsManager) mContext.getSystemService("usagestats");
		Calendar endCal = Calendar.getInstance();
		endCal.add(Calendar.DATE, 1);
		long endTime = endCal.getTimeInMillis();
		long startTime = calendar.getTimeInMillis();
		
		Log.d(TAG, "Range start:" + dateFormat.format(startTime) );
        Log.d(TAG, "Range end:" + dateFormat.format(endTime));
        
        UsageEvents uEvents = usm.queryEvents(startTime,endTime);
        calendar = Calendar.getInstance();
        
        List<String> usageEvents = new Vector<String>();
        
        while (uEvents.hasNextEvent()){
            UsageEvents.Event e = new UsageEvents.Event();
            uEvents.getNextEvent(e);

            if (e != null){
            	if(e.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND)
            	{
            		currentForegroundStartTime = e.getTimeStamp();
            		currentForegroundPackageName = e.getPackageName();
            	}
            	else if (e.getEventType() == UsageEvents.Event.MOVE_TO_BACKGROUND && currentForegroundPackageName != null)
            	{
            		long elapsedTime = e.getTimeStamp() - currentForegroundStartTime;
            		usageEvents.add("AppUsage:" + currentForegroundPackageName + " started at " + dateFormat.format(currentForegroundStartTime) + ", Active for " + elapsedTime + " milisec.");
            		currentForegroundPackageName = null;
            	}
            	
            	/*String timestamp = dateFormat.format(e.getTimeStamp());
            	String eventType = e.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND ? "Foreground" : "Background";
            	usageEvents.add("Event: " + e.getPackageName() + "\t\t" + eventType + "\t\t" +  timestamp);*/
            }
        }
        
        if(currentForegroundPackageName != null)
        {
        	long elapsedTime = Calendar.getInstance().getTimeInMillis() - currentForegroundStartTime;
    		usageEvents.add("AppUsage:" + currentForegroundPackageName + " started at " + dateFormat.format(currentForegroundStartTime) + " Active for " + elapsedTime + " milisec.");
        }
        
        return usageEvents;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
