package ws.wiklund.guides.activities;

import ws.wiklund.guides.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class FullAdActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.fullad);
		
	}
	
    public void skip(View view) {    	
    	finish();
    }
	
}
