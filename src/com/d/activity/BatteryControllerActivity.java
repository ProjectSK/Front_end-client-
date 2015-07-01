package com.d.activity;

import java.io.IOException;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.d.localdb.BatteryRecord;
import com.d.localdb.LocalDB;
import com.d.utility.BatteryInfoCollector;

public class BatteryControllerActivity extends WebBatteryActivity  {

	BatteryInfoCollector bic;
	private TextView tv;
	private Handler handler;
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		yaxisName = "Battery (%)";
		ldb = new LocalDB(getBaseContext(), BatteryRecord.TABLE);
		handler = new Handler();
		bic = new BatteryInfoCollector(getBaseContext());
		
		setContentView(R.layout.activity_vis);
		
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
		webview =  (WebView) findViewById(R.id.webview_graph);
	
		/*try {
			webview.loadDataWithBaseURL("file:///android_asset/",
					getAssetAsString("html/battery.html"),
					"text/html; charset=utf-8", null, null);
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		webview.loadUrl("file:///android_asset/html/battery.html");
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setDomStorageEnabled(true);
		webview.getSettings().setLoadWithOverviewMode(true);
		webview.addJavascriptInterface(new JSInterface(), "Android");
		

		handler.post(new Runnable() {

			@Override
			public void run() {

				registerReceiver(bic.bcr, bic.filter);
				List<BatteryRecord> elements = ldb.getAll(null, null, null, true, 1);
				
				String batteryInfoMessage = "";
				for (BatteryRecord record : elements) {								
					batteryInfoMessage += "Battery Voltage : " + record.voltage + "mV\n";
					batteryInfoMessage += "Battery Level : " + record.level + "\n";
					batteryInfoMessage += "Battery Scale : " + record.scale + "\n";
					batteryInfoMessage += "Battery Temperature : " + record.temperature + "��C\n";
					batteryInfoMessage += "Battery Plug Type : " + record.plugType + "\n";
					batteryInfoMessage += "Battery Health Type : " + record.healthType + "\n";
					batteryInfoMessage += "Battery Capacity : " + record.capacity + "%\n";
					
				}
				
				tv.setText(batteryInfoMessage);
				tv.invalidate();
				handler.postDelayed(this, bic.batteryCalculator()); // set time here to refresh
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
