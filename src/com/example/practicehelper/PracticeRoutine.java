package com.example.practicehelper;




import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SimpleCursorAdapter;

public class PracticeRoutine extends ListActivity { // implements LoaderManager.LoaderCallbacks<Cursor>{
	private static final int ACTIVITY_CREATE = 0;
	
	private static final int INSERT_ID = Menu.FIRST;
	
	private PracticeDbAdapter dbHelper;
	private SimpleCursorAdapter adapter;
	//static final String[] PROJECTION = new String[] {PracticeDbAdapter._ID, PracticeDbAdapter.KEY_TITLE, PracticeDbAdapter.KEY_TIME};
	//static final String SELECTION = "(" + PracticeDbAdapter.KEY_TITLE + " != '')";
	private int mNoteNumber = 1;
	
	
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
    	Cursor c = dbHelper.fetchAllRows();
    	startManagingCursor(c);
    	
    	String[] from = new String[] { PracticeDbAdapter.KEY_TITLE, PracticeDbAdapter.KEY_ORDER};
    	int[] to = new int[] { R.id.practice_text, R.id.practice_time };
    	adapter = new SimpleCursorAdapter(this, R.layout.practice_item, c, from, to);
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
    
    /**
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		return new CursorLoader(this, ContactsContract.Data.CONTENT_URI, PROJECTION, SELECTION, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		// TODO Auto-generated method stub
		adapter.swapCursor(arg1);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		adapter.swapCursor(null);
	}**/
    
}
