package ws.wiklund.guides.util;

import ws.wiklund.guides.R;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class BasicBootStrapHandler {
	private static final String VERSION_KEY = "version_number";
	
	public static void init(Context context, String preferenceKey) {
		initVersions(context, preferenceKey);
		
		AppRater.appLaunched(context, context.getString(R.string.app_name), context.getString(R.string.play_url));
	}
	
	public static boolean runOnce(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(key, 0);
        if (!prefs.getBoolean("dontshowagain", false)) {
            SharedPreferences.Editor editor = prefs.edit();
            
            editor.putBoolean("dontshowagain", true);
            editor.commit();

            return true;
        }
		
		return false;		
	}
	
	
	private static void initVersions(Context context, String preferenceKey) {
		SharedPreferences sharedPref = context.getSharedPreferences(preferenceKey, Context.MODE_PRIVATE);
		int currentVersionNumber = 0;
		int savedVersionNumber = sharedPref.getInt(VERSION_KEY, 0);

		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			currentVersionNumber = pi.versionCode;
		} catch (Exception e) {
		}

		if (currentVersionNumber > savedVersionNumber) {
			showWhatsNewDialog(context);

			Editor editor = sharedPref.edit();

			editor.putInt(VERSION_KEY, currentVersionNumber);
			editor.commit();
		}
	}
	
	private static void showWhatsNewDialog(Context context) {
    	LayoutInflater inflater = LayoutInflater.from(context);		
        View view = inflater.inflate(R.layout.whatsnew, null);
        
        LinearLayout layoutView = (LinearLayout) view.findViewById(R.id.layout);
        LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        
        String[] headers = context.getString(R.string.versionHeads).split("£");
        String[] messages = context.getString(R.string.versionMessages).split("£");
        
        for(int i=0; i<headers.length; i++) {
            TextView headerView = new TextView(context);
            headerView.setLayoutParams(params);
            
            headerView.setTextColor(context.getResources().getColor(android.R.color.white));
            headerView.setTypeface(null, Typeface.BOLD);
            headerView.setText(headers[i]);
            layoutView.addView(headerView);
            
            TextView messageView = new TextView(context);
            messageView.setLayoutParams(params);
            
            messageView.setTextColor(context.getResources().getColor(android.R.color.white));
            messageView.setText(messages[i]);
            layoutView.addView(messageView);
        }
        
  	  	Builder builder = new AlertDialog.Builder(context);

	  	builder.setView(view).setTitle(context.getString(R.string.whatsnew)).setPositiveButton("OK", new DialogInterface.OnClickListener() {
	  		@Override
	  		public void onClick(DialogInterface dialog, int which) {
	  			dialog.dismiss();
	  		}
	    });
  	
	  	builder.create().show();
	}	

}
