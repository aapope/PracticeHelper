package com.example.practicehelper;




import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class PracticeRoutine extends ListActivity { // implements LoaderManager.LoaderCallbacks<Cursor>{
	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;
	
	private static final int INSERT_ID = Menu.FIRST;
	
	private PracticeDbAdapter dbHelper;
	private SimpleCursorAdapter adapter;
	private Cursor itemsCursor;
	//static final String[] PROJECTION = new String[] {PracticeDbAdapter._ID, PracticeDbAdapter.KEY_TITLE, PracticeDbAdapter.KEY_TIME};
	//static final String SELECTION = "(" + PracticeDbAdapter.KEY_TITLE + " != '')";
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_routine);
        dbHelper = new PracticeDbAdapter(this);
        dbHelper.open();
        setFooter();
        fillData();
    }

    private void setFooter() {
    	View footerView = getLayoutInflater().inflate(R.layout.add_practice_item, null);
    	getListView().addFooterView(footerView);
    }
    //Do this with a cursor loader, eventually
    private void fillData() {
    	itemsCursor = dbHelper.fetchAllRows();
    	startManagingCursor(itemsCursor);
    	
    	String[] from = new String[] { PracticeDbAdapter.KEY_TITLE, PracticeDbAdapter.KEY_TIME};
    	int[] to = new int[] { R.id.practice_text, R.id.practice_time };
    	adapter = new SimpleCursorAdapter(this, R.layout.practice_item, itemsCursor, from, to);
    	//adapter = new SimpleCursorAdapter(this, R.layout.practice_item, null, from, to, 0);
    	setListAdapter(adapter);
    	//getLoaderManager().initLoader(0, null, this);
    	
    	/**ArrayList<String> lines = new ArrayList<String>();
    	lines.add("line 1");
    	lines.add("line 2");
    	setListAdapter(new ArrayAdapter<String>(this, R.layout.practice_item, lines));**/
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	boolean result = super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.add_item);
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
        case INSERT_ID:
            createItem();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void createItem() {
    	Intent i = new Intent(this, AddItem.class);
    	startActivityForResult(i, ACTIVITY_CREATE);
        /**String noteName = "Note " + mNoteNumber++;
        dbHelper.createRow(noteName, "", 15);
        fillData();**/
    }
    
    public void createItem(View v) {
    	createItem();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	if (resultCode == RESULT_OK) {
    		super.onActivityResult(requestCode, resultCode, intent);
    		Bundle extras = intent.getExtras();
    		switch(requestCode) {
    		case ACTIVITY_CREATE:
    			String title = extras.getString(PracticeDbAdapter.KEY_TITLE);
    			String type = extras.getString(PracticeDbAdapter.KEY_TYPE);
    			int time = Integer.parseInt(extras.getString(PracticeDbAdapter.KEY_TIME));
    			dbHelper.createRow(title, type, time);
    			fillData();
    			break;
    		case ACTIVITY_EDIT:
    			break;
    		}
    	}
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	super.onListItemClick(l, v, position, id);
    	Cursor c = itemsCursor;
    	
    	c.moveToPosition(position);
    	Intent i = new Intent(this, PieceDetail.class);
    	String item_id = c.getString(c.getColumnIndexOrThrow(PracticeDbAdapter._ID));
    	startActivity(i);
    }
     
    /**
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(this, ContactsContract.Data.CONTENT_URI, PROJECTION, SELECTION, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		adapter.swapCursor(arg1);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		adapter.swapCursor(null);
	}**/
    
}
