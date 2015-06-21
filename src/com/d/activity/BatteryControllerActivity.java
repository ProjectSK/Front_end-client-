package com.d.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.tech.NfcF;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.TextView;

import com.d.utility.BatteryInfoCollector;

public class BatteryControllerActivity extends Activity {
	
	BatteryInfoCollector bctr;

	private PendingIntent mPendingIntent;
	private IntentFilter[] mFilters;
	private String[][] mTechLists;
	private TextView tv;

	private BroadcastReceiver bcr = new BroadcastReceiver() {
		int count = 0;

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			count++;
			if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
				onBatteryChanged(intent);
			}
			if (action.equals(Intent.ACTION_BATTERY_LOW)) {
				bctr.update(intent);
			}
			if (action.equals(Intent.ACTION_BATTERY_OKAY)) {
				bctr.update(intent);
			}
			if (action.equals(Intent.ACTION_POWER_CONNECTED)) {
				bctr.update(intent);
			}
			if (action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
				bctr.update(intent);
			}
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_BATTERY_CHANGED);
		filter.addAction(Intent.ACTION_BATTERY_LOW);
		filter.addAction(Intent.ACTION_BATTERY_OKAY);
		filter.addAction(Intent.ACTION_POWER_CONNECTED);
		filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
		registerReceiver(bcr, filter);
	}

	public void onBatteryChanged(Intent intent) {
		int plug, status, scale, level, ratio;
		String sPlug = "";
		String sStatus = "";

		if (intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false) == false) {
			// sStatus.setText("no battery");
			return;
		}
		bctr.update(intent);
		tv.setText(bctr.toString()+"\n");
		setContentView(tv);
		

	}
	
	
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_battery);
		if (savedInstanceState == null) {
			//TODO something
		}
		tv = new TextView(this);
		
		bctr = new BatteryInfoCollector();
		Intent targetIntent = new Intent(this, BatteryInfoCollector.class);		
		targetIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		mPendingIntent = PendingIntent.getActivity(this, 0, targetIntent, 0);
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_BATTERY_CHANGED);
		filter.addAction(Intent.ACTION_BATTERY_LOW);
		filter.addAction(Intent.ACTION_BATTERY_OKAY);
		filter.addAction(Intent.ACTION_POWER_CONNECTED);
		filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
		registerReceiver(bcr, filter);
		
		try {
			filter.addDataType("*/*");
		} catch (MalformedMimeTypeException e) {
			throw new RuntimeException("fail", e);
		}

		mFilters = new IntentFilter[] { filter, };

		mTechLists = new String[][] { new String[] { NfcF.class.getName() } };

		Intent passedIntent = getIntent();
		if (passedIntent != null) {
			String action = passedIntent.getAction();			
		}
		
	}

}
