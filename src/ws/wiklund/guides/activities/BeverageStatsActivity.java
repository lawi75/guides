package ws.wiklund.guides.activities;

import java.util.List;

import ws.wiklund.guides.R;
import ws.wiklund.guides.activities.BaseActivity;
import ws.wiklund.guides.db.BeverageDatabaseHelper;
import ws.wiklund.guides.model.Beverage;
import ws.wiklund.guides.util.DownloadImageTask;
import ws.wiklund.guides.util.ViewHelper;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public abstract class BeverageStatsActivity extends BaseActivity {
	private final static String urlGoogleChart = "http://chart.apis.google.com/chart";

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats);
        
        BeverageDatabaseHelper helper = getDatabaseHelper();
        
        List<Beverage> beverages = helper.getAllBeverages();
        
        //TODO History of beverages in cellar
        
		TextView total = (TextView) findViewById(R.id.total);
		total.setText(String.format(getString(R.string.statsNow), new Object[]{beverages.size(), ViewHelper.getDecimalStringFromNumber(helper.getAverageRating())}));
		
		createChart();		
		
		TextView totalCellar = (TextView) findViewById(R.id.totalCellar);
		
		totalCellar.setText(String.format(getString(R.string.cellarStatsNow), new Object[]{helper.getNoBottlesInCellar(), ViewHelper.formatPrice(helper.getCellarValue())}));
		
		
		TableLayout table = (TableLayout)findViewById(R.id.TableLayoutStats);
		
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		for(Beverage beverage : beverages) {
			if(beverage.hasBottlesInCellar()) {
				TableRow row = new TableRow(this);
				row.setLayoutParams(params);
				
				TextView amountView = new TextView(this);
				amountView.setPadding(0, 0, 10, 0);
				amountView.setText(String.format(getString(R.string.amount), beverage.getBottlesInCellar()));
				row.addView(amountView);

				TextView yearView = new TextView(this);
				yearView.setPadding(0, 0, 10, 0);
				
				if (beverage.getYear() != -1) {
					yearView.setText(String.valueOf(beverage.getYear()));
				}
				
				row.addView(yearView);

				TextView nameView = new TextView(this);
				nameView.setPadding(0, 0, 10, 0);
				nameView.setText(beverage.getName());
				row.addView(nameView);

				table.addView(row);
			}
		}

    }
    
	protected abstract BeverageDatabaseHelper getDatabaseHelper();
    
    private void createChart() {
		new DownloadImageTask((ImageView)findViewById(R.id.pie), urlGoogleChart, 400, 150).execute(ViewHelper.buildChartUrl(getDatabaseHelper()));
	}

}
