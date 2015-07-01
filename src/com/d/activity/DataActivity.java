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

import com.d.api.DataUsage;
import com.d.localdb.DataUsageRecord;
import com.google.gson.Gson;

public class DataActivity extends Activity {

	public static class GraphRow {
		public String date;
		public Long recdata;
		public Long transdata;
	}
	public static class Information {
		ArrayList<GraphRow> data = new ArrayList<GraphRow>();

		public String yaxisDesc;

		Information() {
			yaxisDesc = yaxisName;
		}
	}
	public class JSInterface {
		Information info;

		public JSInterface() {
			info = getInformation();
		}

		@JavascriptInterface
		public String info() {
			return new Gson().toJson(info);
		}
	}

	public static String yaxisName;

	protected SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss", Locale.getDefault());

	private DataUsage du;

	private Handler handler;
	
	private TextView tv;

	protected WebView webview;
	public String getAssetAsString(String path) throws IOException {
		StringBuilder buf = new StringBuilder();
		InputStream json;
		json = getAssets().open(path);
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String str;
		while ((str = in.readLine()) != null) {
			buf.append(str);
		}
		in.close();
		return buf.toString();
	}

	protected Information getInformation() {
		Information info = new Information();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -1);
		List<DataUsageRecord> records = du.getRecords(20000);
		ArrayList<GraphRow> data = new ArrayList<GraphRow>(records.size());
		for (DataUsageRecord record : records) {
			GraphRow row = new GraphRow();
			row.date = dateFormat.format(record.time);
			row.transdata = record.trans_data;
			row.recdata = record.rec_data;
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
		du = new DataUsage(getBaseContext());
		yaxisName = "Data";
		handler = new Handler();

		TabHost tabhost = (TabHost) findViewById(android.R.id.tabhost);
		tabhost.setup();
		TabSpec ts = tabhost.newTabSpec("tag1");
		ts.setContent(R.id.graph);
		ts.setIndicator("Data Usage graph");
		tabhost.addTab(ts);

		ts = tabhost.newTabSpec("tag2");
		ts.setContent(R.id.present);
		ts.setIndicator("Data Usage Logs");
		tabhost.addTab(ts);

		tv = (TextView) findViewById(R.id.text);
		webview = (WebView) findViewById(R.id.webview_graph);

		/*
		 * try { webview.loadDataWithBaseURL("file:///android_asset/",
		 * getAssetAsString("html/memory.html"), "text/html; charset=utf-8",
		 * null, null); } catch (IOException e) { e.printStackTrace(); }
		 */
		webview.loadUrl("file:///android_asset/html/data.html");
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setDomStorageEnabled(true);
		webview.getSettings().setLoadWithOverviewMode(true);
		webview.addJavascriptInterface(new JSInterface(), "Android");

		handler.post(new Runnable() {

			@Override
			public void run() {
				List<DataUsageRecord> elements = du.getRecords(100);

				String output = "";
				for (DataUsageRecord record : elements) {
					output += record.toString() + "\n";
				}
				tv.setText(output);
				tv.invalidate();

				handler.postDelayed(this, 2000); 
			}
		});

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		handler.removeMessages(0);
	}

}
