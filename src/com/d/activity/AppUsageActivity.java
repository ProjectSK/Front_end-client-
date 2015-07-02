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
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.d.api.AppUsage;
import com.d.localdb.AppUsageRecord;
import com.google.gson.Gson;

public class AppUsageActivity extends Activity {


	private static class GraphRow {
		public Long elapsedTime;
		public String name;
		public String startTime;
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
	private AppUsage au;
	protected SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
	private Handler handler;
	private TextView tv;
	protected WebView webview;

	/**
	 * Records와 Y축의 이름을 Information class에 저장하여 넘겨주는 method
	 * @return the list of Information classes
	 */
	protected Information getInformation() {
		Information info = new Information();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -1);
		au = new AppUsage(getBaseContext());

		List<AppUsageRecord> records = au.getRecords(20000);

		ArrayList<GraphRow> data = new ArrayList<GraphRow>(records.size());
		for (AppUsageRecord record : records) {
			GraphRow row = new GraphRow();
			row.startTime = dateFormat.format(record.startTime);
			row.elapsedTime = record.elapsedTime;
			row.name = record.packageName;
			data.add(row);
		}
		info.data = data;
		return info;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vis);
		yaxisName = "AppPackageName";
		handler = new Handler();

		au = new AppUsage(getBaseContext());
		TabHost tabhost = (TabHost) findViewById(android.R.id.tabhost);
		tabhost.setup();
		TabSpec ts = tabhost.newTabSpec("tag1");
		ts.setContent(R.id.graph);
		ts.setIndicator("App Usage Graph");
		tabhost.addTab(ts);

		ts = tabhost.newTabSpec("tag2");
		ts.setContent(R.id.present);
		ts.setIndicator("App Log");
		tabhost.addTab(ts);

		tv = (TextView) findViewById(R.id.text);
		webview = (WebView) findViewById(R.id.webview_graph);

		webview.loadUrl("file:///android_asset/html/appUsage.html");
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setDomStorageEnabled(true);
		webview.getSettings().setLoadWithOverviewMode(true);
		webview.addJavascriptInterface(new JSInterface(), "Android");

		tv.setMovementMethod(ScrollingMovementMethod.getInstance());

		handler.post(new Runnable() {

			@Override
			public void run() {
				List<AppUsage.StatisticalInfo> stats = au.getStatisticalInfos();
				List<AppUsageRecord> records = au.getRecords(100);

				String output = "";

				output += "Package Name, Overall Time(s), Number of Execution\n";
				for (AppUsage.StatisticalInfo res : stats) {
					output += res.PackageName + ", " + res.overallTime + ", "
							+ res.numberOfExecution + "\n";
				}

				output += "\nPackage Name, Start Time, Elapsed Time(ms)\n";
				for (AppUsageRecord record : records) {
					output += record.packageName;
					output += ", ";
					output += dateFormat.format(record.startTime);
					output += ", ";
					output += record.elapsedTime;
					output += "\n";
				}

				tv.setText(output);
				tv.invalidate();

				handler.postDelayed(this, 5 * 1000); // set time here to refresh
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		handler.removeMessages(0);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
