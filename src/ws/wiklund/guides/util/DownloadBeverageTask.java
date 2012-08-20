package ws.wiklund.guides.util;

import java.io.IOException;

import ws.wiklund.guides.R;
import ws.wiklund.guides.bolaget.SystembolagetParser;
import ws.wiklund.guides.model.Beverage;
import ws.wiklund.guides.model.BeverageTypes;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class DownloadBeverageTask extends AsyncTask<String, Void, Beverage> {
	private Activity activity;
	
	@SuppressWarnings("rawtypes")
	private Class activityClass;
	private BeverageTypes types;

	private String no;
	private String errorMsg;
	
	private ProgressDialog dialog;

	@SuppressWarnings("rawtypes")
	public DownloadBeverageTask(Activity context, Class activityClass, BeverageTypes types) {
		this.activity = context;
		this.activityClass = activityClass;
		this.types = types;
	}

	@Override
	protected Beverage doInBackground(String... no) {
		this.no = no[0];

        try {
			if(this.no == null) {
	        	Log.w(DownloadBeverageTask.class.getName(), "Failed to get info for wine,  no is null");		        	
	        	errorMsg = activity.getString(R.string.genericParseError);
			} else {
				return SystembolagetParser.parseResponse(this.no, types);
			}
		} catch (IOException e) {
        	Log.w(DownloadBeverageTask.class.getName(), "Failed to get info for wine with no: " + this.no, e);
        	errorMsg = activity.getString(R.string.genericParseError);
		}

        return null;
	}

	@Override
	protected void onPostExecute(Beverage beverage) {
		Intent intent = new Intent(activity, activityClass);

		if (beverage != null) {
			intent.putExtra("ws.wiklund.guides.model.Beverage", beverage);
			activity.startActivityForResult(intent, 0);
		} else {
			Toast.makeText(activity, errorMsg == null ? String.format(activity.getString(R.string.missingNoError), this.no) : errorMsg, Toast.LENGTH_SHORT).show();
			errorMsg = null;
		}
		
		dialog.dismiss();
		super.onPostExecute(beverage);
	}

	@Override
	protected void onPreExecute() {
		dialog = new ProgressDialog(activity, R.style.CustomDialog);
		dialog.setMessage("Vänligen vänta...");
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
		dialog.show();

		super.onPreExecute();
	}

}
