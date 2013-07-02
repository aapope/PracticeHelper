package com.example.practicehelper;




import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class PracticeRoutine extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {
	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;
	private static final int INSERT_ID = Menu.FIRST;
	private SimpleCursorAdapter adapter;
	private Cursor itemsCursor;
	static final String[] PROJECTION = new String[] { 
		PieceTable._ID, 
		PieceTable.COLUMN_TITLE,
		PieceTable.COLUMN_TIME
	};
	//static final String SELECTION = "(" + PracticeDbAdapter.KEY_TITLE + " != '')";
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	//TODO: Make this view a slider on top (or something) that contains a metronome, 
    	//a timer (maybe have a "start practicing" button), etc.
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_routine);
        setFooter();
        fillData();
        registerForContextMenu(getListView());
    }

    private void setFooter() {
    	View footerView = getLayoutInflater().inflate(R.layout.add_practice_item, null);
    	getListView().addFooterView(footerView);
    }

    private void fillData() {
    	String[] from = new String[] { PieceTable.COLUMN_TITLE, PieceTable.COLUMN_TIME };
    	int[] to = new int[] { R.id.practice_text, R.id.practice_time };
    	getLoaderManager().initLoader(0, null, this);
    	adapter = new SimpleCursorAdapter(this, R.layout.practice_item, null, from, to, 0);
    	setListAdapter(adapter);
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
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    	AdapterView.AdapterContextMenuInfo info;

        // Tries to get the position of the item in the ListView that was long-pressed.
        try {
            // Casts the incoming data object into the type for AdapterView objects.
            info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            // If the menu object can't be cast, logs an error.
            return;
        }
        
        Cursor cursor = (Cursor) getListAdapter().getItem(info.position);
    	if (cursor == null) {
    		return;
    	}
    	
        MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.piece_list_context, menu);
    	
    	menu.setHeaderTitle(cursor.getString(cursor.getColumnIndex(PieceTable.COLUMN_TITLE)));
    	
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterContextMenuInfo info;
    	try {
    		info = (AdapterContextMenuInfo) item.getMenuInfo();
    	} catch (ClassCastException e) {
    		return false;
    	}
    	
    	//use contentprovider to find the things of interest.
    	Uri pieceUri = Uri.parse(PracticeContentProvider.CONTENT_URI_PIECE + "/" + info.id);
    	ContentValues values;
        switch(item.getItemId()) {
            case R.id.delete_item:
            	getContentResolver().delete(pieceUri, null, null);
            	break;
            case R.id.move_up:
            	values = new ContentValues();
            	values.put(PieceTable.COLUMN_ORDER, PracticeContentProvider.MOVE_UP);
            	getContentResolver().update(pieceUri, values, null, null);
            	break;
            case R.id.move_down:
            	values = new ContentValues();
            	values.put(PieceTable.COLUMN_ORDER, PracticeContentProvider.MOVE_DOWN);
            	getContentResolver().update(pieceUri, values, null, null);
            	break;
            case R.id.move_top:
            	values = new ContentValues();
            	values.put(PieceTable.COLUMN_ORDER, PracticeContentProvider.MOVE_TOP);
            	getContentResolver().update(pieceUri, values, null, null);
            	break;
            case R.id.move_bottom:
            	values = new ContentValues();
            	values.put(PieceTable.COLUMN_ORDER, PracticeContentProvider.MOVE_BOTTOM);
            	getContentResolver().update(pieceUri, values, null, null);
            	break;
            default:
            	return false;
        }
        
        fillData();
        return true;
    }
    
    private void createItem() {
    	Intent i = new Intent(this, EditItem.class);
    	startActivity(i);
    }
    
    public void createItem(View v) {
    	createItem();
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	super.onListItemClick(l, v, position, id);
    	Intent i = new Intent(this, PieceDetail.class);
    	Uri pieceUri = Uri.parse(PracticeContentProvider.CONTENT_URI_PIECE + "/" + id);
    	i.putExtra(PracticeContentProvider.CONTENT_ITEM_TYPE_PIECE, pieceUri);
    	startActivity(i);
    }
     
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		//maybe add a selection after projection?
		return new CursorLoader(this, PracticeContentProvider.CONTENT_URI_PIECE, PROJECTION, null, null, PieceTable.COLUMN_ORDER);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		adapter.swapCursor(arg1);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		adapter.swapCursor(null);
	}
    
}
