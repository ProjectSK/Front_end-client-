package com.d.utility;

import java.util.Calendar;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;

import com.d.localdb.MemoryRecord;

/**
 * @author vs223
 * Memory 사용 정보를 획득하기 위한 class이다.
 */
public class MemoryUsageCollector {
	private ActivityManager am;
	private MemoryInfo mi;
	private Long freeMemory;
	private Long percentageOfMemoryUsage;
	private Long totalMemory;

	/**
	 * @param context 정보를 추출하기위한 context, 특별한 의도가 없다면 getBaseContext()를 추천한다. 
	 */
	public MemoryUsageCollector(Context context) {
		am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		mi = new ActivityManager.MemoryInfo();
		update();
	}
	

	/**
	 * MemoryRecord type으로 본 class의 정보들을 저장하여 return한다.
	 * @return MemoryRecord
	 */
	public MemoryRecord getRecord() {
		update();
		return new MemoryRecord(Calendar.getInstance().getTime(),
				percentageOfMemoryUsage, totalMemory, freeMemory);
	}

	/**
	 *  본 클래스의 내용을 업데이트한다.
	 */
	public void update() {
		am.getMemoryInfo(mi);
		this.totalMemory = mi.totalMem / 1024 / 1024;
		this.freeMemory = mi.availMem / 1024 / 1024;
		this.percentageOfMemoryUsage = (long) (100.0 * (totalMemory - freeMemory) / totalMemory);
	}
}
