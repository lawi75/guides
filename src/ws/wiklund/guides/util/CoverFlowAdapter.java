package ws.wiklund.guides.util;

import ws.wiklund.guides.R;
import ws.wiklund.guides.db.BeverageDatabaseHelper;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class CoverFlowAdapter extends BaseAdapter {
	private LayoutInflater inflator;

	protected BeverageDatabaseHelper helper;
	protected SQLiteDatabase db;
	protected Cursor cursor;

	public CoverFlowAdapter(Context c, BeverageDatabaseHelper helper) {
		this.helper = helper;
		
		inflator = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);		
		
		BitmapManager.INSTANCE.setPlaceholder(BitmapFactory.decodeResource(c.getResources(), R.drawable.icon));
	}

	public int getOptimalSelection() {
		int c = getCount();

		if(c > 4) {
			return 4;
		} else if(c > 0) {
			return c - 1;
		}

		return 0;
	}

	public void destroy() {
		if (cursor != null) {
			cursor.close();
		}

		if (db != null) {
			db.close();
		}
	}

	public int getCount() {
		return getNewOrReuseCursor().getCount();
	}

	public Cursor getItem(int position) {
		Cursor c = getNewOrReuseCursor();
		
		if (c.moveToPosition(position)) {
			return c;
		}

		return null;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		return ViewHelper.getView(inflator, R.layout.coveritem, convertView, getItem(position));
	}

	protected Cursor getNewOrReuseCursor() {
		if (db == null || !db.isOpen()) {
			db = helper.getReadableDatabase();
			cursor = db.rawQuery(BeverageDatabaseHelper.SQL_SELECT_ALL_BEVERAGES_INCLUDING_NO_IN_CELLAR, null);
		}
		
		return cursor;
	}

}
