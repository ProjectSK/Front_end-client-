package com.d.utility;

import java.io.ObjectInputStream.GetField;
import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;
import android.view.LayoutInflater.Filter;

import com.d.localdb.BatteryRecord;
import com.d.localdb.LocalDB;

public class BatteryInfoCollector {

	private BatteryManager batteryManager;

	public BroadcastReceiver bcr = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
				update(intent);
			}
			if (action.equals(Intent.ACTION_BATTERY_LOW)) {
				update(intent);
			}
			if (action.equals(Intent.ACTION_BATTERY_OKAY)) {
				update(intent);
			}
			if (action.equals(Intent.ACTION_POWER_CONNECTED)) {
				update(intent);
			}
			if (action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
				update(intent);
			}
		}
	};

	private long capacity;
	private long goaltime;
	private String healthType;
	private long level;
	private String plugType;
	private double previousRatio;
	private long previousState;
	private long previousTime;
	private long requestTime;
	private long scale;
	private String technology;
	private float temperature;
	private long voltage;
	private Context context;

	public IntentFilter filter;

	/**
	 * Device의 구동시간을 10시간을 기준으로 잡고 데이터 수집 주기를 조절한다. 해당시간을 조정하고 싶다면
	 * BatteryInfoCollector(int goaltime)을 이용한다.
	 */
	public BatteryInfoCollector(Context context) {
		batteryManager = new BatteryManager();
		goaltime = 60 * 10;
		previousTime = System.currentTimeMillis();
		previousRatio = 100 / (goaltime * 60 * 1000);
		previousState = 100;
		requestTime = 1000;
		this.context = context;
		setFilter();
	}

	/**
	 * Device의 구동시간을 주어신 시간(goaltime, 분 기준)을 기준으로 잡고 데이터 수집 주기를 조절한다.
	 * 
	 * @param goaltime
	 */
	public BatteryInfoCollector(Context context, int goaltime) {
		batteryManager = new BatteryManager();
		this.goaltime = 60 * 10;
		previousTime = System.currentTimeMillis();
		previousRatio = 100 / (goaltime * 60 * 1000);
		previousState = 0;
		requestTime = 5000;
		if (isGoalInRightRange(goaltime)) {
			this.goaltime = goaltime;
		}
		this.context = context;
		setFilter();
	}

	private void setFilter() {
		filter = new IntentFilter();
		filter.addAction(Intent.ACTION_BATTERY_CHANGED);
		filter.addAction(Intent.ACTION_BATTERY_LOW);
		filter.addAction(Intent.ACTION_BATTERY_OKAY);
		filter.addAction(Intent.ACTION_POWER_CONNECTED);
		filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
	}

	/**
	 * 생성자에서 정해진 '예상 device 구동 시간'(goaltime)을 기준으로 데이터 수집 주기를 계산한다. 기존의 주기에 베터리
	 * 소모 속도의 변화율에 곱한 값들의 지수이동평균을 다음 주기값으로 한다. 단위는 ms
	 * 
	 * @return 데이터 수집 주기(ms)
	 */
	public long batteryCalculator() {
		long presentTime = System.currentTimeMillis();
		double presentRatio;
		long presentState = capacity;
		double goalRatio = 100.0 / (goaltime * 60 * 1000);
		double alpha = 0.5;
		double rate = 1.0;
		
		/*
		 *  지수이동평균
		 */

		presentRatio = (double) (presentState - previousState)
				/ (presentTime - previousTime);
		presentRatio = (presentRatio * alpha + previousRatio * (1 - alpha));

		if (presentRatio != 0)
			rate = goalRatio / presentRatio;

		requestTime = (long) (requestTime * rate);
		previousRatio = presentRatio;
		previousTime = presentTime;
		previousState = presentState;

		requestTime = (requestTime < 1000) ? 1000 : requestTime;
		requestTime = (requestTime > 30000) ? 30000 : requestTime;

		return requestTime;

	}

	/**
	 * 파라미터 goaltime_minute로 주어진 '예상 device 구동 시간'(goaltime)을 기준으로 데이터 수집 주기를
	 * 계산한다. 기존의 주기에 베터리 소모 속도의 변화율에 곱한 값들의 지수이동평균을 다음 주기값으로 한다. 단위는 ms
	 * 
	 * @param goaltime_minute
	 *            '예상 device 구동 시간'(분, Minute)
	 * @return 데이터 수집 주기(ms)
	 */
	public long batteryCalculator(int goaltime_minute) {
		long presentTime = System.currentTimeMillis();
		double presentRatio;
		long presentState = capacity;
		double goalRatio = 100.0 / (goaltime_minute * 60 * 1000);
		double alpha = 0.5;
		double rate = 1.0;

		presentRatio = (double) (presentState - previousState)
				/ (presentTime - previousTime);
		presentRatio = (presentRatio * alpha + previousRatio * (1 - alpha));

		if (presentRatio != 0)
			rate = goalRatio / presentRatio;

		requestTime = (long) (requestTime * rate);
		previousRatio = presentRatio;
		previousTime = presentTime;
		previousState = presentState;

		return requestTime;
	}

	/**
	 * BatteryRecord type으로 본 class의 정보들을 저장하여 return한다. 최소한 한번의 update(intent)가
	 * 시행되어야 유의미한 record값을 얻을 수 있다.
	 * 
	 * @return BatteryRecord
	 */
	private BatteryRecord getRecord() {
		return new BatteryRecord(Calendar.getInstance().getTime(), capacity,
				level, scale, voltage, temperature, healthType, plugType);
	}

	private boolean isGoalInRightRange(int goaltime) {
		return 0 <= goaltime;
	}

	@Override
	public String toString() {

		String batteryInfoMessage = "";

		batteryInfoMessage = batteryInfoMessage + "Battery Voltage : "
				+ voltage + "mV\n";
		batteryInfoMessage = batteryInfoMessage + "Battery Level : " + level
				+ "\n";
		batteryInfoMessage = batteryInfoMessage + "Battery Scale : " + scale
				+ "\n";
		batteryInfoMessage = batteryInfoMessage + "Battery Temperature : "
				+ temperature + "˚C\n";
		batteryInfoMessage = batteryInfoMessage + "Battery Technology : "
				+ technology + "\n";
		batteryInfoMessage = batteryInfoMessage + "Battery Plug Type : "
				+ plugType + "\n";
		batteryInfoMessage = batteryInfoMessage + "Battery Health Type : "
				+ healthType + "\n";
		batteryInfoMessage = batteryInfoMessage + "Battery Capacity : "
				+ capacity + "%\n";
		batteryInfoMessage += "Request time : " + batteryCalculator() + "ms\n";

		return batteryInfoMessage;
	}

	public void saveRecord() {
		LocalDB ldb_bat = new LocalDB(context, BatteryRecord.TABLE);
		try {
			BatteryRecord record = getRecord();
			if (record != null)
				ldb_bat.addRecord(record);
		} finally {
			ldb_bat.close();
		}
	}

	/**
	 * 본 class의 정보를 update한다.
	 * 
	 * @param intent
	 *            battery 정보를 포함하는 intent가 필요하다. (eg. system intent)
	 */
	private void update(Intent intent) {
		int plug = intent.getIntExtra("plugged", 0);
		level = intent.getIntExtra("level", 0);
		scale = intent.getIntExtra("scale", 100);
		voltage = intent.getIntExtra("voltage", 0);
		temperature = intent.getIntExtra("temperature", 0) / 10.0f;
		technology = intent.getStringExtra("technology");
		int health = intent.getIntExtra("health",
				BatteryManager.BATTERY_HEALTH_UNKNOWN);

		switch (plug) {
		case 0:
			plugType = "Unplugged";
			break;
		case BatteryManager.BATTERY_PLUGGED_AC:
			plugType = "AC";
			break;
		case BatteryManager.BATTERY_PLUGGED_USB:
			plugType = "USB";
			break;
		case (BatteryManager.BATTERY_PLUGGED_AC | BatteryManager.BATTERY_PLUGGED_USB):
			plugType = "AC and USB";
			break;
		default:
			plugType = "Unknown";
			break;
		}

		if (health == BatteryManager.BATTERY_HEALTH_GOOD) {
			healthType = "Good";
		} else if (health == BatteryManager.BATTERY_HEALTH_OVERHEAT) {
			healthType = "OverHeat";
		} else if (health == BatteryManager.BATTERY_HEALTH_DEAD) {
			healthType = "Dead";
		} else if (health == BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE) {
			healthType = "Over voltage";
		} else if (health == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE) {
			healthType = "Unknown error";
		} else if (health == BatteryManager.BATTERY_HEALTH_COLD) {
			healthType = "Cold";
		} else {
			healthType = "Unknown";
		}

		capacity = batteryManager
				.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
		System.out.println(capacity);
	}
}
