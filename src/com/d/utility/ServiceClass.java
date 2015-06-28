package com.d.utility;

import java.util.List;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.d.localdb.AppUsageRecord;
import com.d.localdb.BatteryRecord;
import com.d.localdb.CPURecord;
import com.d.localdb.LocalDB;
import com.d.localdb.LocationLogRecord;
import com.d.localdb.MemoryRecord;

public class ServiceClass extends Service {

	private AppUsageCollector auc;
	/**
	 * 배터리의 변화라는 event에 반응하여 battery 정보를 업데이트하는 receiver.
	 */
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
	private BatteryInfoCollector bctr;
	private Handler handler;

	// DEBUG
	private long interval = 1000;
	private LocationCollector lc;
	private CpuUsageCollector cuc;

	private LocalDB ldb_usage, ldb_loc, ldb_bat, ldb_mem, ldb_cpu;

	private MemoryUsageCollector muc;

	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * @see android.app.Service#onDestroy()
	 * 추가적으로 OnstartCommand에서 생성된 handler를 종료한다.
	 * background service를 종료하기 원한다면 반드시 호출해야한다.
	 */
	@Override
	public void onDestroy() {
		Log.d("slog", "onDestroy()");
		super.onDestroy();

		handler.removeMessages(0);
	}

	/**
	 * 내부에서 handler를 호출하여 데이터 수집을 위한 background service가 작동한다.
	 * background service를 종료하기 위해서는 본 class의 onDestroy()를 호출해야한다.
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		Log.d("slog", "onStartCommand()");
		bctr = new BatteryInfoCollector();
		auc = new AppUsageCollector(getBaseContext());
		lc = new LocationCollector(getBaseContext());
		muc = new MemoryUsageCollector(getBaseContext());
		cuc = new CpuUsageCollector();

		handler = new Handler();
		handler.post(new Runnable() {
			@Override
			public void run() {

		        new CpuUsageCollector().getRecord();
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

				ldb_cpu = new LocalDB(getBaseContext(), CPURecord.TABLE);
				try{
					
					CPURecord record = cuc.getRecord();
					if(record != null){
						ldb_cpu.addRecord(record);
					}
				}
				finally {
					ldb_cpu.close();
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
}
