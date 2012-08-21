package ws.wiklund.guides.util;

import java.io.File;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import ws.wiklund.guides.R;
import ws.wiklund.guides.bolaget.SystembolagetParser;
import ws.wiklund.guides.db.BeverageDatabaseHelper;
import ws.wiklund.guides.model.Beverage;
import ws.wiklund.guides.model.BeverageType;
import ws.wiklund.guides.model.BeverageTypes;
import ws.wiklund.guides.model.Category;
import ws.wiklund.guides.model.Country;
import ws.wiklund.guides.model.Producer;
import ws.wiklund.guides.model.Provider;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class ViewHelper {
	private final static DateFormat dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM);
	private final static DecimalFormat decimalFormat = new DecimalFormat("#.#");
	private final static DecimalFormat currencyFormat = new java.text.DecimalFormat("SEK 0.00");
	private final static String urlp3Api = "?chf=bg,s,65432100&cht=p3&chs=400x150&chl=";
	private final static List<String> strengths = new ArrayList<String>();
	private final  static int lightVersion = 1;
	
	private static final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "guides" + File.separator);
	
	static {
		decimalFormat.setDecimalSeparatorAlwaysShown(true);
		decimalFormat.setParseBigDecimal(true);
	}
	
    public static boolean isLightVersion(int versionType) {
    	return versionType == lightVersion;
    }
    
    public static void setText(TextView view, String value) {
		if (value != null) {
			view.setText(value);
		}
	}
    
    public static void setThumbFromUrl(ImageView view, String thumb) {
    	if(thumb != null) {
    		new DownloadImageTask(view, SystembolagetParser.BASE_URL, 50, 100).execute(thumb);
    	}
	}
	
    public static void setCountryThumbFromUrl(ImageView view, Country country) {
		if (country != null && country.getThumbUrl() != null) {
			new DownloadImageTask(view, SystembolagetParser.BASE_URL, 29, 17).execute(country != null ? country.getThumbUrl() : null);
		}
	}
    
	public static String getDateAsString(Date date) {
		return dateFormat.format(date);
	}

	public static String getDecimalStringFromNumber(Number value) {
		String s = decimalFormat.format(value);
				
		if(s.endsWith(String.valueOf(decimalFormat.getDecimalFormatSymbols().getDecimalSeparator()))) {
			return s.substring(0, s.length() - 1);
		}
		
		return s;
	}
	
	public static Double getDoubleFromDecimalString(String value) {
		try {
			BigDecimal bd = (BigDecimal) decimalFormat.parse(value);
			return bd.doubleValue();
		} catch (ParseException e) {
			Log.d(ViewHelper.class.getName(), "Failed to parse string (" + value + ")", e);
		}
		
		return -1d;
	}

	public static synchronized List<String> getStrengths() {
		if (strengths.isEmpty()) {
			for (Double i = 10.0; i <= 25.0; i += 0.1) {
				strengths.add(decimalFormat.format(i) + " %");
			}
		}
		
		return strengths;
	}

	public static String formatPrice(double price) {
		return currencyFormat.format(price);
	}
	
	public static String buildChartUrl(BeverageDatabaseHelper helper, Iterator<BeverageType> i) {
		StringBuilder urlBuilder = new StringBuilder();
		StringBuilder values = new StringBuilder();

		urlBuilder.append(urlp3Api);
		
		while(i.hasNext()) {
			BeverageType type = i.next();
			
			int amount = helper.getAllBeveragesForType(type);
						
			if (amount > 0) {
				urlBuilder.append(URLEncoder.encode(type.toString())).append("(").append(amount).append(")");
				values.append(amount);
			}
			
			if(i.hasNext()) {
				if (amount > 0) {
					urlBuilder.append("|");
					values.append(",");
				}
			} else {
				urlBuilder.append("&chd=t:");
			}				
		}

		urlBuilder.append(values.toString());

		return urlBuilder.toString();
	}
	
	public static boolean validateBeverage(Context c, Beverage b) {
		String name = b.getName();
		if(name == null || name.length() == 0) {
			Toast.makeText(c, c.getString(R.string.missingName), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}
	
	public static File getRoot() {
		if(!root.exists()) {
			root.mkdirs();
		}		

		return root;
	}
	
	public static Beverage getBeverageFromCursor(Cursor c) {
		int i = 0;
		return new Beverage(c.getInt(i++),
				c.getString(i++),
				c.getInt(i++),
				c.getInt(i++),
				c.getString(i++),
				new Country(c.getInt(i++), c.getString(i++), c.getString(i++)),
				c.getInt(i++),
				new Producer(c.getInt(i++), c.getString(i++)),
				c.getFloat(i++),
				c.getFloat(i++),
				c.getString(i++),
				c.getString(i++),
				new Provider(c.getInt(i++), c.getString(i++)),
				c.getFloat(i++),
				c.getString(i++),
				new Category(c.getInt(i++), c.getString(i++)),
				new Date(c.getLong(i++)),
				c.getInt(i++));
	}

	public static View getView(LayoutInflater inflator, int item, View view, Cursor c, BeverageTypes types) {
		ViewHolder holder;
		
		if (view == null) {  
			view = inflator.inflate(item, null);
			
			TextView titleView = (TextView) view.findViewById(R.id.itemTitle);  
	        TextView textView = (TextView) view.findViewById(R.id.itemText);  
	        TextView typeView = (TextView) view.findViewById(R.id.itemType);  
	        ImageView imageView = (ImageView) view.findViewById(R.id.itemImage);
	        RatingBar rating = (RatingBar) view.findViewById(R.id.itemRatingBar);

	         
	        holder = new ViewHolder();  
	        holder.titleView = titleView;  
	        holder.textView = textView;  
	        holder.imageView = imageView;
	        holder.rating = rating;
	        holder.typeView = typeView;
	         
	        view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag(); 
		}
		
		if (c != null) {
			Beverage b = getBeverageFromCursor(c);
			
			int noBottles = b.getBottlesInCellar(); 
			StringBuilder name = new StringBuilder(b.getName());
			
			if(noBottles > 0) {
				name.append("(").append(noBottles).append(")");
			}
					
			holder.titleView.setText(name.toString());
			holder.typeView.setText(types.findTypeFromId(b.getBeverageTypeId()).toString());
			
			int year = b.getYear(); 
			holder.textView.setText(b.getCountry().getName() + " " + (year != -1 ? year : ""));
			holder.rating.setRating(b.getRating());

			String u = b.getThumb();			
			if (u != null) {
				String url = u.startsWith("/") ? SystembolagetParser.BASE_URL + u : u;
				holder.imageView.setTag(url);
				BitmapManager.INSTANCE.loadBitmap(url, holder.imageView, 50, 100);
			} else {
				holder.imageView.setImageResource(R.drawable.icon);
			}
		}
		
		return view;
	}
	
}
