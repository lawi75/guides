package ws.wiklund.guides.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ws.wiklund.guides.R;
import ws.wiklund.guides.db.BeverageDatabaseHelper;
import ws.wiklund.guides.model.Beverage;
import ws.wiklund.guides.model.Category;
import ws.wiklund.guides.model.Country;
import ws.wiklund.guides.util.PayPalFactory;
import ws.wiklund.guides.util.ViewHelper;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import android.widget.TableRow.LayoutParams;

import com.paypal.android.MEP.CheckoutButton;
import com.paypal.android.MEP.PayPal;

public class BaseActivity extends Activity {
	protected static final int REQUEST_FROM_CAMERA = 432;
	
	private Calendar calendar = Calendar.getInstance();
	private List<Integer> years = new ArrayList<Integer>();
	
	private static Set<Category> categories = new HashSet<Category>();

	protected ViewHelper viewHelper;
	protected Bitmap bitmap;
	protected Country country;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
		viewHelper = new ViewHelper();
		
		/*if (!isLightVersion()) {
			View ad = findViewById(R.id.adView);
			if(ad != null) {
				ad.setVisibility(View.GONE);
			}
			
			View ad1 = findViewById(R.id.adView1);
			if(ad1 != null) {
				ad1.setVisibility(View.GONE);
			}
		}*/	
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
	
    protected boolean isLightVersion() {
    	return viewHelper.isLightVersion(Integer.valueOf(getString(R.string.version_type)));
    }
    
	protected synchronized Set<Category> getCategories() {
		if(categories.isEmpty()) {
			categories.add(new Category(""));
			//categories.add(new Category(Category.NEW_ID, getString(R.string.newStr)));
		}
		
		/*List<Category> c = helper.getCategories();
		
		if (c != null && !c.isEmpty()) {
			categories.addAll(c);
		}*/
		
		//TODO possibility to remove categories
		//TODO dialog if new is selected
		
		return categories;
	}
	
	protected CheckoutButton getCheckoutButton() {
		return getCheckoutButton(PayPal.BUTTON_152x33);
	}
	
	protected CheckoutButton getCheckoutButton(int btnSize) {
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.span = 2;
		params.topMargin = 10;
		
		PayPal payPal = PayPalFactory.getPayPal();
		
		CheckoutButton btn = null;
		if (payPal != null) {
			btn = payPal.getCheckoutButton(this, btnSize,
					CheckoutButton.TEXT_DONATE);
			btn.setLayoutParams(params);
			btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					donate();
				}
			});
		}
		
		return btn;
	}
	
	public int getCurrentYear() {
		return calendar.get(Calendar.YEAR);
	}
	
	public synchronized List<Integer> getYears() {
		if(years.isEmpty()) {
			for(int i = 1900; i<= calendar.get(Calendar.YEAR); i++) {
				years.add(i);
			}	
		}
		
		return years;
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

	private void donate() {
		/*
		NumberPickerDialog pickerDialog = new NumberPickerDialog(this, -1, 20, R.string.dialog_set_number, true);
		pickerDialog.setTitle(getString(R.string.donateTitle));
		pickerDialog.setOnNumberSetListener(new OnNumberSetListener() {
			@Override
			public void onNumberSet(int selectedNumber) {
				PayPalPayment payment = new PayPalPayment();

				payment.setSubtotal(new BigDecimal(selectedNumber));

				payment.setCurrencyType("SEK");

				payment.setRecipient("vinguiden@wiklund.ws");

				payment.setPaymentType(PayPal.PAYMENT_TYPE_PERSONAL);

				Intent checkoutIntent = PayPal.getInstance().checkout(payment, BaseActivity.this);

				startActivityForResult(checkoutIntent, 1);					
			}
		});
		
		pickerDialog.show();
		*/			
    }
	
}
