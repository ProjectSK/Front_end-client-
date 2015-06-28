package com.d.activity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

import com.d.activity.MyWebActivity.JSInterface;
import com.d.localdb.AppUsageRecord;
import com.d.localdb.LocalDB;

public class AppUsageActivity extends MyWebActivity {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"HH:mm:ss", Locale.getDefault());
	private int display_num;
	private Handler handler;
	// CollectorMain collector;
	LocalDB ldb_usage;
	private TextView tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_usage);
		tv = new TextView(this);
		handler = new Handler();
		String output = "";
		tv.setText(output);
		setContentView(tv);
		tv.setMovementMethod(new ScrollingMovementMethod());

		ldb_usage = new LocalDB(getBaseContext(), AppUsageRecord.TABLE);
		handler.post(new Runnable() {

			@Override
			public void run() {
				List<AppUsageRecord> elements = ldb_usage.getAll(null, null,
						null, true, 100);

				String output = "";
				for (AppUsageRecord record : elements) {
					output += record.packageName;
					output += ", ";
					output += dateFormat.format(record.startTime);
					output += ", ";
					output += record.elapsedTime;
					output += "\n";

					// Log.d("print",output);
				}
				tv.setText(output);
				tv.invalidate();

				handler.postDelayed(this, 500); // set time here to refresh
			}
		});
		webview =  (WebView) findViewById(R.id.webview_appUsage);
		
		try {
			webview.loadDataWithBaseURL("file:///android_asset/",
					getAssetAsString("html/battery.html"),
					"text/html; charset=utf-8", null, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setDomStorageEnabled(true);
		webview.getSettings().setLoadWithOverviewMode(true);
		webview.addJavascriptInterface(new JSInterface(), "Android");

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
