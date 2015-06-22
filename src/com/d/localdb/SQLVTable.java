package com.d.localdb;

import java.util.ArrayList;
import java.util.List;


public class SQLVTable {
    public final SQLVColumn [] columns;
    public final String name;
    public final RecordFactory recordFactory;
    
    public SQLVTable(RecordFactory recordFactory, String name, SQLVColumn[] columns) {
        super();
        this.recordFactory = recordFactory;
        this.name = name;
        this.columns = columns;
        for (SQLVColumn col : columns) {
            col.setTable(this);
        }
    }
    
    public String generateCreateTable() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("CREATE TABLE IF NOT EXISTS ");
        buffer.append(this.name);
        buffer.append(" ( ");
        
        boolean isFirst = true;
		for (SQLVColumn column : columns) {
		    if (!isFirst)
		        buffer.append(", ");
		    else
		        isFirst = false;
		    buffer.append(column.name);
		    buffer.append(" ");
		    buffer.append(column.toSQLType());
		}
		
		List<SQLVColumn> primaryKeys = getPrimaryKeys();
		if (!primaryKeys.isEmpty()) {
		    buffer.append(", PRIMARY KEY (");
            isFirst = true;
            for (SQLVColumn primaryCol : primaryKeys) {
                if (!isFirst)
                    buffer.append(", ");
                else
                    isFirst = false;
                buffer.append(primaryCol.name);
            }
		    buffer.append(")");
		}
        buffer.append(")");
		return buffer.toString();
    }
    
    public String [] getColumnNameArray() {
        String [] result = new String[columns.length];
        int idx = 0;
        for (SQLVColumn col : columns) {
            result[idx++] = col.name;
        }
        return result;
    }
    
    public List<SQLVColumn> getPrimaryKeys() {
        ArrayList<SQLVColumn> result = new ArrayList<SQLVColumn>();
        for (SQLVColumn col : columns) {
            if (col.isPrimaryKey) {
                result.add(col);
            }
        }
        return result;
    }
}

