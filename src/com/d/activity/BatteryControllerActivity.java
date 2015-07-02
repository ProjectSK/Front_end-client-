package com.d.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.d.api.BatteryUsage;
import com.d.localdb.BatteryRecord;
import com.google.gson.Gson;

public class BatteryControllerActivity extends Activity {

	private static class GraphRow {
		public String date;
		public float percentage;
		public float temperature;
	}
	/**
	 * 자바스크립트에서 그래프를 그릴 때 참조할 Record들과 y축의 이름을 전달하는 Container 
	 * @author Jun
	 */
	public static class Information {
		ArrayList<GraphRow> data = new ArrayList<GraphRow>();

		public String yaxisDesc;

		Information() {
			yaxisDesc = yaxisName;
		}
	}

	private class JSInterface {
		Information info;

		public JSInterface() {
			info = getInformation();
		}

		@JavascriptInterface
		public String info() {
			return new Gson().toJson(info);
		}
	}

	private static String yaxisName;

	private BatteryUsage bu;

	protected SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss", Locale.getDefault());

	private Handler handler;


	private TextView tv;

	private WebView webview;

	protected Information getInformation() {
		Information info = new Information();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -1);
		List<BatteryRecord> records = bu.getRecords(20000);
		ArrayList<GraphRow> data = new ArrayList<GraphRow>(records.size());
		for (BatteryRecord record : records) {
			GraphRow row = new GraphRow();
			row.date = dateFormat.format(record.time);
			row.percentage = (float) record.capacity;
			row.temperature = (float) record.temperature;
			data.add(row);
		}
		info.data = data;
		return info;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		yaxisName = "Battery (%)";
		handler = new Handler();
		bu = new BatteryUsage(getBaseContext());

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
		webview = (WebView) findViewById(R.id.webview_graph);

		webview.loadUrl("file:///android_asset/html/battery.html");
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setDomStorageEnabled(true);
		webview.getSettings().setLoadWithOverviewMode(true);
		webview.addJavascriptInterface(new JSInterface(), "Android");

		handler.post(new Runnable() {

			@Override
			public void run() {

				
				BatteryRecord record = bu.getSingleRecord();
				String batteryInfoMessage = "";
				if(record!= null){
					batteryInfoMessage += "Battery Voltage : " + record.voltage
							+ "mV\n";
					batteryInfoMessage += "Battery Level : " + record.level
							+ "\n";
					batteryInfoMessage += "Battery Scale : " + record.scale
							+ "\n";
					batteryInfoMessage += "Battery Temperature : "
							+ record.temperature + "°C\n";
					batteryInfoMessage += "Battery Plug Type : "
							+ record.plugType + "\n";
					batteryInfoMessage += "Battery Health Type : "
							+ record.healthType + "\n";
					batteryInfoMessage += "Battery Capacity : "
							+ record.capacity + "%\n";

				}

				tv.setText(batteryInfoMessage);
				tv.invalidate();
				handler.postDelayed(this, 3 * 1000); 
			}
		});

		// setContentView(tv);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		handler.removeMessages(0);
	}

}
