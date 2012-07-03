package ws.wiklund.guides.util.facebook;

import java.io.IOException;

import ws.wiklund.guides.util.facebook.SessionEvents.AuthListener;
import ws.wiklund.guides.util.facebook.SessionEvents.LogoutListener;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class FacebookConnector {

	private Facebook facebook = null;
	private Context context;
	private String[] permissions;
	private Handler mHandler;
	private Activity activity;
	private SessionListener mSessionListener = new SessionListener();
	
	public FacebookConnector(String appId, Activity activity, Context context, String[] permissions) {
		this.facebook = new Facebook(appId);
		
		SessionStore.restore(facebook, context);
        SessionEvents.addAuthListener(mSessionListener);
        SessionEvents.addLogoutListener(mSessionListener);
        
		this.context=context;
		this.permissions=permissions;
		this.mHandler = new Handler();
		this.activity=activity;
	}
	
	public void login() {
        if (!facebook.isSessionValid()) {
            facebook.authorize(activity, this.permissions,new LoginDialogListener());
        }
    }
	
	public void logout() {
        SessionEvents.onLogoutBegin();
        AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(this.facebook);
        asyncRunner.logout(this.context, new LogoutRequestListener());
	}
	
	public void postMessageOnWall(Bundle bundle) {
		if (facebook.isSessionValid()) {
		    try {
				String response = facebook.request("me/feed", bundle,"POST");
				Log.d(FacebookConnector.class.getName(), "Facebook response: " + response);
			} catch (IOException e) {
				Log.w(FacebookConnector.class.getName(), "Failed to post message on Facebook wall", e);
			}
		} else {
			login();
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
            mHandler.post(new Runnable() {
                public void run() {
                    SessionEvents.onLogoutFinish();
                }
            });
        }
    }
    
    private class SessionListener implements AuthListener, LogoutListener {
        
        public void onAuthSucceed() {
            SessionStore.save(facebook, context);
        }

        public void onAuthFail(String error) {
        }
        
        public void onLogoutBegin() {           
        }
        
        public void onLogoutFinish() {
            SessionStore.clear(context);
        }
    }

	public Facebook getFacebook() {
		return this.facebook;
	}
}