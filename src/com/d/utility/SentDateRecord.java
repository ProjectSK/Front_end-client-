package com.d.utility;

import java.util.Date;

import com.d.localdb.Record;
import com.d.localdb.RecordFactory;
import com.d.localdb.ReflVColumn;
import com.d.localdb.SQLVColumn;
import com.d.localdb.SQLVDatedTable;
import com.d.localdb.SQLVTable;
import com.d.localdb.SQLVColumn.ColumnType;

public class SentDateRecord implements Record {
    public Date recentSentDate;
    public SentDateRecord() { }
    public SentDateRecord(Date date) { this.recentSentDate = date; }

    public static SQLVTable TABLE = 
        new SQLVDatedTable(RecordFactory.reflection(SentDateRecord.class), "sentDate", "recentSentDate",
                new SQLVColumn[] {
                    new ReflVColumn(SentDateRecord.class, "recentSentDate", ColumnType.Datetime, true),
                });

    @Override
    public SQLVTable getTable() {
        return TABLE;
    }
    
    
}
