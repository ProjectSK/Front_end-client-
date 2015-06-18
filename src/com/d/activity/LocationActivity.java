package com.d.activity;

import java.util.List;

import com.d.localdb.Localdb;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class LocationActivity extends Activity {

	// CollectorMain collector;
		Localdb ldb_loc;
		private TextView tv;
		Handler handler;

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_app_usage);
			ldb_loc = new Localdb(getBaseContext(), "usage");
			tv = new TextView(this);
			handler = new Handler();
			String output = "";
			tv.setText(output);
			setContentView(tv);
			handler.post(new Runnable() {

				@Override
				public void run() {
					
					List<String> elements = ldb_loc.getAlls();
					
					String output = "";
					for (int i = 0; i < elements.size(); i++) {
						output += elements.get(i) + "\n";
					}
					tv.setText(output);
					setContentView(tv);
					tv.setMovementMethod(new ScrollingMovementMethod());

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
