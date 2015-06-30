package com.d.utility;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class ServiceClass extends Service {

	private AppUsageCollector auc;
	private BatteryInfoCollector bic;
	private LocationCollector lc;
	private CpuUsageCollector cuc;
	private DataUsageCollector duc;
	private MemoryUsageCollector muc;
	private Handler handler;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * @see android.app.Service#onDestroy() 추가적으로 OnstartCommand에서 생성된 handler를
	 *      종료한다. background service를 종료하기 원한다면 반드시 호출해야한다.
	 */
	@Override
	public void onDestroy() {
		Log.d("slog", "onDestroy()");
		super.onDestroy();

		handler.removeMessages(0);
	}

	/**
	 * 내부에서 handler를 호출하여 데이터 수집을 위한 background service가 작동한다. background
	 * service를 종료하기 위해서는 본 class의 onDestroy()를 호출해야한다.
	 * 
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		Log.d("slog", "onStartCommand()");
		bic = new BatteryInfoCollector(getBaseContext());
		auc = new AppUsageCollector(getBaseContext());
		lc = new LocationCollector(getBaseContext());
		muc = new MemoryUsageCollector(getBaseContext());
		cuc = new CpuUsageCollector(getBaseContext());
		duc = new DataUsageCollector(getBaseContext());

		handler = new Handler();
		handler.post(new Runnable() {
			@Override
			public void run() {
				cuc.saveRecord();
				auc.saveRecords();
				lc.saveRecord();
				muc.saveRecord();
				duc.saveRecord();
				
				registerReceiver(bic.bcr, bic.filter);
				bic.saveRecord();
				handler.postDelayed(this, bic.batteryCalculator()); // set time
																	// here to
																	// refresh
			}
		});

		return START_NOT_STICKY;

	}
}
