package ws.wiklund.guides.db;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public abstract class CellarProvider extends ContentProvider {
    protected static final String CELLAR_BASE_PATH = "cellar";

	public static final int CELLAR = 100;
    public static final int CELLAR_ID = 105;
    public static final int BEVERAGE_ID = 110;

    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/mt-cellar";
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/mt-cellar";

	public abstract UriMatcher getUriMatcher();
	public abstract BeverageDatabaseHelper getDatabase();
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = getUriMatcher().match(uri);
        SQLiteDatabase sqlDB = getDatabase().getWritableDatabase();
        int rowsAffected = 0;
        switch (uriType) {
        	case CELLAR:
        		rowsAffected = sqlDB.delete(BeverageDatabaseHelper.CELLAR_TABLE, selection, selectionArgs);
        		break;
        	case CELLAR_ID:
        		String id = uri.getLastPathSegment();
        		if (TextUtils.isEmpty(selection)) {
        			rowsAffected = sqlDB.delete(BeverageDatabaseHelper.CELLAR_TABLE, "_id =" + id, null);
        		} else {
        			rowsAffected = sqlDB.delete(BeverageDatabaseHelper.CELLAR_TABLE, selection + " and _id = " + id, selectionArgs);
        		}
        		break;
        	default:
        		throw new IllegalArgumentException("Unknown or Invalid URI " + uri);
        }
        
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
	}

	@Override
	public String getType(Uri uri) {
	    int uriType = getUriMatcher().match(uri);
        switch (uriType) {
	        case CELLAR:
	            return CONTENT_TYPE;
	        case CELLAR_ID:
	            return CONTENT_ITEM_TYPE;
	        case BEVERAGE_ID:
	            return CONTENT_ITEM_TYPE;
	        default:
	            return null;
        }
    }

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = getUriMatcher().match(uri);
		if (uriType != CELLAR) {
			throw new IllegalArgumentException("Invalid URI for insert");
		}
		
		SQLiteDatabase sqlDB = getDatabase().getWritableDatabase();
		long newID = sqlDB.insert(BeverageDatabaseHelper.CELLAR_TABLE, null, values);
		if (newID > 0) {
			Uri newUri = ContentUris.withAppendedId(uri, newID);
			getContext().getContentResolver().notifyChange(uri, null);
			return newUri;
		} else {
			throw new SQLException("Failed to insert row into " + uri);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {		
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
    	queryBuilder.setTables(BeverageDatabaseHelper.CELLAR_TABLE);

    	int uriType = getUriMatcher().match(uri);
	    switch (uriType) {
	    	case BEVERAGE_ID:
	    		queryBuilder.appendWhere("beverage_id =" + uri.getLastPathSegment());
	        	break;
	    	case CELLAR:
	    		// no filter
	    		break;
	    	default:
	    		throw new IllegalArgumentException("Unknown URI");
	    }

    	Cursor cursor = queryBuilder.query(getDatabase().getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = getUriMatcher().match(uri);
        SQLiteDatabase sqlDB = getDatabase().getWritableDatabase();

        int rowsAffected;

        switch (uriType) {
        	case CELLAR_ID:
        		String id = uri.getLastPathSegment();
        		StringBuilder modSelection = new StringBuilder("_id = " + id);

        		if (!TextUtils.isEmpty(selection)) {
        			modSelection.append(" AND " + selection);
        		}

        		rowsAffected = sqlDB.update(BeverageDatabaseHelper.CELLAR_TABLE, values, modSelection.toString(), null);
        		break;
        	case CELLAR:
        		rowsAffected = sqlDB.update(BeverageDatabaseHelper.CELLAR_TABLE, values, selection, selectionArgs);
        		break;
        	default:
        		throw new IllegalArgumentException("Unknown URI");
        }
        
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }

}
