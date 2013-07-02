package com.example.practicehelper;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class PieceTable {
	public static final String TABLE = "pieces";
	public static final String _ID = "_id";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_ORDER = "item_order";
	public static final String COLUMN_TIME = "time";
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_DATEADDED = "date_added";
	
	public static final String DATABASE_CREATE = "create table "
			+ TABLE
			+ "("
			+ _ID + " integer primary key autoincrement, "
			+ COLUMN_TITLE + " text not null, "
			+ COLUMN_ORDER + " integer, "
			+ COLUMN_TIME + " integer, "
			+ COLUMN_TYPE + " integer, "
			+ COLUMN_DATEADDED + " integer"
			+ ");";
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(PieceTable.class.getName(), "Upgrading database from version "
		        + oldVersion + " to " + newVersion
		        + ", which will destroy all old data");
		    database.execSQL("DROP TABLE IF EXISTS " + TABLE);
		    onCreate(database);
	}
}
