package com.d.utility;

import java.util.Calendar;
import java.util.Date;

import com.d.localdb.MemoryRecord;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;

public class MemoryUsageCollector {
	private ActivityManager am;
	private MemoryInfo mi;
	
	public Long percentageOfMemoryUsage;
	public Long totalMemory;
	public Long freeMemory;
	
	public MemoryUsageCollector(Context context){
		  am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	      mi = new ActivityManager.MemoryInfo();
		update();
	}
	
	public void update(){
		am.getMemoryInfo(mi);
		this.totalMemory= mi.totalMem / 1024 / 1024;
		this.freeMemory = mi.availMem / 1024 / 1024;
		this.percentageOfMemoryUsage = (long) (100.0*(totalMemory - freeMemory)/totalMemory);
	}
	
	public MemoryRecord getRecord(){
		update();
		return new MemoryRecord(Calendar.getInstance().getTime(), percentageOfMemoryUsage, totalMemory, freeMemory);
	}
}
