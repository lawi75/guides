package ws.wiklund.guides.activities;

import ws.wiklund.guides.R;
import ws.wiklund.guides.bolaget.SystembolagetParser;
import ws.wiklund.guides.model.Beverage;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

public class FullSizeImageActivity extends BaseActivity {
	
	private WebView webView;
	 
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.full_size_image);
 
		webView = (WebView) findViewById(R.id.fullSizeImage);
		webView.getSettings().setLoadWithOverviewMode(true);
		webView.getSettings().setUseWideViewPort(true);

		Beverage beverage = (Beverage) getIntent().getSerializableExtra("ws.wiklund.guides.model.Beverage");
        
        String url = null;
        
        if(beverage.isCustomThumb()) {
        	url = beverage.getThumb();
        } else if(beverage.hasImage()) {
        	url = SystembolagetParser.BASE_URL + beverage.getImage();
        }
		
		if (url != null) {
			webView.loadUrl(url);
		} else {
			 Toast.makeText(this, String.format(getString(R.string.noImageAvailable), new Object[]{beverage.getName()}), Toast.LENGTH_SHORT).show();
			 finish();
		}
	}
	
}
