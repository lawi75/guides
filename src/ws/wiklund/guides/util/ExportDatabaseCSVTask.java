package ws.wiklund.guides.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import ws.wiklund.guides.R;
import ws.wiklund.guides.db.BeverageDatabaseHelper;
import ws.wiklund.guides.model.Beverage;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import au.com.bytecode.opencsv.CSVWriter;

@SuppressLint("HandlerLeak")
public class ExportDatabaseCSVTask extends AsyncTask<Void, Void, Boolean> {
	private Context context;
	private BeverageDatabaseHelper helper;
	private File exportFile;

    private ProgressDialog dialog;
	private int numberOfRecords;
	private int currentRecords;

	public ExportDatabaseCSVTask(Context context, BeverageDatabaseHelper helper, File exportFile, int numberOfRecords) {
		this.context = context;
		this.helper = helper;
		this.exportFile = exportFile;
		this.numberOfRecords = numberOfRecords;
		
		dialog = new ProgressDialog(context);
		dialog.setTitle(context.getString(R.string.wait));
		dialog.setMax(numberOfRecords);
	}
	
    @Override
    protected void onPreExecute() {
    	dialog.setMessage(String.format(context.getString(R.string.exporting_db), new Object[]{currentRecords++, numberOfRecords}));
    	// set the progress to be horizontal
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // reset the bar to the default value of 0
        dialog.setProgress(0);
        dialog.show();
    }   
	
	@Override
	protected void onProgressUpdate(Void... values) {
    	dialog.setMessage(String.format(context.getString(R.string.exporting_db), new Object[]{currentRecords++, numberOfRecords}));
		super.onProgressUpdate(values);
	}

	@Override
	protected Boolean doInBackground(Void... args) {
        try {
        	exportFile.createNewFile();                
            CSVWriter csvWrite = new CSVWriter(new OutputStreamWriter(new FileOutputStream(exportFile), "ISO-8859-1"));
            
            csvWrite.writeNext(new String[]{
            	"Namn", 
            	"Nummer", 
            	"Typ", 
            	"Årgång", 
            	"Styrka", 
            	"Land", 
            	"Pris", 
            	"Antal i källaren", 
            	"Betyg", 
            	"Kommentar", 
            	"Användning", 
            	"Smak", 
            	"Producent", 
            	"Leverantör"
            });
            
            List<Beverage> beverages = helper.getAllBeverages();
            for(Beverage beverage : beverages) {
				publishProgress();
            	
                String arrStr[] = {
                		beverage.getName(),
                		String.valueOf(beverage.getNo()),
                		beverage.getBeverageType().getName(),
                		String.valueOf(beverage.getYear()),
                		String.valueOf(beverage.getStrength()),
                		beverage.getCountry().getName(),
                		String.valueOf(beverage.getPrice()),
                		String.valueOf(beverage.getBottlesInCellar()),
                		(beverage.getRating() >= 0 ? String.valueOf(beverage.getRating()) : "0"),
                		beverage.getComment(),
                		beverage.getUsage(),
                		beverage.getTaste(),
                		beverage.getProducer().getName(),
                		beverage.getProvider().getName()
               };

                csvWrite.writeNext(arrStr);
                
                // active the update handler
                progressHandler.sendMessage(progressHandler.obtainMessage());
            }
            
            csvWrite.close();
            
            return true;
        } catch(SQLException sqlEx) {
            Log.e(ExportDatabaseCSVTask.class.getName(), sqlEx.getMessage(), sqlEx);
        } catch (IOException e) {
            Log.e(ExportDatabaseCSVTask.class.getName(), e.getMessage(), e);
        }

        return false;
	}
	
	  @Override
	protected void onPostExecute(Boolean result) {
		dialog.cancel();
		super.onPostExecute(result);
	}

	// handler for the background updating
	Handler progressHandler = new Handler() {
        public void handleMessage(Message msg) {
            dialog.incrementProgressBy(1);
        }
    };	
}
