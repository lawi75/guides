package ws.wiklund.guides.util;

import ws.wiklund.guides.R;
import ws.wiklund.guides.bolaget.SystembolagetParser;
import ws.wiklund.guides.model.Beverage;
import ws.wiklund.guides.util.facebook.FacebookConnector;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class FacebookPostMessageTask extends AsyncTask<Void, Void, Void> {
	private final Handler facebookHandler = new Handler();

	private Context context;
	private FacebookConnector connector;
	private Beverage beverage;
	private String header;

	final Runnable updateFacebookNotification = new Runnable() {
        public void run() {
        	Toast.makeText(context, context.getString(R.string.facebookPosted), Toast.LENGTH_LONG).show();
        }
    };

	public FacebookPostMessageTask(Context context, FacebookConnector connector, Beverage beverage, String header) {
		this.context = context;
		this.connector = connector;
		this.beverage = beverage;
		this.header = header;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		Bundle bundle = new Bundle();
		bundle.putString("picture", SystembolagetParser.BASE_URL + beverage.getThumb());
		bundle.putString("name", header);
		bundle.putString("link", SystembolagetParser.BASE_URL + "/" + beverage.getNo());
		
		StringBuilder builder = new StringBuilder(context.getString(R.string.recommend_wine));
		builder.append(" ").append(beverage.getName());
		
		if(beverage.getNo() != -1) {
			builder.append(" (" + beverage.getNo() + ")"); 
		}
		
		if(beverage.getRating() != -1) {
			builder.append(" ").append(context.getString(R.string.recommend_wine1)).append(" ");					
			builder.append(ViewHelper.getDecimalStringFromNumber(beverage.getRating())).append(" ").append(context.getString(R.string.recommend_wine2));
		}
		
		bundle.putString("description", builder.toString());

		connector.postMessageOnWall(bundle);
		facebookHandler.post(updateFacebookNotification);
		
		return null;
	}
	
}