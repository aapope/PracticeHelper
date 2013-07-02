package com.example.practicehelper;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

public class PieceDetail extends Activity {
	//private PracticeDbAdapter dbHelper;
	private Cursor pieceCursor;
	private Uri pieceUri;
	private static final String[] PIECE_PROJECTION = { PieceTable._ID,
		PieceTable.COLUMN_TITLE, PieceTable.COLUMN_ORDER, PieceTable.COLUMN_TIME,
		PieceTable.COLUMN_TYPE, PieceTable.COLUMN_DATEADDED };
	private static final String[] DETAIL_PROJECTION = { DetailsTable._ID,
		DetailsTable.COLUMN_MEASURE_RANGE, DetailsTable.COLUMN_TEMPO_CURRENT,
		DetailsTable.COLUMN_TEMPO_TARGET, DetailsTable.COLUMN_DETAILS };
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.piece_detail);
		
		Bundle extras = getIntent().getExtras();
		
		if (extras != null) {
			pieceUri = extras.getParcelable(PracticeContentProvider.CONTENT_ITEM_TYPE_PIECE);
			
			fillData(pieceUri);
		}
	}
	
	private void fillData(Uri uri) {
		pieceCursor = getContentResolver().query(uri, PIECE_PROJECTION, null, null, null);
		
		if (pieceCursor != null) {
			pieceCursor.moveToFirst();
			//do the filling here!
			setTitle(pieceCursor.getString(pieceCursor.getColumnIndexOrThrow(PieceTable.COLUMN_TITLE)));
		}
		
		//something about changing format based upon the format. Now I need to think about another 
		//database table that holds all of the extra stuff. This view should probably also hold some
		//options (maybe as a slider on the top?) for other stuff.
	}
}
