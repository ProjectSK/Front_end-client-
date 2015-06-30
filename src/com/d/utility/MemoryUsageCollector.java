package com.d.utility;

import java.util.Calendar;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.util.Log;

import com.d.localdb.LocalDB;
import com.d.localdb.MemoryRecord;

/**
 * @author vs223 Memory ��� ������ ȹ���ϱ� ���� class�̴�.
 */
public class MemoryUsageCollector {
	private ActivityManager am;
	private MemoryInfo mi;
	private Long freeMemory;
	private Long percentageOfMemoryUsage;
	private Long totalMemory;
	private Context context;

	/**
	 * @param context
	 *            ������ �����ϱ����� context, Ư���� �ǵ��� ���ٸ� getBaseContext()�� ��õ�Ѵ�.
	 */
	public MemoryUsageCollector(Context context) {
		this.context = context;
		am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		mi = new ActivityManager.MemoryInfo();
		update();
	}

	/**
	 * MemoryRecord type���� �� class�� �������� �����Ͽ� return�Ѵ�.
	 * 
	 * @return MemoryRecord
	 */
	private MemoryRecord getRecord() {
		update();
		return new MemoryRecord(Calendar.getInstance().getTime(),
				percentageOfMemoryUsage, totalMemory, freeMemory);
	}

	public void saveRecord() {
		LocalDB ldb_mem = new LocalDB(context, MemoryRecord.TABLE);
		try {
			MemoryRecord record = getRecord();

			if (record != null)
				ldb_mem.addRecord(record);
		} finally {
			ldb_mem.close();
		}
	}

	/**
	 * �� Ŭ������ ������ ������Ʈ�Ѵ�.
	 */
	public void update() {
		am.getMemoryInfo(mi);
		this.totalMemory = mi.totalMem / 1024 / 1024;
		this.freeMemory = mi.availMem / 1024 / 1024;
		this.percentageOfMemoryUsage = (long) (100.0 * (totalMemory - freeMemory) / totalMemory);
	}
}
