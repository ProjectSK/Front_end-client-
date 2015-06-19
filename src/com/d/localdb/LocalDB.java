package com.d.localdb;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
	private String DATABASE_TABLE;
	private String[] COLUMN_NAMES;
	private Integer[] PRIMARY_KEYS;

	/*
	 * Parameters: context to use to open or create the database table name of
	 * the database file column names of the database file primary keys of the
	 * database file. they refer the indexes of column names starting form 0.
	 */
	public LocalDB(Context context, Record record) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		DATABASE_TABLE = new String(record.getTableName());
		COLUMN_NAMES = new String[record.getColumnNames().size()];
		PRIMARY_KEYS = new Integer[record.getPrimaryKeyIndexes().size()];
		record.getColumnNames().toArray(COLUMN_NAMES);
		record.getPrimaryKeyIndexes().toArray(PRIMARY_KEYS);
		
		
	}

	private String create_table_query(){
		String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + DATABASE_TABLE
				+ " ( ";
		String PRIMARY_KEY_CLAUSE = "";
		int j = 0;
		for (int i = 0; i < COLUMN_NAMES.length; i++) {
			CREATE_TABLE += COLUMN_NAMES[i] + " TEXT";

			if (j < PRIMARY_KEYS.length) {
				if (PRIMARY_KEYS[j].equals(i)) {
					if (j != 0) {
						PRIMARY_KEY_CLAUSE += ", ";
					}
					PRIMARY_KEY_CLAUSE += COLUMN_NAMES[i];
					j++;
				}
			}
			if (i != COLUMN_NAMES.length - 1)
				CREATE_TABLE += ", ";
		}

		if (PRIMARY_KEYS.length != 0) {
			CREATE_TABLE += " , PRIMARY KEY ( " + PRIMARY_KEY_CLAUSE + " )";

		}

		CREATE_TABLE += " )";
		return CREATE_TABLE;
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		// SQL statement to create record table
		String CREATE_TABLE = create_table_query();

		// create table
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);

		// create fresh table
		this.onCreate(db);
	}

	// ---------------------------------------------------------------------
	public void resetTable() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DELETE FROM " + DATABASE_TABLE);
		db.close();
	}

	public void addElement(Record record) {
		Log.d("add element", record.toString());
		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();

		// 2. create ContentValues to add key "column"/value
		ContentValues values = new ContentValues();
		List<String> elements = record.getElements();
		List<String> columns = record.getColumnNames();
		for (int i = 0; i < columns.size(); i++) {
			values.put(columns.get(i), elements.get(i)); // get title
		}

		long flag;
		// 3. insert
		do {
			try {
				flag = db.insertOrThrow(DATABASE_TABLE, // table
						null, // nullColumnHack
						values); // key/value -> keys = column names/ values =
									// column values
				break;
			}
			/*
			 * handle the insert error if it resulted from the primary key
			 * constraint.
			 */
			catch (SQLiteConstraintException c) {
				String whereClause = "";
				for (int i = 0; i < PRIMARY_KEYS.length; i++) {
					if (i != 0)
						whereClause += " AND ";
					int index = PRIMARY_KEYS[i];
					whereClause += COLUMN_NAMES[index] + " LIKE "
							+ elements.get(index);
				}
				db.update(DATABASE_TABLE, values, whereClause, null);
			}
			/*
			 * handle no such table
			 */
			catch (SQLiteException s) {
				if (s.getMessage().contains("no such table")) {
					onCreate(db);
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		} while (true);

		// 4. close
		db.close();
	}

	public List<String> getElement(List<String> primaryKeys) {

		// 1. get reference to readable DB
		SQLiteDatabase db = this.getReadableDatabase();

		// 1.5. set where clause
		String whereClause = "";
		for (int i = 0; i < PRIMARY_KEYS.length; i++) {
			if (i != 0)
				whereClause += " AND ";
			int index = PRIMARY_KEYS[i];
			whereClause += COLUMN_NAMES[index] + " LIKE " + primaryKeys.get(i);
		}

		// 2. build query
		Cursor cursor = db.query(DATABASE_TABLE, // a. table
				COLUMN_NAMES, // b. column names
				whereClause, // c. selections
				null, // d. selections args
				null, // e. group by
				null, // f. having
				null, // g. order by
				null); // h. limit

		// 3. if we got results get the first one
		if (cursor != null)
			cursor.moveToFirst();

		// 4. build object
		List<String> element = new Vector<String>();

		for (int i = 0; i < cursor.getColumnCount(); i++)
			element.add(cursor.getString(i));

		Log.d("get ", element.toString());

		// 5. return record
		return element;
	}

	// Get All records
	public List<String[]> getAlls() {
		List<String[]> elements = new LinkedList<String[]>();

		// 2. get reference to Writable DB
		SQLiteDatabase db = this.getWritableDatabase();
		onCreate(db);
		Cursor cursor = db.rawQuery("SELECT * FROM " + DATABASE_TABLE, null);
		Log.d("getAll()", DATABASE_TABLE);
		// 3. go over each row, build record and add it to list

		while (cursor.moveToNext()) {
			String element[] = new String[cursor.getColumnCount()];
			for (int i = 0; i < cursor.getColumnCount(); i++)
				element[i] = cursor.getString(i);

			// Add record to records
			elements.add(element);
		}

		// return records
		return elements;
	}

	// Updating single record
	public int updateElement(Record record) {

		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();

		// 2. create ContentValues to add key "column"/value
		ContentValues values = new ContentValues();
		List<String> elements = record.getElements();
		List<String> columns = record.getColumnNames();
		for (int i = 0; i < columns.size(); i++) {
			values.put(columns.get(i), elements.get(i)); // get title
		}

		// 3. updating row
		String whereClause = "";
		for (int i = 0; i < PRIMARY_KEYS.length; i++) {
			if (i != 0)
				whereClause += " AND ";
			int index = PRIMARY_KEYS[i];
			whereClause += COLUMN_NAMES[index] + " LIKE " + elements.get(index);
		}

		int i = db.update(DATABASE_TABLE, // table
				values, // column/value
				whereClause, // selections
				null); // selection args

		// 4. close
		db.close();

		return i;

	}

	// Deleting single record
	public void deleteElement(List<String> primaryKeys) {

		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();

		// 1.5. set where clause
		String whereClause = "";
		for (int i = 0; i < PRIMARY_KEYS.length; i++) {
			if (i != 0)
				whereClause += " AND ";
			int index = PRIMARY_KEYS[i];
			whereClause += COLUMN_NAMES[index] + " LIKE " + primaryKeys.get(i);
		}

		// 2. delete
		db.delete(DATABASE_TABLE, whereClause, null);

		// 3. close
		db.close();

		Log.d("delet", whereClause);

	}
}
