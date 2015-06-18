package com.d.activity;

import java.util.List;

import com.d.localdb.Localdb;
import com.d.utility.*;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AppUsageActivity extends Activity {
	// CollectorMain collector;
	Localdb ldb_usage;
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
		handler.post(new Runnable() {

			@Override
			public void run() {

				ldb_usage = new Localdb(getBaseContext(), "usage");
				List<String> elements = ldb_usage.getAlls();
				
				
				String output = "";
				for (int i = elements.size()-1; i >= elements.size() - 100 ; i--) {
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
