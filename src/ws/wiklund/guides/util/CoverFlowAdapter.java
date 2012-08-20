package ws.wiklund.guides.util;

import ws.wiklund.guides.R;
import ws.wiklund.guides.bolaget.SystembolagetParser;
import ws.wiklund.guides.db.BeverageDatabaseHelper;
import ws.wiklund.guides.model.BeverageTypes;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class CoverFlowAdapter extends BaseAdapter {
	private BeverageDatabaseHelper helper;
	private BeverageTypes types;

	private LayoutInflater inflator;
	private SQLiteDatabase db;
	private Cursor cursor;

	public CoverFlowAdapter(Context c, BeverageDatabaseHelper helper, BeverageTypes types) {
		this.helper = helper;
		this.types = types;
		
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
		ViewHolder holder;
		
		if (convertView == null) {  
			convertView = inflator.inflate(R.layout.coveritem, null);
			
			TextView titleView = (TextView) convertView.findViewById(R.id.itemTitle);  
	        TextView textView = (TextView) convertView.findViewById(R.id.itemText);  
	        TextView typeView = (TextView) convertView.findViewById(R.id.itemType);  
	        ImageView imageView = (ImageView) convertView.findViewById(R.id.itemImage);
	        RatingBar rating = (RatingBar) convertView.findViewById(R.id.itemRatingBar);

	         
	        holder = new ViewHolder();  
	        holder.titleView = titleView;  
	        holder.textView = textView;  
	        holder.imageView = imageView;
	        holder.rating = rating;
	        holder.typeView = typeView;
	         
	        convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag(); 
		}
		
		Cursor c = getItem(position);

		if (c != null) {
			int noBottles = c.getInt(22); 
			StringBuilder name = new StringBuilder(c.getString(1));
			
			if(noBottles > 0) {
				name.append("(").append(c.getInt(22)).append(")");
			}
					
			holder.titleView.setText(name.toString());
			holder.typeView.setText(types.findTypeFromId(c.getInt(3)).toString());
			
			int year = c.getInt(8); 
			holder.textView.setText(c.getString(6) + " " + (year != -1 ? year : ""));
			holder.rating.setRating(c.getFloat(16));

			String u = c.getString(4);			
			if (u != null) {
				String url = u.startsWith("/") ? SystembolagetParser.BASE_URL + u : u;
				holder.imageView.setTag(url);
				BitmapManager.INSTANCE.loadBitmap(url, holder.imageView, 50, 100);
			} else {
				holder.imageView.setImageResource(R.drawable.icon);
			}
		}
		
		return convertView;
	}

	private Cursor getNewOrReuseCursor() {
		if (db == null || !db.isOpen()) {
			db = helper.getReadableDatabase();
			cursor = db.rawQuery(BeverageDatabaseHelper.SQL_SELECT_ALL_BEVERAGES_INCLUDING_NO_IN_CELLAR, null);
		}
		
		return cursor;
	}

}
