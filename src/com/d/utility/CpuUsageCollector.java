package com.d.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;

import com.d.activity.R.id;
import com.d.localdb.CPURecord;
import com.d.localdb.MemoryRecord;

import android.util.Log;

public class CpuUsageCollector {
	
	public Long user;
	public Long system;
	public Long idle;
	public Long other;
	
	public CpuUsageCollector() {

		long[] update = getCpuUsageStatistic();
		if(update.length == 4){
			user = update[0];
			system = update[1];
			idle = update[2];
			other = update[3];
		}
		else {
			user = 0l;
			system = 0l;
			idle = 0l;
			other = 0l;
		}
	}
	
	
	/**
	 * 
	 * @return integer Array with 4 elements: user, system, idle and other cpu
	 *         usage in percentage.
	 */
	private long[] getCpuUsageStatistic() {
		String tempString = executeTop();
		Log.d("CPU", tempString);
		tempString = tempString.replaceAll(",", "");
		tempString = tempString.replaceAll("User", "");
		tempString = tempString.replaceAll("System", "");
		tempString = tempString.replaceAll("IOW", "");
		tempString = tempString.replaceAll("IRQ", "");
		tempString = tempString.replaceAll("%", "");
		for (int i = 0; i < 10; i++) {
			tempString = tempString.replaceAll("  ", " ");
		}

		tempString = tempString.trim();
		String[] myString = tempString.split(" ");
		long[] cpuUsageAsInt = new long[myString.length];
		for (int i = 0; i < myString.length; i++) {
			myString[i] = myString[i].trim();
			cpuUsageAsInt[i] = Integer.parseInt(myString[i]);
		}

		return cpuUsageAsInt;
	}

	private String executeTop() {
		java.lang.Process p = null;
		BufferedReader in = null;
		String returnString = null;
		try {
			p = Runtime.getRuntime().exec("top -n 1");
			in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while (returnString == null || returnString.contentEquals("")) {
				returnString = in.readLine();
			}
		} catch (IOException e) {
			Log.e("executeTop", "error in getting first line of top");
			e.printStackTrace();
		} finally {
			try {
				in.close();
				p.destroy();
			} catch (IOException e) {
				Log.e("executeTop",
						"error in closing and destroying top process");
				e.printStackTrace();
			}
		}
		return returnString;
	}
	public CPURecord getRecord(){
		long[] update = getCpuUsageStatistic();
		if(update.length == 4){
			user = update[0];
			system = update[1];
			idle = update[2];
			other = update[3];
			return new CPURecord(Calendar.getInstance().getTime(),user,system,idle,other);
		}
		return null;
	}

}
