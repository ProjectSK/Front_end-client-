package com.d.localdb;


import java.util.Date;

import com.d.localdb.SQLVColumn.ColumnType;

public class MemoryRecord implements Record {
	
	public static SQLVTable TABLE = 
        new SQLVDatedTable(RecordFactory.reflection(MemoryRecord.class), "memoryRecord", "time",
                new SQLVColumn[] {
                    new ReflVColumn(MemoryRecord.class, "time", ColumnType.Datetime, true),
                    new ReflVColumn(MemoryRecord.class, "percentageOfMemoryUsage", ColumnType.Long, false),
                    new ReflVColumn(MemoryRecord.class, "totalMemory", ColumnType.Long, false),
                    new ReflVColumn(MemoryRecord.class, "freeMemory", ColumnType.Long, false)
                });

	public Long freeMemory;
	public Long percentageOfMemoryUsage;
	public Date time;
	public Long totalMemory;
    
    public MemoryRecord() { }

    public MemoryRecord(Date time, Long percentageOfMemoryUsage,
			Long totalMemory, Long freeMemory) {
		super();
		this.time = time;
		this.percentageOfMemoryUsage = percentageOfMemoryUsage;
		this.totalMemory = totalMemory;
		this.freeMemory = freeMemory;
	}
    
    @Override
    public SQLVTable getTable() {
        return TABLE;
    }

	@Override
	public String toString() {
		return "MemoryRecord [time=" + time + ", percentageOfMemoryUsage="
				+ percentageOfMemoryUsage + ", totalMemory=" + totalMemory
				+ ", freeMemory=" + freeMemory + "]";
	}

   
	
}
