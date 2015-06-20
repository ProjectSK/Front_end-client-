package com.d.localdb;


import java.util.Date;
import java.util.Locale;

import com.d.localdb.SQLVColumn.ColumnType;

public class AppUsageRecord implements Record {
    public String packageName;
    public Date startTime;
    public Long elapsedTime;

    
    public AppUsageRecord() { }
    public AppUsageRecord(String packageName, Date startTime, Long elapsedTime) {
        this.packageName = packageName;
        this.startTime = startTime;
        this.elapsedTime = elapsedTime;
    }

    public static SQLVTable TABLE = 
        new SQLVDatedTable(RecordFactory.reflection(AppUsageRecord.class), "appUsage", "startTime",
                new SQLVColumn[] {
                    new ReflVColumn(AppUsageRecord.class, "packageName", ColumnType.String, true),
                    new ReflVColumn(AppUsageRecord.class, "startTime", ColumnType.Datetime, true),
                    new ReflVColumn(AppUsageRecord.class, "elapsedTime", ColumnType.Long, false)
                });
    
    @Override
    public SQLVTable getTable() {
        return TABLE;
    }
	
    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "[packageName=%s, startTime=%s, elapsedTime=%ld]",  packageName, startTime, elapsedTime);
    }
	
}
