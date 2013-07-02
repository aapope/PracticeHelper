package com.example.practicehelper;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class PieceDetail extends Activity {
	//private PracticeDbAdapter dbHelper;
	private Cursor pieceCursor;
	private Cursor detailsCursor;
	private Uri pieceUri;
	
	private static final String[] PIECE_PROJECTION = { PieceTable._ID,
		PieceTable.COLUMN_TITLE, PieceTable.COLUMN_ORDER, PieceTable.COLUMN_TIME,
		PieceTable.COLUMN_TYPE, PieceTable.COLUMN_DATEADDED };
	private static final String[] DETAIL_PROJECTION = { DetailsTable._ID,
		DetailsTable.COLUMN_MEASURE_RANGE, DetailsTable.COLUMN_TEMPO_CURRENT,
		DetailsTable.COLUMN_TEMPO_TARGET, DetailsTable.COLUMN_DETAILS };
	
	private TextView time, type;
	private ListView details; 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.piece_detail);
		
		time = (TextView) findViewById(R.id.detail_time);
		type = (TextView) findViewById(R.id.detail_type);
		details = (ListView) findViewById(R.id.measure_range_list);
		
		Bundle extras = getIntent().getExtras();
		
		if (extras != null) {
			pieceUri = extras.getParcelable(PracticeContentProvider.CONTENT_ITEM_TYPE_PIECE);
			fillData(pieceUri);
		}
	}
	
	private void fillData(Uri uri) {
		//look out for notifyDataSetChanged()
		pieceCursor = getContentResolver().query(uri, PIECE_PROJECTION, null, null, null);
		if (pieceCursor != null) {
			pieceCursor.moveToFirst();
			
			Uri detUri = PracticeContentProvider.CONTENT_URI_DETAIL;
			String selection = DetailsTable.COLUMN_PIECE_ID + "=" + pieceCursor.getString(pieceCursor.getColumnIndexOrThrow(PieceTable._ID));
			detailsCursor = getContentResolver().query(detUri, DETAIL_PROJECTION, selection, null, null);
			
			if (detailsCursor != null) {
				setTitle(pieceCursor.getString(pieceCursor.getColumnIndexOrThrow(PieceTable.COLUMN_TITLE)));
				time.setText(pieceCursor.getString(pieceCursor.getColumnIndexOrThrow(PieceTable.COLUMN_TIME)));
				type.setText(pieceCursor.getString(pieceCursor.getColumnIndexOrThrow(PieceTable.COLUMN_TYPE)));
				setUpList();
			}
		}
		
		//something about changing format based upon the format. Now I need to think about another 
		//database table that holds all of the extra stuff. This view should probably also hold some
		//options (maybe as a slider on the top?) for other stuff.
	}
	
	private void setUpList() {
		View footerView = getLayoutInflater().inflate(R.layout.add_practice_item, null);
    	details.addFooterView(footerView);
		
		String[] from = new String[] { DetailsTable.COLUMN_MEASURE_RANGE, DetailsTable.COLUMN_TEMPO_CURRENT,
				DetailsTable.COLUMN_TEMPO_TARGET, DetailsTable.COLUMN_DETAILS };
		int[] to = new int[] { R.id.list_item_measure_range, R.id.list_item_tempo_current, 
				R.id.list_item_tempo_target, R.id.measure_range_comments };
		
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.measure_range_details, detailsCursor, from, to, 0);
		details.setAdapter(adapter);
	}
}
