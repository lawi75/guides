package ws.wiklund.guides.db;

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
	
}
