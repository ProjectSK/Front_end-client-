package com.d.localdb;

public class SQLVDatedTable extends SQLVTable {
    public final SQLVColumn dateColumn;
    public SQLVDatedTable(RecordFactory recordFactory, String name, String dateColumnName, SQLVColumn[] columns) {
        super(recordFactory, name, columns);
        SQLVColumn dateColumn = null;
        for (SQLVColumn col : columns) {
            if (col.name.equals(dateColumnName)) {
                dateColumn = col;
                break;
            }
        }
        this.dateColumn = dateColumn;
        if (dateColumn == null) {
            throw new IllegalArgumentException("Field '" + dateColumnName + "' is not found");
        }
    }

    public SQLVColumn getDateColumn() {
        return dateColumn;
    }
}