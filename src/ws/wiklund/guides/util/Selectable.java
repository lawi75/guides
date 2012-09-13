package ws.wiklund.guides.util;

import ws.wiklund.guides.R;
import ws.wiklund.guides.db.BeverageDatabaseHelper;
import ws.wiklund.guides.model.BaseModel;
import ws.wiklund.guides.model.Cellar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

public class Selectable {
	public static final int DELETE_ACTION = 3234;
	public static final int ADD_ACTION = 5435;
	public static final int REMOVE_ACTION = 1554;
	public static final int REMOVE_THIS_ACTION = 7436;
	
	private String header;
	private int drawable;
	private int action;

	public Selectable(String header, int drawable, int action) {
		this.header = header;
		this.drawable = drawable;
		this.action = action;
	}

	public String getHeader() {
		return header;
	}

	public int getDrawable() {
		return drawable;
	}
	
	public int getAction() {
		return action;
	}

	public void select(final Context context, final BeverageDatabaseHelper helper, final BaseModel model) {
		switch (action) {
			case ADD_ACTION:
				// TODO create add wine to cellar activity
				// Step 1, simple only add one bottle to cellar on click
				Cellar c = new Cellar(model.getId());
				c.setNoBottles(1);
				
				helper.addToOrUpdateCellar(c);
				
				Log.d(Selectable.class.getName(), "Added one bottle of " + model.getName() + " to cellar");
				// Step 2, create activity to be able to add multiple bottles, set
				// reminder, set location
	
				((Notifyable)context).notifyDataSetChanged();
				break;
			case REMOVE_ACTION:
				// Step 1, just assume every row contains JUST one bottle and therefore remove the hole row
				Cellar cellar = helper.getOldestCellarItemForBeverage(model.getId());
				
				if (cellar != null) {
					if (cellar.getNoBottles() == 1) {
						if (!helper.deleteCellar(cellar.getId())) {
							Toast.makeText(context, context.getString(R.string.deleteFailed) + " " + model.getName(), Toast.LENGTH_LONG).show();
						}
						
						((Notifyable)context).notifyDataSetChanged();
					} else {
						// TODO add support for this when it is possible to add more
						// then one bottle per row
					}
				}
	
				// Step 2, create remove beverage from cellar dialog if beverages has been
				// added with different dates or on different locations
				break;
			case DELETE_ACTION:
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
				
				alertDialog.setMessage(String.format(context.getString(R.string.deleteBeverage), model.getName()));
				alertDialog.setCancelable(false);
				alertDialog.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id1) {
								boolean b = helper.deleteBeverage(model.getId());
	
								if (!b) {
									Toast.makeText(context, context.getString(R.string.deleteFailed) + " " + model.getName(), Toast.LENGTH_LONG).show();
								}
								
								((Notifyable)context).notifyDataSetChanged();
							}
						});
	
				alertDialog.setNegativeButton(android.R.string.no,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id1) {
								// Action for 'NO' Button
								dialog.cancel();
							}
						});
	
				AlertDialog alert = alertDialog.create();
	
				// Title for AlertDialog
				alert.setTitle(context.getString(R.string.deleteTitle) + " " + model.getName() + "?");
				// Icon for AlertDialog
				alert.setIcon(R.drawable.icon);
				alert.show();
	
				// Return true to consume the click event. In this case the
				// onListItemClick listener is not called anymore.
				break;
		}
	}

	@Override
	public String toString() {
		return "Selectable [header=" + header + ", drawable=" + drawable
				+ ", action=" + action + "]";
	}

}

