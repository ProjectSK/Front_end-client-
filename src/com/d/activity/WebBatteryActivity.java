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
import com.d.localdb.BatteryRecord;
import com.d.localdb.LocalDB;
import com.google.gson.Gson;

public class WebBatteryActivity extends Activity {
   
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
        public String date;
        public float percentage;
        public float temperature;
    }
    public static class Information {
        public String yaxisDesc;
        Information(){
        	yaxisDesc = yaxisName;
        }
        ArrayList<GraphRow> data = new ArrayList<WebBatteryActivity.GraphRow>();
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
    protected LocalDB ldb;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
     
       
		super.onCreate(savedInstanceState);
		
	}
	
	protected Information getInformation() {
        Information info = new Information();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        List<BatteryRecord> records = ldb.getAll(null, cal.getTime(), null, true, 20000);
        ArrayList<GraphRow> data = new ArrayList<WebBatteryActivity.GraphRow>(records.size());
        for (BatteryRecord record : records) {
            GraphRow row = new GraphRow();
            row.date = dateFormat.format(record.time);
            row.percentage = (float)record.capacity;
            row.temperature = (float)record.temperature;
            data.add(row);
        }
        info.data = data;
        return info;
	}
}
