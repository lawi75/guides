package ws.wiklund.guides.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import ws.wiklund.guides.R;
import ws.wiklund.guides.db.BeverageDatabaseHelper;
import ws.wiklund.guides.model.Beverage;
import ws.wiklund.guides.model.Country;
import ws.wiklund.guides.util.ViewHelper;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

public abstract class BaseActivity extends Activity {
	protected static final int REQUEST_FROM_CAMERA = 432;
	
	protected Bitmap bitmap;
	protected Country country;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    }
    
    @Override
	protected void onStart() {
    	super.onStart();
		ViewHelper.handleAds(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_FROM_CAMERA && resultCode == RESULT_OK) {
			InputStream is=null;

			File file=getTempFile();
			
			try {
				is=new FileInputStream(file);
				bitmap = BitmapFactory.decodeStream(is);
			} catch (FileNotFoundException e) {
				Log.e(BaseActivity.class.getName(), "Failed to create stream from file", e);
			}

			try {
				//On HTC Hero the requested file will not be created. Because HTC Hero has custom camera
				//app implementation and it works another way. It doesn't write to a file but instead
				//it writes to media gallery and returns uri in intent. More info can be found here:
				//http://stackoverflow.com/questions/1910608/android-actionimagecapture-intent
				//http://code.google.com/p/android/issues/detail?id=1480
				//So here's the workaround:
				if(is==null){
			        Uri uri = data.getData();
			        is=getContentResolver().openInputStream(uri);
					bitmap = BitmapFactory.decodeStream(is);
			        
			        //Store it to sd
			        try {
			             FileOutputStream out = new FileOutputStream(file);
			             bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
			             out.flush();
			             out.close();
			        } catch (Exception e) {
						Log.e(BaseActivity.class.getName(), "Failed to store tmp file on sd", e);
			        }			        
				}
			} catch (FileNotFoundException e) {
				Log.e(BaseActivity.class.getName(), "Failed to create stream from file", e);
			}
		}		
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	protected void addAdaptertoCountryView(AutoCompleteTextView countryView, BeverageDatabaseHelper helper) {
		List<Country> countries = helper.getCountries();
		
		ArrayAdapter<Country> adapter = new ArrayAdapter<Country>(this, android.R.layout.simple_dropdown_item_1line, countries);
		countryView.setAdapter(adapter);
		
		countryView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
		    	country = (Country)parent.getItemAtPosition(position);
		    }
		});		
	}
		
	protected void saveBeverage(BeverageDatabaseHelper helper, Beverage b) {
		if(country != null && country.getName().equals(b.getCountry().getName())) {
			//Updated with an existing country
			b.setCountry(country);
		}
		
		b = helper.addBeverage(b);
    	
		if(bitmap != null) {
			File file=getTempFile();
			File newFile = new File(ViewHelper.getRoot(), String.valueOf(b.getId()) + ".jpg");
			if(file.renameTo(newFile)) {
				b.setThumb(Uri.fromFile(newFile).toString());
				helper.updateBeverage(b);
			} else {
				Toast.makeText(getApplicationContext(), getString(R.string.failedToStoreImage), Toast.LENGTH_SHORT).show();
			}

			file.delete();
		}
	}
	
	
	protected File getTempFile() {
		return new File(ViewHelper.getRoot(), "beverage.tmp");
	}	

}
