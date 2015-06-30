package com.d.utility;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.d.httpmodule.CanonicalGY;
import com.d.localdb.AppUsageRecord;
import com.d.localdb.BatteryRecord;
import com.d.localdb.CPURecord;
import com.d.localdb.DataUsageRecord;
import com.d.localdb.LocalDB;
import com.d.localdb.LocationLogRecord;
import com.d.localdb.MemoryRecord;
import com.d.localdb.Record;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AlarmReceiver extends BroadcastReceiver {
    
    public static String getDeviceId(Context context) {
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
        return deviceId;
    }

    public static class Bulk {
        public Bulk(String deviceId, List<Record> data) {
            this.deviceId = deviceId;
            this.data = data;
        }
        public String deviceId;
        public List<Record> data;
    }

    public static String URL = "http://52.11.1.59:8080";
    
    public static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    

    @Override
    public void onReceive(Context context, Intent intent) {
        final LocalDB locationDB = new LocalDB(context, LocationLogRecord.TABLE);
        final LocalDB appUsageDB = new LocalDB(context, AppUsageRecord.TABLE);
        final LocalDB batteryDB = new LocalDB(context, BatteryRecord.TABLE);
        final LocalDB memoryDB = new LocalDB(context, MemoryRecord.TABLE);
        final LocalDB cpuDB = new LocalDB(context, CPURecord.TABLE);
        final LocalDB dataDB = new LocalDB(context, DataUsageRecord.TABLE);
        final LocalDB sentDataDB = new LocalDB(context, SentDateRecord.TABLE);
        final List<SentDateRecord> recent = sentDataDB.getAll(null, null, null, true, 1);
        
        final Date recentUpload;
        if (!recent.isEmpty()) {
            recentUpload = recent.get(0).recentSentDate;
        } else
            recentUpload = null;

        final String deviceId = getDeviceId(context);
        
        final CanonicalGY gy = new CanonicalGY(context);
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    gy.POST(URL + "/location").withJson(gson.toJson(new Bulk(deviceId, locationDB.getAll(null, recentUpload, null, false, null)))).send();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    gy.POST(URL + "/appUsage").withJson(gson.toJson(new Bulk(deviceId, appUsageDB.getAll(null, recentUpload, null, false, null)))).send();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    gy.POST(URL + "/battery").withJson(gson.toJson(new Bulk(deviceId, batteryDB.getAll(null, recentUpload, null, false, null)))).send();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    gy.POST(URL + "/memory").withJson(gson.toJson(new Bulk(deviceId, memoryDB.getAll(null, recentUpload, null, false, null)))).send();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    gy.POST(URL + "/cpu").withJson(gson.toJson(new Bulk(deviceId, cpuDB.getAll(null, recentUpload, null, false, null)))).send();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    gy.POST(URL + "/data").withJson(gson.toJson(new Bulk(deviceId, dataDB.getAll(null, recentUpload, null, false, null)))).send();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
            }
        }).start();
        sentDataDB.addRecord(new SentDateRecord(Calendar.getInstance().getTime()));
    }

    public static void setAlarm(Context context, long intervalMillis) {
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("onetime", Boolean.FALSE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), intervalMillis, pi);
    }
}
