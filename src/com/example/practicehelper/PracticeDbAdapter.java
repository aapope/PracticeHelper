package com.example.practicehelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 
 * Most code taken from the NotesDbAdapter class in the Notepad sample
 * @author andrewpope
 *
 */
public class PracticeDbAdapter {
	public static final String KEY_TYPE = "type";
	public static final String KEY_ORDER = "item_order";
	public static final String KEY_TIME = "time";
	public static final String KEY_DATEADDED = "date_added";
	public static final String KEY_TITLE = "title";
    public static final String _ID = "_id";

    private static final String TAG = "PracticeDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    
    private static final String DATABASE_CREATE =
    		"CREATE TABLE practice_list (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
    		+ "type TEXT, item_order INTEGER UNIQUE, time INTEGER, " +
    				"date_added DATE NOT NULL, title TEXT NOT NULL);";
    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "practice_list";
    private static final int DATABASE_VERSION = 1;
    
    private final Context mCtx;
    
    private int max_order;
    
    private static class DatabaseHelper extends SQLiteOpenHelper {
    	
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
        }
    }
    
    public PracticeDbAdapter(Context ctx) {
    	this.mCtx = ctx;
    }
    
    public PracticeDbAdapter open() throws SQLException {
    	mDbHelper = new DatabaseHelper(mCtx);
    	mDb = mDbHelper.getWritableDatabase();
    	Cursor orderC  = mDb.rawQuery("SELECT MAX(" + KEY_ORDER + ") FROM " + DATABASE_TABLE+";", null);
    	if (orderC != null) {
    		orderC.moveToFirst();
    		max_order = orderC.getInt(0) + 1;
    	} else {
    		max_order = 1;
    	}
    	return this;
    }
    
    public void close() {
    	mDbHelper.close();
    }
    
    public long createRow(String title, String type, int mins) {
    	long date_added = System.currentTimeMillis();
    	ContentValues initialValues = new ContentValues();
    	initialValues.put(KEY_TITLE, title);
    	initialValues.put(KEY_ORDER, max_order);
    	initialValues.put(KEY_TYPE, type);
    	initialValues.put(KEY_TIME, mins);
    	initialValues.put(KEY_DATEADDED, date_added);
    	
    	max_order++;
    	
    	return mDb.insert(DATABASE_TABLE, null, initialValues);
    }
    
    public boolean deleteRow(long rowId) {

        return mDb.delete(DATABASE_TABLE, _ID + "=" + rowId, null) > 0;
    }
    
    public Cursor fetchAllRows() {
    	String[] col = {_ID, KEY_TITLE, KEY_ORDER, KEY_TYPE, KEY_TIME, KEY_DATEADDED};
        return mDb.query(DATABASE_TABLE, col, null, null, null, null, KEY_ORDER);
    }
    
    public Cursor fetchRow(long rowId) throws SQLException {
    	String[] rows = {_ID, KEY_TITLE, KEY_ORDER, KEY_TYPE, KEY_TIME, KEY_DATEADDED};

        Cursor mCursor =

            mDb.query(true, DATABASE_TABLE, rows, _ID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    
    public boolean updateTime(long rowId, int mins) {
        ContentValues args = new ContentValues();
        args.put(KEY_TIME, mins);
        
        return mDb.update(DATABASE_TABLE, args, _ID + "=" + rowId, null) > 0;
    }
    
    public boolean updateOrder(long rowId, int order) {
    	//oh god, I don't really want to do this!
    	String putThis= "UPDATE " + DATABASE_TABLE + " SET " + KEY_ORDER + "=" + Integer.toString(order) + " WHERE " + _ID + "=" + Long.toString(rowId) + ";";
    	String setOthers = "UPDATE " + DATABASE_TABLE + " SET " + KEY_ORDER + "=" + KEY_ORDER + "+1 "
    			+ "WHERE " + KEY_ORDER + ">=" + Integer.toString(order) + " AND " + KEY_ORDER + "<(SELECT " + KEY_ORDER + " FROM " +
    			DATABASE_TABLE + " WHERE " + _ID + "=" + Long.toString(rowId) + ";";
    	
    	Cursor others = mDb.rawQuery(setOthers, null);
    	Cursor thisOne = mDb.rawQuery(putThis, null);
    	
    	//can we somehow check if they worked?
    	return true;
    }
    
}
