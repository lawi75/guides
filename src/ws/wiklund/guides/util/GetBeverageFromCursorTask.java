package ws.wiklund.guides.util;

import ws.wiklund.guides.R;
import ws.wiklund.guides.model.Beverage;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;

public class GetBeverageFromCursorTask extends AsyncTask<Cursor, Void, Beverage> {
	private ProgressDialog dialog;
	private Activity activity;
	
	@SuppressWarnings("rawtypes")
	private Class activityClass;

	@SuppressWarnings("rawtypes")
	public GetBeverageFromCursorTask(Activity activity, Class activityClass) {
		this.activity = activity;
		this.activityClass = activityClass;
	}

	@Override
	protected Beverage doInBackground(Cursor... cursors) {
		return ViewHelper.getBeverageFromCursor(cursors[0]);
	}

	@Override
	protected void onPostExecute(Beverage bevarage) {
		dialog.dismiss();

		Intent intent = new Intent(activity, activityClass);
		intent.putExtra("ws.wiklund.guides.model.Beverage", bevarage);

		activity.startActivityForResult(intent, 0);
	}

	@Override
	protected void onPreExecute() {
		dialog = new ProgressDialog(activity);
		dialog.setTitle(activity.getString(R.string.wait));
		dialog.setMessage(activity.getString(R.string.fetch_beverage));
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
		dialog.show();
	}

}
