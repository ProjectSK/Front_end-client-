package com.d.activity;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.d.localdb.AppUsageRecord;
import com.d.localdb.LocalDB;

public class AppUsageActivity extends Activity {
	// CollectorMain collector;
	LocalDB ldb_usage;
	private TextView tv;
	private  Handler handler;
	private int display_num;

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
                List<AppUsageRecord> elements = ldb_usage.getAll(null, null, null, true, 100);
				
				String output = "";
				for (AppUsageRecord record : elements) {
				    output += record.packageName;
				    output += " ";
				    output += record.startTime;
				    output += " ";
				    output += record.elapsedTime;
					output+= "\n";
				}
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

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
