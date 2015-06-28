package com.d.activity;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.d.api.AppUsage;
import com.d.localdb.AppUsageRecord;
import com.d.localdb.LocalDB;

public class AppUsageActivity extends Activity {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"HH:mm:ss", Locale.getDefault());
	private int display_num;
	private Handler handler;
	// CollectorMain collector;
	private AppUsage au;
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

		au = new AppUsage(getBaseContext());
		handler.post(new Runnable() {

			@Override
			public void run() {
				List<AppUsage.Resource> stats = au.getStaticInfos();
				List<AppUsageRecord> records = au.getRecords(100);

				String output = "";

				output += "Package Name, Overall Time(s), Number of Execution\n";
				for (AppUsage.Resource res : stats) {
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

				handler.postDelayed(this, 500); // set time here to refresh
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
