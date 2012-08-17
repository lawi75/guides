package ws.wiklund.guides.model;

import java.util.Date;

@TableName(name = "beverage")
public class Beverage extends BaseModel {
	private static final long serialVersionUID = 8130320547884807505L;
	
	private int no = -1;
	private int beverageTypeId;
	private String thumb;
	private Country country;
	private int year = -1;
	private Producer producer;
	private double strength = -1;
	private double price = -1;
	private String usage;
	private String taste;
	private Provider provider;
	private float rating = -1;
	private Date added;
	private String comment;
	private Category category;
	private int bottlesInCellar;

	public Beverage() {
		this(null);
	}
	
	public Beverage(String name) {
		super(name);
	}

	public Beverage(int id, String name, int no, int beverageTypeId, String thumb,
			Country country, int year, Producer producer, double strength, double price,
			String usage, String taste, Provider provider, float rating, String comment, 
			Category category, Date added, int bottlesInCellar) {
		super(id, name);
		
		this.no = no;
		this.beverageTypeId = beverageTypeId;
		this.thumb = thumb;
		this.country = country;
		this.year = year;
		this.producer = producer;
		this.strength = strength;
		this.price = price;
		this.usage = usage;
		this.taste = taste;
		this.provider = provider;
		this.rating = rating;
		this.comment = comment;
		this.category = category;
		this.added = added;
		this.bottlesInCellar = bottlesInCellar;
	}

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}
	
	public int getBeverageTypeId() {
		return beverageTypeId;
	}

	public void setBeverageTypeId(int beverageTypeId) {
		this.beverageTypeId = beverageTypeId;
	}

	public String getThumb() {
		return thumb;
	}

	public void setThumb(String thumb) {
		this.thumb = thumb;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public Producer getProducer() {
		return producer;
	}

	public void setProducer(Producer producer) {
		this.producer = producer;
	}

	public double getStrength() {
		return strength;
	}

	public void setStrength(double strength) {
		this.strength = strength;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public String getTaste() {
		return taste;
	}

	public void setTaste(String taste) {
		this.taste = taste;
	}

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	public float getRating() {
		return rating;
	}

	public void setRating(float rating) {
		this.rating = rating;
	}

	public String getComment() {
		return comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getAdded() {
		return added;
	}

	public void setAdded(Date added) {
		this.added = added;
	}

	public Category getCategory() {
		return category;
	}
	
	public void setCategory(Category category) {
		this.category = category;
	}
	
	public boolean hasPrice() {
		return price > 0;
	}
	
	public int getBottlesInCellar() {
		return bottlesInCellar;
	}

	public void setBottlesInCellar(int bottlesInCellar) {
		this.bottlesInCellar = bottlesInCellar;
	}

	public boolean hasBottlesInCellar() {
		return bottlesInCellar > 0;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Beverage other = (Beverage) obj;
		if (added == null) {
			if (other.added != null)
				return false;
		} else if (!added.equals(other.added))
			return false;
		if (beverageTypeId != other.beverageTypeId)
			return false;
		if (bottlesInCellar != other.bottlesInCellar)
			return false;
		if (category == null) {
			if (other.category != null)
				return false;
		} else if (!category.equals(other.category))
			return false;
		if (comment == null) {
			if (other.comment != null)
				return false;
		} else if (!comment.equals(other.comment))
			return false;
		if (country == null) {
			if (other.country != null)
				return false;
		} else if (!country.equals(other.country))
			return false;
		if (no != other.no)
			return false;
		if (Double.doubleToLongBits(price) != Double
				.doubleToLongBits(other.price))
			return false;
		if (producer == null) {
			if (other.producer != null)
				return false;
		} else if (!producer.equals(other.producer))
			return false;
		if (provider == null) {
			if (other.provider != null)
				return false;
		} else if (!provider.equals(other.provider))
			return false;
		if (Float.floatToIntBits(rating) != Float.floatToIntBits(other.rating))
			return false;
		if (Double.doubleToLongBits(strength) != Double
				.doubleToLongBits(other.strength))
			return false;
		if (taste == null) {
			if (other.taste != null)
				return false;
		} else if (!taste.equals(other.taste))
			return false;
		if (thumb == null) {
			if (other.thumb != null)
				return false;
		} else if (!thumb.equals(other.thumb))
			return false;
		if (usage == null) {
			if (other.usage != null)
				return false;
		} else if (!usage.equals(other.usage))
			return false;
		if (year != other.year)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Beverage [no=" + no + ", beverageTypeId=" + beverageTypeId + ", thumb=" + thumb
				+ ", country=" + country + ", year=" + year + ", producer="
				+ producer + ", strength=" + strength + ", price=" + price
				+ ", usage=" + usage + ", taste=" + taste + ", provider="
				+ provider + ", rating=" + rating + ", added=" + added
				+ ", comment=" + comment + ", category=" + category
				+ ", bottlesInCellar=" + bottlesInCellar + "]";
	}

}
