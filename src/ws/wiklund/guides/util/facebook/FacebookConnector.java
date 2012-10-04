package ws.wiklund.guides.util.facebook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import ws.wiklund.guides.R;
import ws.wiklund.guides.bolaget.SystembolagetParser;
import ws.wiklund.guides.model.Beverage;
import ws.wiklund.guides.util.ViewHelper;
import ws.wiklund.guides.util.facebook.SessionEvents.AuthListener;
import ws.wiklund.guides.util.facebook.SessionEvents.LogoutListener;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class FacebookConnector {

	private Facebook facebook = null;
	private Handler handler;
	private Activity activity;
	private SessionListener mSessionListener = new SessionListener();
	
	public FacebookConnector(Activity activity) {
		this.facebook = new Facebook(activity.getString(R.string.facebookAppId));
		
		this.handler = new Handler();
		this.activity=activity;

		SessionStore.restore(facebook, activity);
        SessionEvents.addAuthListener(mSessionListener);
        SessionEvents.addLogoutListener(mSessionListener);
	}
	
	public void login() {
        if (!facebook.isSessionValid()) {
            facebook.authorize(activity, new LoginDialogListener());
        }
    }
	
	public void logout() {
        SessionEvents.onLogoutBegin();
        AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(this.facebook);
        asyncRunner.logout(activity, new LogoutRequestListener());
	}
	
	public void postMessageOnWall(Beverage beverage) {
		Bundle bundle = new Bundle();
		bundle.putString("picture", SystembolagetParser.BASE_URL + beverage.getThumb());
		bundle.putString("name", activity.getString(R.string.recommend_header));
		bundle.putString("link", SystembolagetParser.BASE_URL + "/" + beverage.getNo());
		
		StringBuilder builder = new StringBuilder(activity.getString(R.string.recommend_wine));
		builder.append(" ").append(beverage.getName());
		
		if(beverage.getNo() != -1) {
			builder.append(" (" + beverage.getNo() + ")"); 
		}
		
		if(beverage.getRating() != -1) {
			builder.append(" ").append(activity.getString(R.string.recommend_wine1)).append(" ");					
			builder.append(ViewHelper.getDecimalStringFromNumber(beverage.getRating())).append(" ").append(activity.getString(R.string.recommend_wine2));
		}
		
		bundle.putString("description", builder.toString());

		postMessageOnWall(bundle);
	}

	private void postMessageOnWall(Bundle bundle) {
		if (facebook.isSessionValid()) {
			AsyncFacebookRunner runner = new AsyncFacebookRunner(this.facebook);
			runner.request("me/feed", bundle, "POST", new RequestListener() {
				@Override
				public void onComplete(String response, Object state) {
					handler.post(new Runnable() {
				        public void run() {
				        	Toast.makeText(activity, activity.getString(R.string.facebookPosted), Toast.LENGTH_LONG).show();
				        }
				    });
				}

				@Override
				public void onIOException(IOException e, Object state) {
					Log.w(FacebookConnector.class.getName(), "Failed to post message on Facebook wall", e);
				}

				@Override
				public void onFileNotFoundException(FileNotFoundException e, Object state) {
					Log.w(FacebookConnector.class.getName(), "Failed to post message on Facebook wall", e);
				}

				@Override
				public void onMalformedURLException(MalformedURLException e, Object state) {
					Log.w(FacebookConnector.class.getName(), "Failed to post message on Facebook wall", e);
				}

				@Override
				public void onFacebookError(FacebookError e, Object state) {
					Log.w(FacebookConnector.class.getName(), "Failed to post message on Facebook wall", e);
				}
			}, null);
		}
	}	

    private final class LoginDialogListener implements DialogListener {
        public void onComplete(Bundle values) {
            SessionEvents.onLoginSuccess();
        }

        public void onFacebookError(FacebookError error) {
            SessionEvents.onLoginError(error.getMessage());
        }
        
        public void onError(DialogError error) {
            SessionEvents.onLoginError(error.getMessage());
        }

        public void onCancel() {
            SessionEvents.onLoginError("Action Canceled");
        }
    }
    
    public class LogoutRequestListener extends BaseRequestListener {
        public void onComplete(String response, final Object state) {
            // callback should be run in the original thread, 
            // not the background thread
            handler.post(new Runnable() {
                public void run() {
                    SessionEvents.onLogoutFinish();
                }
            });
        }
    }
    
    private class SessionListener implements AuthListener, LogoutListener {
        
        public void onAuthSucceed() {
            SessionStore.save(facebook, activity);
        }

        public void onAuthFail(String error) {
			Log.d(FacebookConnector.class.getName(), "onAuthFail: " + error);
        }
        
        public void onLogoutBegin() {           
        }
        
        public void onLogoutFinish() {
            SessionStore.clear(activity);
        }
    }

	public Facebook getFacebook() {
		return this.facebook;
	}
	
}