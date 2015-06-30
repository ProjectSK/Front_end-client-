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
import android.view.ViewGroup.LayoutParams;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.d.api.AppUsage;
import com.d.localdb.AppUsageRecord;
import com.google.gson.Gson;

public class WebAppUsageActivity extends Activity {
   
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
    
    public static String yaxisName;
    public static class GraphRow {
        public String startTime;
        public Long elapsedTime;
        public String name;
    }
    public static class Information {
        public String yaxisDesc;
        Information(){
        	yaxisDesc = yaxisName;
        }
        ArrayList<GraphRow> data = new ArrayList<WebAppUsageActivity.GraphRow>();
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

    protected WebView webview;
    protected AppUsage au;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
     
       
		super.onCreate(savedInstanceState);
		
	}
	
	protected Information getInformation() {
        Information info = new Information();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        au = new AppUsage(getBaseContext());
        
        List<AppUsageRecord> records = au.getRecords(20000);
        
        ArrayList<GraphRow> data = new ArrayList<WebAppUsageActivity.GraphRow>(records.size());
        for (AppUsageRecord record : records) {
            GraphRow row = new GraphRow();
            row.startTime = dateFormat.format(record.startTime);
            row.elapsedTime = record.elapsedTime;
            row.name = record.packageName;
            data.add(row);
        }
        info.data = data;
        return info;
	}
}
