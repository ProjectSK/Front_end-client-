package com.d.localdb;

import java.util.Date;
import java.util.Locale;

import com.d.localdb.SQLVColumn.ColumnType;

public class LocationLogRecord implements Record {
    public static SQLVTable TABLE = new SQLVDatedTable(RecordFactory.reflection(LocationLogRecord.class), "location", "time",
                new SQLVColumn [] {
                    new ReflVColumn(LocationLogRecord.class, "time", ColumnType.Datetime, true),
                    new ReflVColumn(LocationLogRecord.class, "latitude", ColumnType.Double, false),
                    new ReflVColumn(LocationLogRecord.class, "longitude", ColumnType.Double, false)
        });

    public Double latitude;
    public Double longitude;

    public Date time;
    public LocationLogRecord() {
    }
    public LocationLogRecord(Date time, Double latitude, Double longitude) {
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public SQLVTable getTable() {
        return TABLE;
    }
    
    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "[time=%s, latitude=%.2f, longitude=%.2f]", time.toString(), latitude, longitude);
    }
}
