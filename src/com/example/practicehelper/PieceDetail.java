package com.example.practicehelper;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;

public class PieceDetail extends Activity {
	private PracticeDbAdapter dbHelper;
	private Cursor pieceCursor;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.piece_detail);
		
		long id = getIntent().getExtras().getLong(PracticeDbAdapter._ID);
		dbHelper = new PracticeDbAdapter(this);
		dbHelper.open();
		
		fillData(id);
		
	}
	
	private void fillData(long id) {
		pieceCursor = dbHelper.fetchRow(id);
		
		String title = pieceCursor.getString(pieceCursor.getColumnIndexOrThrow(PracticeDbAdapter.KEY_TITLE));
		String type = pieceCursor.getString(pieceCursor.getColumnIndexOrThrow(PracticeDbAdapter.KEY_TYPE));
		int time = pieceCursor.getInt(pieceCursor.getColumnIndexOrThrow(PracticeDbAdapter.KEY_TIME));
		long date_added = pieceCursor.getLong(pieceCursor.getColumnIndexOrThrow(PracticeDbAdapter.KEY_DATEADDED));
		long now = System.currentTimeMillis();
		
		setTitle(title);
		
		//something about changing format based upon the format. Now I need to think about another 
		//database table that holds all of the extra stuff. This view should probably also hold some
		//options (maybe as a slider on the top?) for other stuff.
	}
}
