package ws.wiklund.guides.db;

import ws.wiklund.guides.model.BeverageType;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public abstract class DatabaseUpgrader {
	//Available DB versions
	protected static final int VERSION_1 = 1;
	protected static final int VERSION_2 = 2;
	protected static final int VERSION_3 = 3;
	protected static final int VERSION_4 = 4;
	protected static final int VERSION_5 = 5;
	protected static final int VERSION_6 = 6;
	protected static final int VERSION_7 = 7;

	protected SQLiteDatabase db;

	public DatabaseUpgrader(SQLiteDatabase db) {
		this.db = db;
	}

	public void doUpdate(int oldVersion, int newVersion) {
		Log.d(DatabaseUpgrader.class.getName(), "Will upgrade from DB version [" + oldVersion + "] to DB version [" + newVersion + "]");
		
		if(oldVersion < newVersion) {
			int version = upgrade(oldVersion, newVersion);
			Log.d(DatabaseUpgrader.class.getName(), "Upgrade DB from version [" + oldVersion + "] to version [" + version + "]");
		}
	}
	
	public static int getLatestDBVersion() {
		return VERSION_7;
	}

	public abstract int upgrade(int oldVersion, int newVersion);
	public abstract void createAndPopulateBeverageTypeTable(SQLiteDatabase db);
	
	protected void addOtherBeverageType() {
	    ContentValues values = new ContentValues();
        
	    values.put("_id", BeverageType.OTHER.getId());
	    values.put("name", BeverageType.OTHER.getName());
        
        db.insert(BeverageDatabaseHelper.BEVERAGE_TYPE_TABLE, null, values);
	}

	protected void insertBeverageType(int id, String name) {
	    ContentValues values = new ContentValues();
        
	    values.put("_id", id);
	    values.put("name", name);
        
        db.insert(BeverageDatabaseHelper.BEVERAGE_TYPE_TABLE, null, values);
	}
	
	protected void updateBeverageTypeIdInBeverageTable(int currentId, int newId) {
	    ContentValues values = new ContentValues();
        
	    values.put("type", newId);
        
		db.update(BeverageDatabaseHelper.BEVERAGE_TABLE, values, "type=?", new String[]{String.valueOf(currentId)});
	}
	
	protected void insertImageColumnToBeverage() {
		Cursor c = db.rawQuery("SELECT * FROM beverage LIMIT 0,1", null);
		
		int idx = c.getColumnIndex("image");
		
		if (idx == -1) {
			//Create image column in BEVERAGE table if it doesn't exist
			db.execSQL("ALTER TABLE beverage ADD COLUMN image text");
		}
		
		c.close();
	}
	
}
