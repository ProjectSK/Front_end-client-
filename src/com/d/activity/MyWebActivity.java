package com.d.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;

import com.d.localdb.AppUsageRecord;
import com.d.localdb.BatteryRecord;
import com.d.localdb.LocalDB;
import com.google.gson.Gson;

public class MyWebActivity extends Activity {
    
    LocalDB appUsageDB;
    /*
    public static class PackageData {
        public String packageName;
        public long minuiteSum;
    }

    public static class DailyData {
        public long minuiteSum;
        public HashMap<String, Long> durationPerPackage;
        public List<PackageData> top10;
    }
    
    public static class PackageDurationPair implements Comparable<PackageDurationPair> {
        public String packageName;
        public long minuiteSum;

        public PackageDurationPair(String packageName, long minuiteSum) {
            this.packageName = packageName;
            this.minuiteSum = minuiteSum;
        }
       
        @Override
        public int compareTo(PackageDurationPair another) {
            return Long.compare(this.minuiteSum, another.minuiteSum);
        }
    }

    public DailyData getDailyUsage() {
        Calendar curCal = (Calendar)Calendar.getInstance().clone();
        curCal.set(Calendar.MINUTE, 0);
        curCal.set(Calendar.SECOND, 0);
        curCal.set(Calendar.MILLISECOND, 0);
        
        Calendar startCal = (Calendar)curCal.clone();
        startCal.add(Calendar.HOUR_OF_DAY, -24);
        
        Calendar iterCal = (Calendar)curCal.clone();
        // extract TOP 10
        // (package name, duration) list, summation 
        HashMap<String, Long> durationPerPackage = new HashMap<String, Long>();
        long durationSum = 0;
        for (int idx = 0; idx < 24; idx++) {
            Date from = iterCal.getTime();
            Calendar nextCal = (Calendar)iterCal.clone();
            nextCal.add(Calendar.HOUR_OF_DAY, 1);
            Date until = nextCal.getTime();

            List<AppUsageRecord> recordList = appUsageDB.getAll(null, from, until, true, null);
            for (AppUsageRecord record: recordList) {
                long minuite = (record.elapsedTime / 1000L + 30L) / 60L;
                if (minuite <= 0)
                    continue;
                durationSum += minuite;
                Long old = durationPerPackage.get(record.packageName);
                if (old == null) {
                    durationPerPackage.put(record.packageName, 0L);
                    old = 0L;
                }
                durationPerPackage.put(record.packageName, old + minuite);
            }
            iterCal.add(Calendar.HOUR_OF_DAY, -1);
        }

        ArrayList<PackageDurationPair> pairs = new ArrayList<MyWebActivity.PackageDurationPair>();
        for (Entry<String, Long> entry : durationPerPackage.entrySet()) {
            pairs.add(new PackageDurationPair(entry.getKey(), entry.getValue()));
        }
        Collections.sort(pairs);
        ArrayList<PackageDurationPair> top10 = new ArrayList<MyWebActivity.PackageDurationPair>(10);
        AppUsageRecord primaryKey = new AppUsageRecord();
        for (int idx = pairs.size() - 1; idx >= pairs.size() - 10; idx--) {
            PackageDurationPair pair = pairs.get(idx);
            PackageData data = new PackageData();
            data.packageName = pair.packageName;
            data.minuiteSum = pair.minuiteSum;

            primaryKey.packageName = pair.packageName;
            List<AppUsageRecord> unchunkedList = appUsageDB.getAll(primaryKey, startCal.getTime(), curCal.getTime(), false, null);
            for (AppUsageRecord record : unchunkedList) {
            }
        }
        
        
        // Do again
        return null;
    }
    */

    public String getAssetAsString(String path) throws IOException {
        StringBuilder buf = new StringBuilder();
        InputStream json;
        json = getAssets().open(path);
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String str;
        while ((str=in.readLine()) != null) {
          buf.append(str);
        }
        in.close();
        return buf.toString();
    }
    
    public static class GraphRow {
        public String date;
        public float percentage;
    }
    public static class Information {
        public String yaxisDesc = "Battery (%)";
        ArrayList<GraphRow> data = new ArrayList<MyWebActivity.GraphRow>();
    }

    protected SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    
    public class JSInterface {
        Information info;
        public JSInterface () {
            info = getInformation();
        }

        @JavascriptInterface
        public String info() {
            return new Gson().toJson(info);
        }
    }

    WebView webview;
    LocalDB batteryLocalDB;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        appUsageDB = new LocalDB(getBaseContext(), AppUsageRecord.TABLE);
        batteryLocalDB = new LocalDB(getBaseContext(), BatteryRecord.TABLE);

		super.onCreate(savedInstanceState);
		webview = new WebView(this);
		setContentView(webview, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		try {
            webview.loadDataWithBaseURL("file:///android_asset/", getAssetAsString("html/area.html"), "text/html; charset=utf-8", null, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setDomStorageEnabled(true);
		webview.getSettings().setLoadWithOverviewMode(true);
		webview.addJavascriptInterface(new JSInterface(), "Android");

		/*
		webview.getSettings().setUseWideViewPort(true);
		webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		webview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });
        */
		setContentView(webview);
	}
	
	protected Information getInformation() {
        Information info = new Information();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        List<BatteryRecord> records = batteryLocalDB.getAll(null, cal.getTime(), null, true, 20000);
        ArrayList<GraphRow> data = new ArrayList<MyWebActivity.GraphRow>(records.size());
        for (BatteryRecord record : records) {
            GraphRow row = new GraphRow();
            row.date = dateFormat.format(record.time);
            row.percentage = (float)record.capacity;
            data.add(row);
        }
        info.data = data;
        return info;
	}
}
