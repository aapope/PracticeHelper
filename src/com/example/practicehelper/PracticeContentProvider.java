package com.example.practicehelper;

import java.util.Arrays;
import java.util.HashSet;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class PracticeContentProvider extends ContentProvider {
	
	private PracticeDatabaseHelper database;
	
	private static final int PIECES = 10;
	private static final int PIECES_ID = 20;
	private static final int DETAILS = 30;
	private static final int DETAILS_ID = 40;
	
	private static final String AUTHORITY = "com.example.practicehelper.contentprovider";
	
	private static final String BASE_PATH_PIECE = "pieces";
	private static final String BASE_PATH_DETAIL = "details";
	
	public static final Uri CONTENT_URI_PIECE = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH_PIECE);
	public static final Uri CONTENT_URI_DETAIL = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH_DETAIL);
	
	public static final String CONTENT_TYPE_PIECE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/pieces";
	public static final String CONTENT_ITEM_TYPE_PIECE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/piece";
	public static final String CONTENT_TYPE_DETAIL = ContentResolver.CURSOR_DIR_BASE_TYPE + "/details";
	public static final String CONTENT_ITEM_TYPE_DETAIL = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/detail";
	
	
	
	public static final int MOVE_TOP = 1;
	public static final int MOVE_BOTTOM = 2;
	public static final int MOVE_UP = 3;
	public static final int MOVE_DOWN = 4;
	
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH_PIECE, PIECES);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH_PIECE + "/#", PIECES_ID);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH_DETAIL, DETAILS);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH_DETAIL + "/#", DETAILS_ID);		
	}
	
	private static int maxOrder;
	
	
	public PracticeContentProvider() {
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		//TODO: add other table support
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int rowsDeleted = 0;
		String id;
		
		switch (uriType) {
		case PIECES:
			//TODO: order for multiple?
			rowsDeleted = sqlDB.delete(PieceTable.TABLE, selection, selectionArgs);
			break;
		case DETAILS:
			rowsDeleted = sqlDB.delete(DetailsTable.TABLE, selection, selectionArgs);
			break;
		case DETAILS_ID:
			id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(DetailsTable.TABLE, 
						DetailsTable._ID + "=" + id, null);
			} else {
				rowsDeleted = sqlDB.delete(DetailsTable.TABLE, 
						DetailsTable._ID + "=" + id
						+ " and " + selection, selectionArgs);
			}
			break;
		case PIECES_ID:
			id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				//move everything below up
				String setOthers = "UPDATE " + PieceTable.TABLE + " SET " + PieceTable.COLUMN_ORDER + "=" + PieceTable.COLUMN_ORDER + "-1 WHERE " 
						+ PieceTable.COLUMN_ORDER + ">(SELECT " + PieceTable.COLUMN_ORDER + " FROM " +
		    			PieceTable.TABLE  + " WHERE " + PieceTable._ID + "=" + id + ")";
				sqlDB.execSQL(setOthers);
				rowsDeleted = sqlDB.delete(PieceTable.TABLE, 
						PieceTable._ID + "=" + id, null);
				
				maxOrder--;
			} else {
				//TODO: maybe fix this?
				return 0;
				//rowsDeleted = sqlDB.delete(PieceTable.TABLE, 
					//	PieceTable._ID + "=" + id
						//+ " and " + selection, selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri + " Type: delete");
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		long id = 0;
		
		switch (uriType) {
		case PIECES:
			//first, complete the values!
			values.put(PieceTable.COLUMN_DATEADDED, System.currentTimeMillis());
			values.put(PieceTable.COLUMN_ORDER, maxOrder);
			
			id = sqlDB.insert(PieceTable.TABLE, null, values);
			
			maxOrder++;
			
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.parse(BASE_PATH_PIECE + "/" + id);
		case DETAILS:
			id = sqlDB.insert(DetailsTable.TABLE, null, values);
			
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.parse(BASE_PATH_DETAIL + "/" + id);
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri + " Type: insert " + uriType);
		}
	}

	@Override
	public boolean onCreate() {
		database = new PracticeDatabaseHelper(getContext());
		setMax();
		return false;
	}
	
	public void setMax() {
		SQLiteDatabase d = database.getReadableDatabase();
		
		Cursor orderC = d.rawQuery("SELECT MAX(" + PieceTable.COLUMN_ORDER + ") FROM " + PieceTable.TABLE, null);
		
		if (orderC != null) {
			orderC.moveToFirst();
			maxOrder = orderC.getInt(0) + 1;
		} else {
			maxOrder = 1;
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		int uriType = sURIMatcher.match(uri);
		
		switch (uriType) {
		case PIECES: case PIECES_ID:
			return queryPiece(uri, projection, selection, selectionArgs, sortOrder);
		case DETAILS: case DETAILS_ID:
			return queryDetail(uri, projection, selection, selectionArgs, sortOrder);
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}
	
	private Cursor queryPiece(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		checkColumns(projection, PieceTable.TABLE);
		
		queryBuilder.setTables(PieceTable.TABLE);
		
		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case PIECES:
			break;
		case PIECES_ID:
			queryBuilder.appendWhere(PieceTable._ID + "="
					+ uri.getLastPathSegment());
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri + " Type: query " + uriType);
		}
		
		SQLiteDatabase db = database.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		
		return cursor;
	}
	
	private Cursor queryDetail(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		checkColumns(projection, DetailsTable.TABLE);
		
		queryBuilder.setTables(DetailsTable.TABLE);
		
		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case DETAILS:
			break;
		case DETAILS_ID:
			queryBuilder.appendWhere(PieceTable._ID + "="
					+ uri.getLastPathSegment());
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri + " Type: query " + uriType);
		}
		
		SQLiteDatabase db = database.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case PIECES: case PIECES_ID:
			return updatePiece(uri, values, selection, selectionArgs);
		case DETAILS: case DETAILS_ID:
			return updateDetails(uri, values, selection, selectionArgs);
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}
	
	private int updatePiece(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int rowsUpdated = 0;
		
		switch (uriType) {
		case PIECES:
			//TODO: Let's hope it doesn't try to modify order. Should only update one at a time
			rowsUpdated = sqlDB.update(PieceTable.TABLE, values, selection, selectionArgs);
			break;
		case PIECES_ID:
			String id = uri.getLastPathSegment();
			if (values.containsKey(PieceTable.COLUMN_ORDER)) {
				int orderSwitch = values.getAsInteger(PieceTable.COLUMN_ORDER);
				String putThis, setOthers;
				switch (orderSwitch) {
				case MOVE_TOP:
					putThis= "UPDATE " + PieceTable.TABLE + " SET " + PieceTable.COLUMN_ORDER + "=1 WHERE " + PieceTable._ID + "=" + id;
					setOthers = "UPDATE " + PieceTable.TABLE + " SET " + PieceTable.COLUMN_ORDER + "=" + PieceTable.COLUMN_ORDER + "+1 "
		    			+ "WHERE " + PieceTable.COLUMN_ORDER + ">=0 AND " + PieceTable.COLUMN_ORDER + "<(SELECT " + PieceTable.COLUMN_ORDER + " FROM " +
		    			PieceTable.TABLE  + " WHERE " + PieceTable._ID + "=" + id + ")";
					break;
				case MOVE_BOTTOM:
					putThis= "UPDATE " + PieceTable.TABLE + " SET " + PieceTable.COLUMN_ORDER + "=" + Integer.toString(maxOrder-1) + " WHERE " + PieceTable._ID + "=" + id;
					setOthers = "UPDATE " + PieceTable.TABLE + " SET " + PieceTable.COLUMN_ORDER + "=" + PieceTable.COLUMN_ORDER + "-1 "
		    			+ "WHERE " + PieceTable.COLUMN_ORDER + ">(SELECT " + PieceTable.COLUMN_ORDER + " FROM " +
		    			PieceTable.TABLE  + " WHERE " + PieceTable._ID + "=" + id + ")";
					break;
				case MOVE_UP:
					putThis= "UPDATE " + PieceTable.TABLE + " SET " + PieceTable.COLUMN_ORDER + "=" + PieceTable.COLUMN_ORDER + "-1 WHERE " + PieceTable._ID + "=" + id;
					setOthers = "UPDATE " + PieceTable.TABLE + " SET " + PieceTable.COLUMN_ORDER + "=" + PieceTable.COLUMN_ORDER + "+1 "
		    			+ "WHERE " + PieceTable.COLUMN_ORDER + " = (SELECT " + PieceTable.COLUMN_ORDER + " FROM " +
		    			PieceTable.TABLE  + " WHERE " + PieceTable._ID + "=" + id + ")-1";
					
					Log.w("PracticeContentProvider", "IN move up, this is the put string: " + putThis);
					break;
				case MOVE_DOWN:
					putThis= "UPDATE " + PieceTable.TABLE + " SET " + PieceTable.COLUMN_ORDER + "=" + PieceTable.COLUMN_ORDER + "+1 WHERE " + PieceTable._ID + "=" + id;
					setOthers = "UPDATE " + PieceTable.TABLE + " SET " + PieceTable.COLUMN_ORDER + "=" + PieceTable.COLUMN_ORDER + "-1 "
		    			+ "WHERE " + PieceTable.COLUMN_ORDER + " = (SELECT " + PieceTable.COLUMN_ORDER + " FROM " +
				    			PieceTable.TABLE  + " WHERE " + PieceTable._ID + "=" + id + ")+1";
					break;
				default:
					throw new IllegalArgumentException("Unknown order: " + orderSwitch);	
				}
					    	
				sqlDB.execSQL(setOthers);
				sqlDB.execSQL(putThis);
				Log.w("PracticeContentProvider", "Out of thing, sql executed. put Others: " +setOthers);
				rowsUpdated = 1;
			} else {
				if (TextUtils.isEmpty(selection)) {
					rowsUpdated = sqlDB.update(PieceTable.TABLE, 
							values, 
							PieceTable._ID + "=" + id, 
							null);
				} else {
					rowsUpdated = sqlDB.update(PieceTable.TABLE,
							values,
							PieceTable._ID + "=" + id
							+ " and "
							+ selection,
							selectionArgs);
				}
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri + " Type: update " + uriType);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}
	
	public int updateDetails(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int rowsUpdated = 0;
		
		switch (uriType) {
		case DETAILS:
			rowsUpdated = sqlDB.update(DetailsTable.TABLE, values, selection, selectionArgs);
			break;
		case DETAILS_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(DetailsTable.TABLE, 
						values, 
						DetailsTable._ID + "=" + id, 
						null);
			} else {
				rowsUpdated = sqlDB.update(DetailsTable.TABLE,
						values,
						DetailsTable._ID + "=" + id
						+ " and "
						+ selection,
						selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri + " Type: update " + uriType);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}
	
	private void checkColumns(String[] projection, String table) {
		String[] available;
		if (table == PieceTable.TABLE) {
			available = new String[] { PieceTable._ID, PieceTable.COLUMN_DATEADDED,
					PieceTable.COLUMN_ORDER, PieceTable.COLUMN_TIME, PieceTable.COLUMN_TITLE,
					PieceTable.COLUMN_TYPE };
		} else if (table == DetailsTable.TABLE) {
			available = new String[] { DetailsTable._ID, DetailsTable.COLUMN_PIECE_ID,
				DetailsTable.COLUMN_MEASURE_RANGE, DetailsTable.COLUMN_TEMPO_CURRENT,
				DetailsTable.COLUMN_TEMPO_TARGET, DetailsTable.COLUMN_DETAILS };
		} else {
			throw new IllegalArgumentException("Unknown table: " + table);
		}
		
		if (projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException("Unknown columns in projection " + projection[1] + " " + table);
			}
		}
	}
}
