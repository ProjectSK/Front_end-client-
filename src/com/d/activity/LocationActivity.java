package com.d.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.d.api.LocationTracer;
import com.d.localdb.LocalDB;
import com.d.localdb.LocationLogRecord;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class LocationActivity extends Activity {
	


	private static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"HH:mm:ss", Locale.getDefault());

	

	private GoogleMap googleMap;
	private Handler handler;
	private LocationTracer lt;

	
	private Handler mHandler;
	private TextView tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_location);
		TabHost tabhost = (TabHost) findViewById(android.R.id.tabhost);
		tabhost.setup();
		TabSpec ts = tabhost.newTabSpec("tag1");
		ts.setContent(R.id.loc_tab1);
		ts.setIndicator("map");
		tabhost.addTab(ts);

		ts = tabhost.newTabSpec("tag2");
		ts.setContent(R.id.loc_tab2);
		ts.setIndicator("log");
		tabhost.addTab(ts);

		tv = (TextView) findViewById(R.id.loc_textView1);
		handler = new Handler();
		String output = "";
		tv.setText(output);
		tv.setMovementMethod(new ScrollingMovementMethod());
		
		Button mapBtn = (Button) findViewById(R.id.map_btn);
		mapBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getBaseContext(),
						googleMapActivity.class);
				startActivity(intent);
			}
		});

		lt = new LocationTracer(getBaseContext());
		handler.post(new Runnable() {

			@Override
			public void run() {
				List<LocationLogRecord> elements = lt.getRecords(100);

				String output = "";
				for (LocationLogRecord record : elements) {
					output += " ( ";
					output += record.latitude;
					output += " , ";
					output += record.longitude;
					output += " ) , ";
					output += dateFormat.format(record.time);
					output += "\n";

					// Log.d("print",output);
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
