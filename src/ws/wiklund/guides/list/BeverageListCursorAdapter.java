package ws.wiklund.guides.list;

import ws.wiklund.guides.R;
import ws.wiklund.guides.util.BitmapManager;
import ws.wiklund.guides.util.ViewHelper;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SectionIndexer;
import android.widget.SimpleCursorAdapter;

import com.woozzu.android.util.StringMatcher;

public class BeverageListCursorAdapter extends SimpleCursorAdapter implements SectionIndexer {
	private LayoutInflater inflator;
	private String sections;

	public BeverageListCursorAdapter(Context context, Cursor c) {
		super(context, R.layout.item, c, new String[] {"thumb", "name", "country_id", "year", "rating"}, new int[] {android.R.id.icon, android.R.id.text1});

    	sections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		
		inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		BitmapManager.INSTANCE.setPlaceholder(BitmapFactory.decodeResource(context.getResources(), R.drawable.icon));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {		
		Cursor c = getCursor();
		
		if (c.moveToPosition(position)) {
			convertView = ViewHelper.getView(inflator, R.layout.item, convertView, c);
		}

			
		return convertView;
	}
	
	@Override
	public int getPositionForSection(int section) {
		// If there is no item for current section, previous section will be selected
		for (int i = section; i >= 0; i--) {
			for (int j = 0; j < getCount(); j++) {
				Cursor c = (Cursor) getItem(j);
				
				if (i == 0) {
					return 0;
				} else {
					if (StringMatcher.match(String.valueOf(ViewHelper.getBeverageFromCursor(c).getName().charAt(0)), String.valueOf(sections.charAt(i)))) {
						return j;
					}
				}
			}
		}
		
		return 0;
	}

	@Override
	public int getSectionForPosition(int position) {
		return 0;
	}

	@Override
	public Object[] getSections() {
		String[] s = new String[sections.length()];
		for (int i = 0; i < sections.length(); i++){
			s[i] = String.valueOf(sections.charAt(i));
		}
		
		return s;
	}
	
}
