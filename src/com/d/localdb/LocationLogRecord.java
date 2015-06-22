package com.d.localdb;

import java.util.Date;
import java.util.Locale;

import com.d.localdb.SQLVColumn.ColumnType;

public class LocationLogRecord implements Record {
    public static SQLVTable TABLE = new SQLVDatedTable(RecordFactory.reflection(LocationLogRecord.class), "location", "time",
                new SQLVColumn [] {
                    new ReflVColumn(LocationLogRecord.class, "time", ColumnType.Datetime, true),
                    new ReflVColumn(LocationLogRecord.class, "latitude", ColumnType.Float, false),
                    new ReflVColumn(LocationLogRecord.class, "longtitude", ColumnType.Float, false)
        });

    public Float latitude;

    public Float longtitude;

    public Date time;
    public LocationLogRecord() {
    }
    public LocationLogRecord(Date time, Float latitude, Float longtitude) {
        this.time = time;
        this.latitude = latitude;
        this.longtitude = longtitude;
    }

    @Override
    public SQLVTable getTable() {
        return TABLE;
    }
    
    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "[time=%s, latitude=%.2f, longitude=%.2f]", time.toString(), latitude, longtitude);
    }
}
