package ws.wiklund.guides.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

public class BeverageListCursorAdapter extends SimpleCursorAdapter implements SectionIndexer {
	private LayoutInflater inflator;
	private Map<String, Integer> alphaIndexer;
	private String[] sections;

	public BeverageListCursorAdapter(Context context, Cursor c) {
		super(context, R.layout.item, c, new String[] {"thumb", "name", "country_id", "year", "rating"}, new int[] {android.R.id.icon, android.R.id.text1});

		alphaIndexer = new HashMap<String, Integer>();
		
		inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		BitmapManager.INSTANCE.setPlaceholder(BitmapFactory.decodeResource(context.getResources(), R.drawable.icon));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {		
		Cursor c = getCursor();
		
		if (c.moveToPosition(position)) {
			convertView = ViewHelper.getView(inflator, R.layout.item, convertView, c);

			alphaIndexer.put(ViewHelper.getBeverageFromCursor(c).getName().substring(0,1).toUpperCase(), position);
			
			Set<String> sectionLetters = alphaIndexer.keySet();
			 
		    // create a list from the set to sort
            List<String> sectionList = new ArrayList<String>(sectionLetters); 
            Collections.sort(sectionList);
 
            sections = new String[sectionList.size()];
 
            sectionList.toArray(sections);
		}

			
		return convertView;
	}
	
	@Override
	public int getPositionForSection(int section) {
		return alphaIndexer.get(sections[section]);
	}

	@Override
	public int getSectionForPosition(int position) {
		return 1;
	}

	@Override
	public Object[] getSections() {
		return sections;
	}
	
}
