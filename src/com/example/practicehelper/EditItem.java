package com.example.practicehelper;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class EditItem extends Activity {
	private EditText titleText;
	private EditText timeText;
	private Spinner typeSpinner;
	
	private Uri pieceUri;
	//TODO: Add a spot for all the things to change!
	private static final String[] PROJECTION = { PieceTable.COLUMN_TITLE,
		PieceTable.COLUMN_TIME, PieceTable.COLUMN_TYPE 
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_layout);
		setTitle(R.string.add_item);
		
		titleText = (EditText) findViewById(R.id.add_piece_title);
		timeText = (EditText) findViewById(R.id.add_piece_time);
		typeSpinner = (Spinner) findViewById(R.id.add_piece_spinner);
		Button confirmButton = (Button) findViewById(R.id.add_piece_confirm);
		Button cancelButton = (Button) findViewById(R.id.add_piece_cancel);
		
		Bundle extras = getIntent().getExtras();
		
		//check the saved instance
		pieceUri = (savedInstanceState == null) ? null : (Uri) savedInstanceState.getParcelable(PracticeContentProvider.CONTENT_ITEM_TYPE_PIECE);
		
		//check from other activity
		if (extras != null) {
			pieceUri = extras.getParcelable(PracticeContentProvider.CONTENT_ITEM_TYPE_PIECE);
			
			fillData(pieceUri);
		}
		
		//now set click listeners for the buttons
		confirmButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//TODO: Save the thing here, then return!
				saveState();
				setResult(RESULT_OK);
				finish();
			}
		});
		
		cancelButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
	}
	
	protected void onSavedInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(PracticeContentProvider.CONTENT_ITEM_TYPE_PIECE, pieceUri);
	}
	
	protected void onPause() {
		super.onPause();
	}
	
	private void fillData(Uri uri) {
		Cursor c = getContentResolver().query(uri, PROJECTION, null, null, null);
		
		if (c != null) {
			c.moveToFirst();
			titleText.setText(c.getString(c.getColumnIndexOrThrow(PieceTable.COLUMN_TITLE)));
			timeText.setText(c.getString(c.getColumnIndexOrThrow(PieceTable.COLUMN_TIME)));
			
			//Set the proper spinner position
			String type = c.getString(c.getColumnIndexOrThrow(PieceTable.COLUMN_TYPE));
			for (int i=0; i<typeSpinner.getCount(); i++) {
				String s = (String) typeSpinner.getItemAtPosition(i);
				if (s.equalsIgnoreCase(type)) {
					typeSpinner.setSelection(i);
				}
			}
			
			c.close();
		}
	}
	
	private void saveState() {
		String type = (String) typeSpinner.getSelectedItem();
		String title = titleText.getText().toString();
		String time = timeText.getText().toString();
		
		ContentValues values = new ContentValues();
		values.put(PieceTable.COLUMN_TIME, Integer.parseInt(time));
		values.put(PieceTable.COLUMN_TITLE, title);
		values.put(PieceTable.COLUMN_TYPE, type);
		
		if (pieceUri == null) {
			pieceUri = getContentResolver().insert(PracticeContentProvider.CONTENT_URI_PIECE, values);
		} else {
			Log.w("EditText", PracticeContentProvider.CONTENT_URI_PIECE + "/" + pieceUri.toString() + " I dunno...");
			getContentResolver().update(pieceUri, values, null, null);
		}
	}
}
