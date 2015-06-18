package com.d.localdb;
import java.util.LinkedList;
import java.util.List;
 
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
 
public class Localdb extends SQLiteOpenHelper {
 
    // Database Version
    private static int DATABASE_VERSION = 1;
    // Database Name
    private static String DATABASE_NAME;
 
    public Localdb(Context context, String talbeName) { 
        super(context, DATABASE_NAME, null, DATABASE_VERSION);  
        DATABASE_NAME = new String(talbeName);
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table
        String CREATE_TABLE = "CREATE TABLE " + DATABASE_NAME +"( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
                "json TEXT )";
 
        // create table
        db.execSQL(CREATE_TABLE);
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_NAME);
 
        // create fresh  table
        this.onCreate(db);
    }
    //---------------------------------------------------------------------
    public void resetTable(){
    	SQLiteDatabase db = this.getWritableDatabase();
    	db.execSQL("DROP TABLE IF EXISTS "+DATABASE_NAME);
    	this.onCreate(db);
    }
   
 
    // Books Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_JSON = "json";
    
    private static final String[] COLUMNS = {KEY_ID,KEY_JSON};
 
    public void addElement(String json){
        Log.d("addelement", json);
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
 
        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_JSON, json); // get title 
    
 
        // 3. insert
        db.insert(DATABASE_NAME, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values
 
        // 4. close
        db.close(); 
    }
 
    public String getElement(int id){
 
        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();
 
        // 2. build query
        Cursor cursor = 
                db.query(DATABASE_NAME, // a. table
                COLUMNS, // b. column names
                " id = ?", // c. selections 
                new String[] { String.valueOf(id) }, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
 
        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();
 
        // 4. build  object
        String element = Integer.parseInt(cursor.getString(0)) + "$" +
        cursor.getString(1) + "$"+
        cursor.getString(2);
 
        Log.d("get("+id+")", element);
 
        // 5. return book
        return element;
    }
 
    // Get All Books
    public List<String> getAlls() {
        List<String> elements = new LinkedList<String>();
 
        // 1. build the query
        String query = "SELECT  * FROM " + DATABASE_NAME;
 
        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
 
        // 3. go over each row, build book and add it to list
     
        if (cursor.moveToFirst()) {
            do {
            	        	String element = Integer.parseInt(cursor.getString(0)) + " " +
            	        cursor.getString(1);
            	 
 
                // Add book to books
                elements.add(element);
            } while (cursor.moveToNext());
        }
 
        Log.d("getAll()", elements.toString());
 
        // return books
        return elements;
    }
 
     // Updating single book
    public int updateElement(String id, String json) {
 
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
 
        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("json", json); 
 
        // 3. updating row
        int i = db.update(DATABASE_NAME, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(id) }); //selection args
 
        // 4. close
        db.close();
 
        return i;
 
    }
 
    // Deleting single book
    public void deleteElement(String id) {
 
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
 
        // 2. delete
        db.delete(DATABASE_NAME,
                KEY_ID+" = ?",
                new String[] { String.valueOf(id) });
 
        // 3. close
        db.close();
 
        Log.d("delet", id);
 
    }
}
