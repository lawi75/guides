package ws.wiklund.guides.util;

import ws.wiklund.guides.R;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public abstract class SelectableAdapter extends ArrayAdapter<Selectable>{
	private LayoutInflater layoutInflater;
	private int textViewResourceId;
	
	public SelectableAdapter(Context context, int textViewResourceId, LayoutInflater layoutInflater) {
		super(context, textViewResourceId);
		this.textViewResourceId = textViewResourceId;
		this.layoutInflater = layoutInflater;
	}
	          
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	@Override
	public boolean areAllItemsEnabled() {
        return false;
    }

	@Override
    public boolean isEnabled(int position) {
		Selectable item = getItem(position);
    	if(item.getAction() == Selectable.REMOVE_ACTION) {
    		return isAvailableInCellar();
    	}
    	
		return true;
    }
    
	public abstract boolean isAvailableInCellar();

	public View getCustomView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		
		if(row == null) {
			row=layoutInflater.inflate(textViewResourceId, parent, false);
		}
		
		Selectable s = getItem(position);
		
		TextView label=(TextView)row.findViewById(R.id.spinner_header);
		label.setText(s.getHeader());
		ImageView icon=(ImageView)row.findViewById(R.id.spinner_image);
		icon.setImageResource(s.getDrawable());
	    
		if (s.getAction() == Selectable.REMOVE_ACTION && !isEnabled(position)) {
			label.setTextColor(Color.GRAY);
			row.setBackgroundColor(Color.LTGRAY);
		} else {
			label.setTextColor(Color.BLACK);
			row.setBackgroundColor(Color.WHITE);
		}
		
		return row;		    
	}

}
