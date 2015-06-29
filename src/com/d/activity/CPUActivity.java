package com.d.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

import com.d.activity.WebBatteryActivity.GraphRow;
import com.d.activity.WebBatteryActivity.Information;
import com.d.activity.WebBatteryActivity.JSInterface;
import com.d.localdb.BatteryRecord;
import com.d.localdb.CPURecord;
import com.d.localdb.LocalDB;
import com.d.localdb.MemoryRecord;
import com.d.utility.BatteryInfoCollector;
import com.d.utility.MemoryUsageCollector;

public class CPUActivity extends WebCPUActivity {


	MemoryUsageCollector muc;
	private TextView tv;
	private Handler handler;
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_battery);
		ldb = new LocalDB(getBaseContext(), MemoryRecord.TABLE);
		yaxisName = "CPU (%)";
		handler = new Handler();
		
		
		TabHost tabhost = (TabHost) findViewById(android.R.id.tabhost);
	    tabhost.setup();
	    TabSpec ts = tabhost.newTabSpec("tag1"); 
	    ts.setContent(R.id.graph);
	    ts.setIndicator("CPU graph");
	    tabhost.addTab(ts);

	    ts = tabhost.newTabSpec("tag2"); 
	    ts.setContent(R.id.present);
	    ts.setIndicator("CPU Logs");  
	    tabhost.addTab(ts);
		
		tv = (TextView) findViewById(R.id.text);
		webview =  (WebView) findViewById(R.id.webview_battery);
	
		try {
			webview.loadDataWithBaseURL("file:///android_asset/",
					getAssetAsString("html/cpu.html"),
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
				List<CPURecord> elements = ldb.getAll(null, null, null, true, 100);

				String output = "";
				for (CPURecord record : elements) {
					output += record.toString() + "\n";
				}
				//Log.d("CPUActivity", output);
				tv.setText(output);
				tv.invalidate();

				handler.postDelayed(this, 500); // set time here to refresh
			}
		});


	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		handler.removeMessages(0);
		ldb.close();
	}
	
	
}
