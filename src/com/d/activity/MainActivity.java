package com.d.activity;

import org.json.JSONArray;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.d.httpmodule.HttpConnect;
import com.d.localdb.AppUsageRecord;
import com.d.localdb.LocalDB;
import com.d.localdb.LocationLogRecord;
import com.d.utility.AlarmReceiver;
import com.d.utility.SenderService;
import com.d.utility.ServiceClass;

public class MainActivity extends Activity {

	Button batteryControllerBotton, appUsageActivityBotton,locbtn, delbtn;
	Button myWebViewBtn; 
	void permission_check(){
		Context context = getBaseContext();
		AppOpsManager appOps = (AppOpsManager) context
		        .getSystemService(Context.APP_OPS_SERVICE);
		boolean granted = appOps.checkOpNoThrow("android:get_usage_stats", 
		        android.os.Process.myUid(), context.getPackageName()) == AppOpsManager.MODE_ALLOWED;
		 Log.d("permission_check", "android:get_usage_stats() : " + granted);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		

		
		batteryControllerBotton = (Button)findViewById(R.id.BatteryController);
		appUsageActivityBotton = (Button)findViewById(R.id.AppUsageActivity);
		locbtn = (Button)findViewById(R.id.LocationLog);
		delbtn = (Button)findViewById(R.id.deleteTables);
		
		myWebViewBtn = (Button)findViewById(R.id.myWebViewBtn);
		
		permission_check();
		
		Intent serviceIntent = new Intent(MainActivity.this, ServiceClass.class);
        startService(serviceIntent);

        serviceIntent = new Intent(MainActivity.this, SenderService.class);
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
				ldb_usage = new LocalDB(getBaseContext(), AppUsageRecord.TABLE);
			    ldb_loc = new LocalDB(getBaseContext(), LocationLogRecord.TABLE);
			    ldb_usage.resetTable();
			    ldb_loc.resetTable();
			    ldb_loc.close();
			    ldb_usage.close();
			}
		});
		myWebViewBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyWebActivity.class);
                startActivity(intent);
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

