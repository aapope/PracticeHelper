package com.example.practicehelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class AddItem extends Activity {
	private EditText titleText;
	private EditText timeText;
	private Spinner typeSpinner;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_layout);
		setTitle(R.string.add_item);
		
		titleText = (EditText) findViewById(R.id.add_piece_title);
		timeText = (EditText) findViewById(R.id.add_piece_time);
		typeSpinner = (Spinner) findViewById(R.id.add_piece_spinner);
		
		Button confirmButton = (Button) findViewById(R.id.add_piece_confirm);
		//Bundle extras = getIntent().getExtras();
		
		confirmButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Bundle bundle = new Bundle();
				
				bundle.putString(PracticeDbAdapter.KEY_TITLE, titleText.getText().toString());
				bundle.putString(PracticeDbAdapter.KEY_TIME, timeText.getText().toString());
				bundle.putString(PracticeDbAdapter.KEY_TYPE, typeSpinner.getSelectedItem().toString());
				
				Intent intent = new Intent();
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		
		Button cancelButton = (Button) findViewById(R.id.add_piece_cancel);
		
		cancelButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				setResult(RESULT_CANCELED, null);
				finish();
			}
		});
	}
}
