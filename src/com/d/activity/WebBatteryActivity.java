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


/**
 * 
 * @author HyunIlHarry
 * 기능 : BatteryControllerActivity의 그래프 부분을 담당하는 Activity. BatteryControllerActivity에서 상속하여 사용한다.
 *  
 *
 */
public class WebBatteryActivity extends Activity {
	/**
	    * @deprecated
	    * 사실상 쓰이지 않는 메소드. html파일을 읽어올 때 쓰려 했으니 굳이 쓸 필요가 없어서 안쓰고 있다.
	    * @param path
	    * @return
	    * @throws IOException
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

    /**
     * y축의 이름. 그래프에 표시됨
     */
    public static String yaxisName;
    /**
     * 
     * @author HyunIlHarry
     * 기능 : 그래프에 들어가는 정보들 (x축, y축, 그래프별 정보)
     */
    public static class GraphRow {
        public String date;
        public float percentage;
        public float temperature;
    }
    /**
     * 
     * @author HyunIlHarry
     * 기능 : asset(html 코드)에 넘길 정보를 담는 구조체이다.
     *
     */
    public static class Information {
        public String yaxisDesc;
        Information(){
        	yaxisDesc = yaxisName;
        }
        ArrayList<GraphRow> data = new ArrayList<WebBatteryActivity.GraphRow>();
    }
    protected SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    /**
     * 
     * @author HyunIlHarry
     * 기능 : 직접적으로 Information 클래스를 asset의 html코드에 넘기는 클래스
     * html에서는 Android.info()를 통해서 정보를 받아들인다
     *
     */
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
    LocalDB ldb;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
     
       
		super.onCreate(savedInstanceState);
		
	}
	/**
	 * LocalDB에서 BatteryRecord(battery관련 기록)를 긁어온다음 이를 GraphRow 클래스에 넣는 함수
	 * JSInterface 클래스 생성시 호출됨
	 * @return Information info
	 */
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
