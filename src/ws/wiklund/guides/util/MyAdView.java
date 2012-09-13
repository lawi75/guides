package ws.wiklund.guides.util;

import ws.wiklund.guides.R;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class MyAdView extends AdView {
	public MyAdView(Activity activity, AdSize adSize, String adUnitId) {
	    super(activity, adSize, adUnitId);
	    
	    loadAdIfNeeded();
	}

	public MyAdView(Context context, AttributeSet attrs) {
	    super(context, attrs); 

	    loadAdIfNeeded();
	}

	MyAdView(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);

	    loadAdIfNeeded();
	}


	private void loadAdIfNeeded() {
	    if (Boolean.valueOf(getContext().getString(R.string.ad_is_enabled))) {
	        AdRequest request = new AdRequest();
	    	request.addTestDevice(AdRequest.TEST_EMULATOR);
	    	loadAd(request);
	    }
	}
}
