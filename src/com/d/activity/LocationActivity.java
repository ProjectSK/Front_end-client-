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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TabHost.TabSpec;

import com.d.localdb.AppUsageRecord;
import com.d.localdb.LocalDB;
import com.d.localdb.LocationLogRecord;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class LocationActivity extends Activity {
		public static enum Interval {
		    OneDay, ThirtyMin, ThreeHour
		}

		public class MapCtrl {
		    boolean isFirst = true;
		    public void draw(boolean move) {
		        Calendar curCalender = Calendar.getInstance();
		        Calendar prevCalendar = delCalender(Calendar.getInstance(), curInterval);
		        
		        ArrayList<LocationLogRecord> points = new ArrayList<LocationLogRecord>();
		        for (int idx = 0; idx < N_INTERVALS; idx++) {
                    List<LocationLogRecord> recordList = ldb_loc.getAll(null, prevCalendar.getTime(), curCalender.getTime(), true, 1);
                    if (!recordList.isEmpty()) {
                        points.add(recordList.get(0));
                    }
                    curCalender = prevCalendar;
                    prevCalendar = delCalender(prevCalendar, curInterval);

		        }
		        
		        for (int idx = 0; idx < points.size(); idx++) {
		            float oldRatio = (float)idx / points.size();
		            LocationLogRecord record = points.get(idx);
		            googleMap.addMarker(new MarkerOptions().position(new LatLng(record.latitude, record.longtitude)).alpha(1 - oldRatio * 0.95f));
		            if (idx > 0) {
		                googleMap.addPolyline(new PolylineOptions().add(
		                        new LatLng(points.get(idx - 1).latitude, points.get(idx - 1).longtitude), 
		                        new LatLng(record.latitude, record.longtitude)).color(Color.rgb(100, 40, 38)));
		            }
		            /*
		            googleMap.addCircle(
		                    new CircleOptions()
                            .center(new LatLng(record.latitude, record.longtitude))
                            .radius(100)
                            .strokeColor(Color.RED)
                            .fillColor(Color.rgb(120, 120, 120)));
		            */
		        }
		        if (move) {
		            if (!isFirst && points.size() > 1) {
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        for (int idx = 0; idx < points.size(); idx++) {
                            LocationLogRecord r = points.get(idx);
                            builder.include(new LatLng(r.latitude, r.longtitude));
                        }
                        LatLngBounds bnd = builder.build();

                        try {
                            CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bnd, 100);
                            googleMap.animateCamera(update);
                        } catch (IllegalStateException e) {
                            googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(points.get(0).latitude, points.get(0).longtitude)));
                        }
		            } else {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(points.get(0).latitude, points.get(0).longtitude)));
                        isFirst = false;
		            }
		        }
		    }

		    public void invalidate() {
		        googleMap.clear();
		    }
		}
		public static final int N_INTERVALS = 60;
		
		private static Calendar delCalender(Calendar calender, Interval itv) {
		    calender = (Calendar)calender.clone();
		    switch (itv) {
		    case OneDay:
		        calender.add(Calendar.MINUTE, -1440 / N_INTERVALS);
		        break;
		    case ThreeHour:
		        calender.add(Calendar.MINUTE, -180 / N_INTERVALS);
		        break;
		    case ThirtyMin:
		        // DEBUG
		        calender.add(Calendar.SECOND, -180 / N_INTERVALS);
		        break;
		    }
		    return calender;
		}
		
		private static Interval progressToInterval(int progress) {
		    switch (progress) {
		    case 0:
		        return Interval.OneDay;
		    case 1:
		        return Interval.ThreeHour;
		    case 2:
		        return Interval.ThirtyMin;
		    }
		    return null;
		}
		
		Interval curInterval = Interval.ThirtyMin;
		private GoogleMap googleMap;
		
		private LocalDB ldb_loc;
		MapCtrl mapCtrl = new MapCtrl();


		
		private Handler mHandler;
		
		private SeekBar resolutionSeekBar;
		TextView tv;
		private Handler handler;
		private static SimpleDateFormat dateFormat = new SimpleDateFormat(
				"HH:mm:ss", Locale.getDefault());

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
			Button mapBtn = (Button)findViewById(R.id.map_btn);
			mapBtn.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getBaseContext(), googleMapActivity.class);
					startActivity(intent);
				}
			});
			
			
			ldb_loc = new LocalDB(getBaseContext(), LocationLogRecord.TABLE);
			handler.post(new Runnable() {

				@Override
				public void run() {
					List<LocationLogRecord> elements = ldb_loc.getAll(null, null,
							null, true, 100);

					String output = "";
					for (LocationLogRecord record : elements) {
						output += " ( ";
						output += record.latitude;
						output += " , ";
						output += record.longtitude;
						output += " ) , ";
						output += dateFormat.format(record.time);
						output += "\n";

						// Log.d("print",output);
					}
					tv.setText(output);
					tv.invalidate();

					handler.postDelayed(this, 1000); // set time here to refresh
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
			if(ldb_loc != null)
				ldb_loc.close();
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
