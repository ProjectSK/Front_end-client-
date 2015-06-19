package com.d.utility;

import java.util.List;

import com.d.localdb.*;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;
 
public class ServiceClass extends Service{
	private CollectorMain collector;
	private BatteryInfoCollector bctr;
	private AppUsageCollector auc;
	private LocationCollector lc;
	LocalDB ldb_usage, ldb_loc, ldb_bat;
	Handler handler;
	
	long interval = 100;
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    private BroadcastReceiver bcr = new BroadcastReceiver() {
		int count = 0;

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			count++;
			if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
				bctr.update(intent);
				interval = bctr.batteryCalculator(); 
			}
			if (action.equals(Intent.ACTION_BATTERY_LOW)) {
				bctr.update(intent);
				interval = bctr.batteryCalculator();
			}
			if (action.equals(Intent.ACTION_BATTERY_OKAY)) {
				bctr.update(intent);
				interval = bctr.batteryCalculator();
			}
			if (action.equals(Intent.ACTION_POWER_CONNECTED)) {
				bctr.update(intent);
				interval = bctr.batteryCalculator();
			}
			if (action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
				bctr.update(intent);
				interval = bctr.batteryCalculator();
			}
		}
	};
     
    @Override
    public void onStart(Intent intent, int startId) {
        Log.d("slog", "onStart()");
        super.onStart(intent, startId);
        
        bctr = new BatteryInfoCollector();
        auc = new AppUsageCollector(getBaseContext());
        lc = new LocationCollector(getBaseContext());

 	   
        
        handler = new Handler();
        handler.post(new Runnable(){

               @Override
               public void run() {
            	   Log.d( "slog", "ServiceClass is running on AppUsageLog\n");

                   ldb_usage = new LocalDB(getBaseContext(),  new AppUsageRecord());
                   List<AppUsageRecord> records = auc.getUsageRecords();
                   for(int i = 0; i < records.size(); i++ ){
                	   ldb_usage.addElement(records.get(i));
                   }            	   
            	   ldb_usage.close();
            	  

                   handler.postDelayed(this, interval); // set time here to refresh

               }
           });
        handler = new Handler();
        
        handler.post(new Runnable(){

            @Override
            public void run() {
         	   Log.d( "slog", "ServiceClass is running on Location log\n");

         	  ldb_loc = new LocalDB(getBaseContext(), new LocationLogRecord());
                LocationLogRecord records = lc.getLocation();
                ldb_loc.addElement(records);           	   
                ldb_loc.close();
                handler.postDelayed(this, interval); // set time here to refresh

            }
        });
    }
    
    @Override
    public void onDestroy() { 
        Log.d("slog", "onDestroy()");
        super.onDestroy();
        
        handler.removeMessages(0);
    }
}