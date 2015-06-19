package com.d.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;

import com.d.httpmodule.HttpConnect;
import com.d.localdb.AppUsageRecord;
import com.d.localdb.LocalDB;
import com.d.localdb.LocationLogRecord;
import com.d.utility.ServiceClass;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.view.View.*;
import android.widget.Button;
import android.os.Build;

public class MainActivity extends Activity {

	Button batteryControllerBotton, appUsageActivityBotton,locbtn, delbtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		
		batteryControllerBotton = (Button)findViewById(R.id.BatteryController);
		appUsageActivityBotton = (Button)findViewById(R.id.AppUsageActivity);
		locbtn = (Button)findViewById(R.id.LocationLog);
		delbtn = (Button)findViewById(R.id.deleteTables);
		
		
		Intent serviceIntent = new Intent(MainActivity.this, ServiceClass.class);
        startService(serviceIntent);
        Log.d("main", "onStart()");
		
		
		batteryControllerBotton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getBaseContext(), BatteryControllerActivity.class);
				startActivity(intent);
			}
		});
		appUsageActivityBotton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getBaseContext(), AppUsageActivity.class);
				startActivity(intent);
			}
		});
		locbtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getBaseContext(), LocationActivity.class);
				startActivity(intent);
			}
		});
		delbtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				LocalDB ldb_usage, ldb_loc;
				 ldb_usage = new LocalDB(getBaseContext(), new AppUsageRecord());
			     ldb_loc = new LocalDB(getBaseContext(), new LocationLogRecord());
			     ldb_usage.resetTable();
			     ldb_loc.resetTable();
			     ldb_loc.close();
			     ldb_usage.close();
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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
	private String sendData(JSONArray data){
		HttpConnect connect = new HttpConnect("52.11.1.59:3180/send",data);
		return connect.connect();
	}
	public void onDestroy() {   
	    super.onDestroy();
	    Intent serviceIntent = new Intent(MainActivity.this, ServiceClass.class);
        stopService(serviceIntent);
	    
	}

}

