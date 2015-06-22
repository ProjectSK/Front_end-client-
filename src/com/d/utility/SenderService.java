package com.d.utility;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SenderService extends Service {

    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AlarmReceiver.setAlarm(this.getBaseContext(), 60000);
        return Service.START_NOT_STICKY;
    }


}
