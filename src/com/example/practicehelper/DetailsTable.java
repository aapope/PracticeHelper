package com.example.practicehelper;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DetailsTable {
	public static final String TABLE = "details";
	public static final String _ID = "_id";
	public static final String COLUMN_PIECE_ID = "piece_id";
	public static final String COLUMN_MEASURE_RANGE = "measure_range";
	public static final String COLUMN_TEMPO_CURRENT = "curr_tempo";
	public static final String COLUMN_TEMPO_TARGET = "target_tempo";
	public static final String COLUMN_DETAILS = "deets";
	
	public static final String DATABASE_CREATE = "create table "
			+ TABLE
			+ "("
			+ _ID + " integer primary key autoincrement, "
			+ COLUMN_PIECE_ID + " integer, "
			+ COLUMN_MEASURE_RANGE + " text, "
			+ COLUMN_TEMPO_CURRENT + " integer, "
			+ COLUMN_TEMPO_TARGET + " integer, "
			+ COLUMN_DETAILS + " text, "
			+ "FOREIGN KEY(" + COLUMN_PIECE_ID + ") "
			+ "references " + PieceTable.TABLE + "(" + PieceTable._ID + ")"
			+ ");";
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(DetailsTable.class.getName(), "Upgrading database version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE);
		onCreate(database);
	}
}
