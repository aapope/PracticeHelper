package com.example.practicehelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PracticeDatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "piecetable.db";
	private static final int DATABASE_VERSION = 1;
	
	public PracticeDatabaseHelper(Context ctx) {
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		PieceTable.onCreate(database);
		DetailsTable.onCreate(database);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		PieceTable.onUpgrade(database, oldVersion, newVersion);
		DetailsTable.onUpgrade(database, oldVersion, newVersion);
	}

}
