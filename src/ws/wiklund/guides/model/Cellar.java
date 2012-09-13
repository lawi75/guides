package ws.wiklund.guides.model;

import java.util.Date;

import android.content.ContentValues;

@TableName(name = "cellar")
public class Cellar extends BaseModel {
	private static final long serialVersionUID = -2168368653140070121L;

	private int beverageId;
	private int noBottles;
	private String comment;
	private String storageLocation;
	private Date addedToCellar;
	private Date consumptionDate;
	private int notificationId;
	
	public Cellar(int beverageId) {
		super(null);
		
		this.beverageId = beverageId;
	}

	public Cellar(int id, int beverageId, int noBottles, String comment,
			String storageLocation, Date addedToCellar, Date consumptionDate,
			int notificationId) {
		super(id, null);
		this.beverageId = beverageId;
		this.noBottles = noBottles;
		this.comment = comment;
		this.storageLocation = storageLocation;
		this.addedToCellar = addedToCellar;
		this.consumptionDate = consumptionDate;
		this.notificationId = notificationId;
	}

	public void setConsumptionDate(Date consumptionDate) {
		this.consumptionDate = consumptionDate;
	}
	
	public Date getConsumptionDate() {
		return consumptionDate;
	}

	public void setNoBottles(int noBottles) {
		this.noBottles = noBottles;
	}
	
	public int getNoBottles() {
		return noBottles;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getComment() {
		return comment;
	}

	public void setStorageLocation(String storageLocation) {
		this.storageLocation = storageLocation;
	}
	
	public String getStorageLocation() {
		return storageLocation;
	}

	public void setAddedToCellar(Date addedToCellar) {
		this.addedToCellar = addedToCellar;
	}

	public Date getAddedToCellar() {
		return addedToCellar;
	}

	public int getNotificationId() {
		return notificationId;
	}

	public int getBeverageId() {
		return beverageId;
	}
	
	@Override
	public boolean isNew() {
		return getId() == -1;
	}
	
	public ContentValues getAsContentValues() {
		ContentValues values = new ContentValues();

		values.put("beverage_id", beverageId);  
		values.put("no_bottles", noBottles);  
		values.put("storage_location", storageLocation);  
		values.put("comment", comment);
		
		if (addedToCellar != null) {
			values.put("added_to_cellar", addedToCellar.getTime());
		}
		
		if (consumptionDate != null) {
			values.put("consumption_date", consumptionDate.getTime());
		} else {
			values.putNull("consumption_date");
		}

		values.put("notification_id", notificationId);  

		return values;
	}
	
	
	
	
}
