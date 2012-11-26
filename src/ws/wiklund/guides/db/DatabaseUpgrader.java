package ws.wiklund.guides.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public abstract class DatabaseUpgrader {
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

	public abstract int upgrade(int oldVersion, int newVersion);
	public abstract void createAndPopulateBeverageTypeTable(SQLiteDatabase db);
	
	protected void insertBeverageType(int id, String name) {
	    ContentValues values = new ContentValues();
        
	    values.put("_id", id);
	    values.put("name", name);
        
        //TODO Other??
        db.insert(BeverageDatabaseHelper.BEVERAGE_TYPE_TABLE, null, values);
	}
	
	protected void updateBeverageTypeIdInBeverageTable(int currentId, int newId) {
	    ContentValues values = new ContentValues();
        
	    values.put("type", newId);
        
		db.update(BeverageDatabaseHelper.BEVERAGE_TABLE, values, "type=?", new String[]{String.valueOf(currentId)});
	}
	
	protected void insertImageColumnToBeverage() {
		//Create image column in BEVERAGE table
		db.execSQL("ALTER TABLE beverage ADD COLUMN image text");
	}
	
}
