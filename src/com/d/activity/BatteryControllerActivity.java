package com.d.activity;

import java.io.IOException;
import java.util.List;

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
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.d.localdb.AppUsageRecord;
import com.d.localdb.BatteryRecord;
import com.d.localdb.LocalDB;
import com.d.utility.BatteryInfoCollector;

public class BatteryControllerActivity extends MyWebActivity {

	BatteryInfoCollector bctr;
	private TextView tv;
	private Handler handler;
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_battery);
		ldb = new LocalDB(getBaseContext(), BatteryRecord.TABLE);
		yaxisName = "Battery (%)";
		
		handler = new Handler();
		ldb = new LocalDB(getBaseContext(), BatteryRecord.TABLE);
		
		TabHost tabhost = (TabHost) findViewById(android.R.id.tabhost);
	    tabhost.setup();
	    TabSpec ts = tabhost.newTabSpec("tag1"); 
	    ts.setContent(R.id.graph);
	    ts.setIndicator("Capacity graph");
	    tabhost.addTab(ts);

	    ts = tabhost.newTabSpec("tag2"); 
	    ts.setContent(R.id.present);
	    ts.setIndicator("Present state");  
	    tabhost.addTab(ts);
		
		tv = (TextView) findViewById(R.id.text);
		webview =  (WebView) findViewById(R.id.webview);
	
		try {
			webview.loadDataWithBaseURL("file:///android_asset/",
					getAssetAsString("html/area.html"),
					"text/html; charset=utf-8", null, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setDomStorageEnabled(true);
		webview.getSettings().setLoadWithOverviewMode(true);
		webview.addJavascriptInterface(new JSInterface(), "Android");
		

		handler.post(new Runnable() {

			@Override
			public void run() {
				List<BatteryRecord> elements = ldb.getAll(null, null, null, true, 1);

				String batteryInfoMessage = "";
				for (BatteryRecord record : elements) {								
					batteryInfoMessage += "Battery Voltage : " + record.voltage + "mV\n";
					batteryInfoMessage += "Battery Level : " + record.level + "\n";
					batteryInfoMessage += "Battery Scale : " + record.scale + "\n";
					batteryInfoMessage += "Battery Temperature : " + record.temperature + "¢ªC\n";
					batteryInfoMessage += "Battery Plug Type : " + record.plugType + "\n";
					batteryInfoMessage += "Battery Health Type : " + record.healthType + "\n";
					batteryInfoMessage += "Battery Capacity : " + record.capacity + "%\n";
					
				}
				tv.setText(batteryInfoMessage);
				tv.invalidate();

				handler.postDelayed(this, 5 * 1000); // set time here to refresh
			}
		});

		// setContentView(tv);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		handler.removeMessages(0);
		ldb.close();
	}

}
