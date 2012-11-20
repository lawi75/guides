package ws.wiklund.guides.util;

import java.io.IOException;

import ws.wiklund.guides.R;
import ws.wiklund.guides.bolaget.SystembolagetParser;
import ws.wiklund.guides.db.BeverageDatabaseHelper;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class DownloadBeveragesTask extends AsyncTask<String, Void, SearchResult> {
	private BeverageDatabaseHelper helper;
	private Activity activity;
	
	@SuppressWarnings("rawtypes")
	private Class activityClass;

	private String searchWord;
	private String errorMsg;
	
	private long startRead = 0;
	
	private ProgressDialog dialog;

	@SuppressWarnings("rawtypes")
	public DownloadBeveragesTask(BeverageDatabaseHelper helper, Activity activity, Class activityClass) {
		this.helper = helper;
		this.activity = activity;
		this.activityClass = activityClass;
	}

	@Override
	protected SearchResult doInBackground(String... searchWord) {
		this.searchWord = searchWord[0];
		dialog.setMessage(String.format(activity.getString(R.string.systembolaget_wait_msg), new Object[]{this.searchWord}));

		try {
			if(this.searchWord == null) {
	        	Log.w(DownloadBeveragesTask.class.getName(), "Failed to get info for wine,  searchWord is null");		        	
	        	errorMsg = activity.getString(R.string.genericParseError);
			} else {
				startRead = System.currentTimeMillis();
				return SystembolagetParser.searchBeverages(this.searchWord, helper);
			}
		} catch (IOException e) {
        	Log.w(DownloadBeveragesTask.class.getName(), "Failed to search for: " + this.searchWord + " after " + (System.currentTimeMillis() - startRead + " milli secs"), e);
        	errorMsg = activity.getString(R.string.genericParseError);
		}

        return null;
	}

	@Override
	protected void onPostExecute(SearchResult searchResult) {
		Intent intent = new Intent(activity, activityClass);

		if (searchResult != null) {
			Log.d(DownloadBeveragesTask.class.getName(), "Got SearchResult after " + (System.currentTimeMillis() - startRead) + " milli secs");
			
			intent.putExtra("ws.wiklund.guides.util.SearchResult", searchResult);
			intent.putExtra("ws.wiklund.guides.util.SearchResult.SearchWord", searchWord);
			
			activity.startActivityForResult(intent, 0);
		} else {
			Toast.makeText(activity, errorMsg == null ? String.format(activity.getString(R.string.missingSearchWordError), this.searchWord) : errorMsg, Toast.LENGTH_SHORT).show();
			errorMsg = null;
		}
		
		dialog.dismiss();
		super.onPostExecute(searchResult);
	}

	@Override
	protected void onPreExecute() {
		dialog = new ProgressDialog(activity, R.style.CustomDialog);
		dialog.setTitle(activity.getString(R.string.wait));
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
		dialog.show();

		super.onPreExecute();
	}

}
