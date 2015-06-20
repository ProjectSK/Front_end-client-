package com.d.utility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.util.Log;

import com.d.localdb.AppUsageRecord;

public class AppUsageCollector{
	private final Context mContext;
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("M-d-yyyy HH:mm:ss");
    private static final String TAG = AppUsageCollector.class.getSimpleName();
	private UsageStatsManager usageStatsManager;
	
	private List<UsageStats> stats;
	
	private Calendar calendar;
	
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
		
		stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_YEARLY, startTime, endTime);
		
		Log.d(TAG, String.valueOf(stats.size()));
		return stats;
	}
	
	public List<AppUsageRecord> getUsageRecords()
	{
		UsageStatsManager usm = (UsageStatsManager) mContext.getSystemService("usagestats");
				
		long endTime = System.currentTimeMillis();
		long startTime = System.currentTimeMillis() - 60*100000;
		
		Log.d(TAG, "Range start:" + dateFormat.format(startTime) );
        Log.d(TAG, "Range end:" + dateFormat.format(endTime));
        
        UsageEvents osEventsLog = usm.queryEvents(startTime,endTime);
        calendar = Calendar.getInstance();
        
        List<AppUsageRecord> usageRecords = new Vector<AppUsageRecord>();
        
        while (osEventsLog.hasNextEvent()){
            UsageEvents.Event usageEvent = new UsageEvents.Event();
            osEventsLog.getNextEvent(usageEvent);

            if (usageEvent != null){
            	if(usageEvent.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND)
            	{
            		currentForegroundStartTime = usageEvent.getTimeStamp();
            		currentForegroundPackageName = usageEvent.getPackageName();
            	}
            	else if (usageEvent.getEventType() == UsageEvents.Event.MOVE_TO_BACKGROUND && currentForegroundPackageName != null)
            	{
            		long elapsedTime = usageEvent.getTimeStamp() - currentForegroundStartTime;
            		usageRecords.add(new AppUsageRecord(currentForegroundPackageName, new Date(currentForegroundStartTime), elapsedTime));
            		currentForegroundPackageName = null;
            	}
            }
        }
        
        if(currentForegroundPackageName != null)
        {
        	long elapsedTime = Calendar.getInstance().getTimeInMillis() - currentForegroundStartTime;
    		usageRecords.add(new AppUsageRecord(currentForegroundPackageName, new Date(currentForegroundStartTime), elapsedTime));
        }
        
        return usageRecords;
	}
}
