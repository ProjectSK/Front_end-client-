package com.d.localdb;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LocalDB extends SQLiteOpenHelper {

	// Database Version
	private static final int DATABASE_VERSION = 1;
	// Database Name
	private static String DATABASE_NAME = "SK_Planet_SNU_TeamD";
	
	public final SQLVTable sqlvtable;

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
	/*
	 * Parameters: context to use to open or create the database table name of
	 * the database file column names of the database file primary keys of the
	 * database file. they refer the indexes of column names starting form 0.
	 */
	public LocalDB(Context context, SQLVTable sqlvtable) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	    this.sqlvtable = sqlvtable;
	}

	private void createTable(SQLiteDatabase db) {

		String sql = sqlvtable.generateCreateTable();
	    Log.d("createTable", "created table with " + sql);
        db.execSQL(sql);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	    createTable(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + sqlvtable.name);
		// create fresh table
		createTable(db);
	}

	// ---------------------------------------------------------------------
	public void resetTable() {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
            db.execSQL("DELETE FROM " + sqlvtable.name);
		} catch (SQLException e){
		    e.printStackTrace();
		} finally {
            db.close();
		}
	}
	
	
	public ContentValues getContentValues(Record record) {
		ContentValues values = new ContentValues();
		for (SQLVColumn col : sqlvtable.columns) {
		    values.put(col.name, col.fromRecord(record));
		}
		return values;
	}
	
	private String getPrimaryKeyWhereClause(Record record) {
	    return getPrimaryKeyWhereClause(record, null);
	}

	private String getPrimaryKeyWhereClause(Record record, String additional) {
	    StringBuffer buffer = new StringBuffer();
        boolean isFirst = true;
        if (record != null) {
            for (SQLVColumn col : sqlvtable.getPrimaryKeys()) {
                if (col.fieldIsNull(record)) {
                    continue;
                }
                if (!isFirst)
                    buffer.append(" AND ");
                else
                    isFirst = false;
                buffer.append(col.name);
                buffer.append(" LIKE ");
                buffer.append("?");
            }
        }
        if (additional != null) {
            if (!isFirst)
                buffer.append(" AND ");
            else
                isFirst = false;
            buffer.append(additional);
        }
        return buffer.toString();
	}

	private String[] getPrimaryKeyWhereArgs(Record record, String [] additional) {
	    ArrayList<String> args = new ArrayList<String>();
	    if (record != null) {
            List<SQLVColumn> primaryKeys = sqlvtable.getPrimaryKeys();
            for (SQLVColumn col : primaryKeys) {
                if (!col.fieldIsNull(record))
                    args.add(col.fromRecord(record));
            }
	    }
        if (additional != null) {
            for (String ad : additional) {
                args.add(ad);
            }
        }
        
        String[] result = new String[args.size()];
        result = args.toArray(result);
        return result;
	}
	private String[] getPrimaryKeyWhereArgs(Record record) {
	    return getPrimaryKeyWhereArgs(record, null);
	}

	private void setRecordByCursor(Record record, Cursor cursor) {
        for (int idx = 0; idx < cursor.getColumnCount(); idx++) {
            String s = cursor.getString(idx);
            sqlvtable.columns[idx].toRecord(record, s);
        }
	}
	
	public boolean addRecord(Record record) {
	    return addRecord(record, true);
	}

	public boolean addRecord(Record record, boolean upsert) {
		Log.d("add element", record.toString() + " to " + sqlvtable.name);
		SQLiteDatabase db = this.getWritableDatabase();

		try {
            ContentValues values = getContentValues(record);

            RETRY: do {
                try {
                    long flag = db.insertOrThrow(sqlvtable.name, // table
                            null, // nullColumnHack
                            values); 
                    return flag != -1;
                }
                catch (SQLiteConstraintException c) {
                    /*
                     * handle the insertion error if it resulted from the primary key
                     * constraint and upsert is true
                     */
                    if (upsert) {
                        db.update(sqlvtable.name, values, getPrimaryKeyWhereClause(record), getPrimaryKeyWhereArgs(record));
                    } else
                        return false;
                } catch (SQLiteException s) {
                    if (s.getMessage().contains("no such table")) {
                        createTable(db);
                        onCreate(db);
                        continue RETRY;
                    } else
                        throw s;
                } 
            } while (false);
		} finally {
            db.close();
		}
		return true;
	}
	
	public <T extends Record> T getRecord(T recordWithPrimaryKey) {
		// 1. get reference to readable DB
		SQLiteDatabase db = this.getReadableDatabase();

		try {
            // 2. build query
            Cursor cursor = db.query(sqlvtable.name, // a. table
                    sqlvtable.getColumnNameArray(), // b. column names
                    getPrimaryKeyWhereClause(recordWithPrimaryKey), // c. selections
                    getPrimaryKeyWhereArgs(recordWithPrimaryKey), // d. selections args
                    null, // e. group by
                    null, // f. having
                    null, // g. order by
                    null); // h. limit

            // 3. if we got results get the first one
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    setRecordByCursor(recordWithPrimaryKey, cursor);
                    return recordWithPrimaryKey;
                } else
                    return null;
            } else
                return null;
		} finally {
		    db.close();
		}
	}

	public int getCount() {
		SQLiteDatabase db = this.getReadableDatabase();
		try {
		    Cursor cursor = db.rawQuery("SELECT count(*) FROM " + sqlvtable.name, null);
		    if (cursor == null)
		        return 0;
		    if (cursor.moveToFirst()) {
		        return cursor.getInt(0);
		    } else
		        return 0;
		} finally {
		    db.close();
		}
	}
	
	
	/**
	 * when either 'from' or 'until' specified, SQLVTable associated to this LocalDB should be of type of SQLVDatedTable
	 * 
	 * (from, until]
	 * 
	 */
	@SuppressWarnings({ "unchecked" })
    public <T extends Record> List<T> getAll(T critiaRecord, Date from, Date until, boolean desc, Integer limit) {

	    String additionalWhereClause = null;
	    String [] additionalArgs = null;
	    
	    String orderBy = null;
	    String limitStr = limit == null? null : limit.toString();
	    if (sqlvtable instanceof SQLVDatedTable) {
            SQLVDatedTable datedTable = (SQLVDatedTable)sqlvtable;
            SQLVColumn dateColumn = datedTable.getDateColumn();
            orderBy = dateColumn.name + (desc?" DESC":" ASC");
	    }

	    if (from != null || until != null) {
            SQLVDatedTable datedTable = (SQLVDatedTable)sqlvtable;
            SQLVColumn dateColumn = datedTable.getDateColumn();

            additionalWhereClause = "";
            additionalArgs = new String[(from != null && until != null)? 2 : 1];
            int argIdx = 0;

            boolean isFirst = true;
            if (from != null) {
                isFirst = false;
                additionalWhereClause += dateColumn.name + " > datetime(?)";
                additionalArgs[argIdx++] = dateFormat.format(from);
            }
            if (until != null) {
                if (!isFirst)
                    additionalWhereClause += " AND ";
                additionalWhereClause += dateColumn.name + " <= datetime(?)";
                additionalArgs[argIdx++] = dateFormat.format(until);
            }
	    }
	    String whereStr = getPrimaryKeyWhereClause(critiaRecord, additionalWhereClause);
	    String [] whereArgs = getPrimaryKeyWhereArgs(critiaRecord, additionalArgs);
	    String d = "[";
	    for  (String a : whereArgs) {
	        d += a;
	        d += ", ";
	    }
	    d += "]";
		Log.d("getAll", "getAll: " + whereStr + " and " + d);
	    
	    if (whereArgs.length == 0) {
	        whereStr = null;
	        whereArgs = null;
	    }
	    
		SQLiteDatabase db = this.getReadableDatabase();
		ArrayList<Record> result = new ArrayList<Record>();
		try {
            Cursor cursor = db.query(sqlvtable.name, sqlvtable.getColumnNameArray(), whereStr, whereArgs, null, null, orderBy, limitStr);
            while (cursor.moveToNext()) {
                Record newRecord = sqlvtable.recordFactory.newRecord();
                setRecordByCursor(newRecord, cursor);
                result.add(newRecord);
            }
		} finally {
		    db.close();
		}
		return (List<T>)result;
	}

	// Updating single record
	public boolean updateElement(Record record) {
		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();
		try {
            int i;
            i = db.update(sqlvtable.name, // table
                    getContentValues(record), 
                    getPrimaryKeyWhereClause(record), // selections
                    getPrimaryKeyWhereArgs(record)); // selection args
            return i != -1;
		} finally {
            db.close();
		}
	}

	public int deleteElement(Record record) {
		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();
		try {
		    return db.delete(sqlvtable.name, getPrimaryKeyWhereClause(record), getPrimaryKeyWhereArgs(record));
		} finally {
            db.close();
		}
	}
}
