package com.d.utility;

import java.util.List;

import com.d.localdb.*;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class ServiceClass extends Service {

	private BatteryInfoCollector bctr;
	private AppUsageCollector auc;
	private LocationCollector lc;
	private MemoryUsageCollector muc;

	LocalDB ldb_usage, ldb_loc, ldb_bat, ldb_mem;
	Handler handler;

	// DEBUG
	long interval = 1000;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private BroadcastReceiver bcr = new BroadcastReceiver() {
		int count = 0;

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			count++;
			if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
				bctr.update(intent);
				interval = bctr.batteryCalculator();
			}
			if (action.equals(Intent.ACTION_BATTERY_LOW)) {
				bctr.update(intent);
				interval = bctr.batteryCalculator();
			}
			if (action.equals(Intent.ACTION_BATTERY_OKAY)) {
				bctr.update(intent);
				interval = bctr.batteryCalculator();
			}
			if (action.equals(Intent.ACTION_POWER_CONNECTED)) {
				bctr.update(intent);
				interval = bctr.batteryCalculator();
			}
			if (action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
				bctr.update(intent);
				interval = bctr.batteryCalculator();
			}
			
		}
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		Log.d("slog", "onStartCommand()");
		bctr = new BatteryInfoCollector();
		auc = new AppUsageCollector(getBaseContext());
		lc = new LocationCollector(getBaseContext());
		muc = new MemoryUsageCollector(getBaseContext());

		handler = new Handler();
		handler.post(new Runnable() {
			@Override
			public void run() {
				Log.d("slog", "ServiceClass is running on AppUsageLog\n");
				ldb_usage = new LocalDB(getBaseContext(), AppUsageRecord.TABLE);
				try{
					
					List<AppUsageRecord> records = auc.getUsageRecords();
					for (AppUsageRecord record : records) {
						ldb_usage.addRecord(record);
					}
				}
				finally {
					ldb_usage.close();
				}
				
				ldb_loc = new LocalDB(getBaseContext(), LocationLogRecord.TABLE);
				try {
					LocationLogRecord record = lc.getLocation();
					if (record != null)
						ldb_loc.addRecord(record);
				} finally {
					ldb_loc.close();
				}
				ldb_mem = new LocalDB(getBaseContext(), MemoryRecord.TABLE);
				try {
					MemoryRecord record = muc.getRecord();
					Log.d("service",record.toString());
					if (record != null)
						ldb_mem.addRecord(record);
				} finally {
					ldb_mem.close();
				}
				
				ldb_bat = new LocalDB(getBaseContext(), BatteryRecord.TABLE);
				try {
					BatteryRecord record = bctr.getRecord();
					Log.d("batDB",record.toString());
					IntentFilter filter = new IntentFilter();
					filter.addAction(Intent.ACTION_BATTERY_CHANGED);
					filter.addAction(Intent.ACTION_BATTERY_LOW);
					filter.addAction(Intent.ACTION_BATTERY_OKAY);
					filter.addAction(Intent.ACTION_POWER_CONNECTED);
					filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
					registerReceiver(bcr, filter);
					if (record != null)
						ldb_bat.addRecord(record);
				} finally {
					ldb_bat.close();
				}
				
				handler.postDelayed(this, interval); // set time here to refresh
			}
		});

	
		return START_NOT_STICKY;

	}

	@Override
	public void onDestroy() {
		Log.d("slog", "onDestroy()");
		super.onDestroy();

		handler.removeMessages(0);
	}
}
