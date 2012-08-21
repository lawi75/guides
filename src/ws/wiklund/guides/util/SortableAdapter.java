package ws.wiklund.guides.util;

import java.util.List;

import ws.wiklund.guides.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SortableAdapter extends ArrayAdapter<Sortable>{
	private List<Sortable> sortableItems;
	private LayoutInflater inflater;
	
	public SortableAdapter(Context context, int textViewResourceId, List<Sortable> sortableItems, LayoutInflater inflater) {
		super(context, textViewResourceId, sortableItems);
		
		this.sortableItems = sortableItems;
		this.inflater= inflater; 
	}
	     
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	public View getCustomView(int position, View convertView, ViewGroup parent) {
		Sortable s = sortableItems.get(position);
		
		View row=inflater.inflate(R.layout.spinner_row, parent, false);
		TextView label=(TextView)row.findViewById(R.id.spinner_header);
		label.setText(s.getHeader());
		TextView sub=(TextView)row.findViewById(R.id.spinner_sub);
		sub.setText(s.getSub());
		ImageView icon=(ImageView)row.findViewById(R.id.spinner_image);
		icon.setImageResource(s.getDrawable());
	    
		return row;		    
	}
	
}
