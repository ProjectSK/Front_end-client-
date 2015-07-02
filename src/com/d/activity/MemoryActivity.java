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

import com.d.api.MemoryUsage;
import com.d.localdb.MemoryRecord;
import com.google.gson.Gson;

public class MemoryActivity extends Activity {

	private static class GraphRow {
		public String date;
		public float percentageUsage;
	}

	private static class Information {
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

	protected SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss", Locale.getDefault());

	private Handler handler;

	private MemoryUsage mu;

	private TextView tv;

	private WebView webview;
	
	private Information getInformation() {
		Information info = new Information();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -1);
		List<MemoryRecord> records = mu.getRecords(20000);
		ArrayList<GraphRow> data = new ArrayList<GraphRow>(records.size());
		for (MemoryRecord record : records) {
			GraphRow row = new GraphRow();
			row.date = dateFormat.format(record.time);
			row.percentageUsage = (float) record.percentageOfMemoryUsage;
			data.add(row);
		}
		info.data = data;
		return info;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vis);
		mu = new MemoryUsage(getBaseContext());
		yaxisName = "Memory (%)";
		handler = new Handler();

		TabHost tabhost = (TabHost) findViewById(android.R.id.tabhost);
		tabhost.setup();
		TabSpec ts = tabhost.newTabSpec("tag1");
		ts.setContent(R.id.graph);
		ts.setIndicator("Memory graph");
		tabhost.addTab(ts);

		ts = tabhost.newTabSpec("tag2");
		ts.setContent(R.id.present);
		ts.setIndicator("Memory Logs");
		tabhost.addTab(ts);

		tv = (TextView) findViewById(R.id.text);
		webview = (WebView) findViewById(R.id.webview_graph);

		
		webview.loadUrl("file:///android_asset/html/memory.html");
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setDomStorageEnabled(true);
		webview.getSettings().setLoadWithOverviewMode(true);
		webview.addJavascriptInterface(new JSInterface(), "Android");

		handler.post(new Runnable() {

			@Override
			public void run() {
				List<MemoryRecord> elements = mu.getRecords(100);

				String output = "";
				for (MemoryRecord record : elements) {
					output += record.toString() + "\n";
				}
				
				tv.setText(output);
				tv.invalidate();

				handler.postDelayed(this, 5 * 1000); // set time here to refresh
			}
		});

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		handler.removeMessages(0);
		
	}
}
