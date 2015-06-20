package com.d.activity;

import java.util.Calendar;
import java.util.List;

import com.d.localdb.LocalDB;
import com.d.localdb.LocationLogRecord;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class LocationActivity extends Activity {

	// CollectorMain collector;
		LocalDB ldb_loc;
		private TextView tv;
		Handler handler;

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_app_usage);
			ldb_loc = new LocalDB(getBaseContext(), LocationLogRecord.TABLE);
			tv = new TextView(this);
            tv.setMovementMethod(new ScrollingMovementMethod());
			handler = new Handler();
			String output = "";
			tv.setText(output);
			setContentView(tv);
			handler.post(new Runnable() {
				@Override
				public void run() {
				    Calendar stcal = Calendar.getInstance();
				    stcal.add(Calendar.SECOND, -20);
				    Calendar edcal = Calendar.getInstance();
				    edcal.add(Calendar.SECOND, -5);

					List<LocationLogRecord> elements = ldb_loc.getAll(null, stcal.getTime(), edcal.getTime(), true, 100);
					
					String output = "";
					for (LocationLogRecord record : elements) {
					    output += record.time;
					    output += " ";
					    output += record.latitude;
					    output += " ";
					    output += record.longtitude;
					    output += "\n";
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
