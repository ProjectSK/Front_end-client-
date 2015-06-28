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
import com.d.localdb.LocalDB;

/**
 * 어플리케이션이 foreground로 얼마간 나왔는지를 확인하기 위한 class
 * 
 * @author vs223
 */
public class AppUsageCollector {
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"M-d-yyyy HH:mm:ss");

	private static final String TAG = AppUsageCollector.class.getSimpleName();
	private Calendar calendar;
	private String currentForegroundPackageName = null;
	private long currentForegroundStartTime;
	private final Context mContext;
	private List<UsageStats> stats;
	private UsageStatsManager usageStatsManager;
	private LocalDB locdb;

	/**
	 * @param context
	 *            정보를 추출하기위한 context, 특별한 의도가 없다면 getBaseContext()를 추천한다.
	 */
	public AppUsageCollector(Context context) {
		mContext = context;
		usageStatsManager = (UsageStatsManager) mContext
				.getSystemService("usagestats");
		calendar = Calendar.getInstance();
		getUsages();
	}

	/**
	 * 생성자에서 받은 Context에서 AppUsageEvents를 받아 AppUsageRecord의 record type들의 list를
	 * return하는 함수
	 * 
	 * @return List<AppUsageRecord>
	 */
	private List<AppUsageRecord> updateRecords() {
		UsageStatsManager usm = (UsageStatsManager) mContext
				.getSystemService("usagestats");

		long endTime = System.currentTimeMillis();
		long startTime = System.currentTimeMillis() - 100 * 60 * 1000;

		Log.d(TAG, "Range start:" + dateFormat.format(startTime));
		Log.d(TAG, "Range end:" + dateFormat.format(endTime));

		UsageEvents osEventsLog = usm.queryEvents(startTime, endTime);
		calendar = Calendar.getInstance();

		List<AppUsageRecord> usageRecords = new Vector<AppUsageRecord>();

		while (osEventsLog.hasNextEvent()) {
			UsageEvents.Event usageEvent = new UsageEvents.Event();
			osEventsLog.getNextEvent(usageEvent);

			if (usageEvent != null) {
				if (usageEvent.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
					currentForegroundStartTime = usageEvent.getTimeStamp();
					currentForegroundPackageName = usageEvent.getPackageName();
				} else if (usageEvent.getEventType() == UsageEvents.Event.MOVE_TO_BACKGROUND
						&& currentForegroundPackageName != null) {
					long elapsedTime = usageEvent.getTimeStamp()
							- currentForegroundStartTime;
					usageRecords.add(new AppUsageRecord(
							currentForegroundPackageName, new Date(
									currentForegroundStartTime), elapsedTime));
					currentForegroundPackageName = null;
				}
			}
		}

		if (currentForegroundPackageName != null) {
			long elapsedTime = Calendar.getInstance().getTimeInMillis()
					- currentForegroundStartTime;
			usageRecords.add(new AppUsageRecord(currentForegroundPackageName,
					new Date(currentForegroundStartTime), elapsedTime));
		}

		return usageRecords;
	}

	public void saveRecords() {
		locdb = new LocalDB(mContext, AppUsageRecord.TABLE);
		try {
			List<AppUsageRecord> records = updateRecords();
			for (AppUsageRecord record : records) {
				locdb.addRecord(record);
			}
		} finally {
			locdb.close();
		}
	}

	private List<UsageStats> getUsages() {
		Calendar endCal = calendar;
		Calendar startCal = calendar;
		endCal.add(Calendar.YEAR, 1);
		long endTime = calendar.getTimeInMillis();
		startCal.add(Calendar.YEAR, -2);
		long startTime = calendar.getTimeInMillis();

		stats = usageStatsManager.queryUsageStats(
				UsageStatsManager.INTERVAL_YEARLY, startTime, endTime);

		Log.d(TAG, "stat" + String.valueOf(stats.size()));
		return stats;
	}
}
