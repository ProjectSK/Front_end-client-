package com.d.api;

import java.util.Date;
import java.util.List;

import android.content.Context;

import com.d.localdb.LocalDB;
import com.d.localdb.MemoryRecord;

public class MemoryUsage {
	private Context context;
	private LocalDB ldb;
	public MemoryUsage(Context context){
		this.context = context;
	}
	/**
	 * 하룻동안 수집된 기록을 상한 만큼 불러오는 method.
	 * 저장된 것의 최신순으로 불려온다.
	 * @param limit DB에서 불러올 Record의 상한
	 * @return DB에서 불러온 a list of  Records.
	 */
	public List<MemoryRecord> getRecords(int limit){
		List<MemoryRecord> records;
		ldb = new LocalDB(context, MemoryRecord.TABLE);
		try{
			if(limit <= 0)
				limit = 0;
			long yesterday = System.currentTimeMillis() - (1000* 60 * 60 * 24);
			records =ldb.getAll(new MemoryRecord(),new Date(yesterday), null, true, limit);
			
		}
		finally {
			ldb.close();
		}
		return records;
	}
}
