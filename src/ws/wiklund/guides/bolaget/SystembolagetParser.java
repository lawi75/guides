package ws.wiklund.guides.bolaget;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ws.wiklund.guides.db.BeverageDatabaseHelper;
import ws.wiklund.guides.model.Beverage;
import ws.wiklund.guides.model.BeverageType;
import ws.wiklund.guides.model.Country;
import ws.wiklund.guides.model.Producer;
import ws.wiklund.guides.model.Provider;
import ws.wiklund.guides.util.SearchResult;
import ws.wiklund.guides.util.ViewHelper;
import android.util.Log;

public class SystembolagetParser {
	public static final String BASE_URL = "http://www.systembolaget.se";

	private static final String YEAR = "Årgång";
	private static final String STRENGTH = "Alkoholhalt";
	private static final String USAGE = "Användning";
	private static final String TASTE = "Smak";
	private static final String PRODUCER = "Producent";
	private static final String PROVIDER = "Leverantör";
	private static final String SWEETNESS = "Sockerhalt";
	

	public static Beverage parseResponse(String no, BeverageDatabaseHelper helper, boolean useSubTypes) throws IOException {
		Document doc = Jsoup.connect(BASE_URL + "/" + no).timeout(10*1000).get();
		
		if(isValidResponse(doc)) {
			Element productName = doc.select("span.produktnamnfet").first();
			
			Element type = doc.select("span.character > strong").first();
			Element typeIncludingSubType = doc.select("span.character").first();
			Element country = doc.select("div.country > img").first();
			Element thumb = doc.select("div.image > img").first();
			Element fullSize = doc.select("a.enlarge").first();
			
			Elements e = doc.select("td:contains(Pris)");
			Element price = null;
			if(e != null && !e.isEmpty()) {
				price = e.first().nextElementSibling();
			}
			
			Beverage beverage = new Beverage(productName != null ? productName.text() : null);
			beverage.setNo(Integer.valueOf(no));
			
			if(useSubTypes) {
				beverage.setBeverageType(helper.getBeverageTypeFromName(typeIncludingSubType.text().split(",")[1]));
			} else {
				beverage.setBeverageType(helper.getBeverageTypeFromName(type.text()));	
			}

			beverage.setCountry(new Country(country.attr("alt"),country.attr("src")));
			beverage.setThumb(thumb.attr("src"));
			
			if(beverage.isFullSizeImageAvailable()) {
				beverage.setImage(fullSize.attr("href"));
			}
			
			if (price != null) {
				updatePrice(beverage, price);
			}
			
			updateBeverageFacts(beverage, doc.select("ul.beverageFacts"));
			
			return beverage;
		}
		
		return null;
	}
	
	public static SearchResult searchBeverages(String searchWord, BeverageDatabaseHelper helper) throws IOException {
		Document doc = Jsoup.connect(BASE_URL + "/Sok-dryck/?searchquery=" + searchWord + "&sortfield=Default&sortdirection=Ascending&hitsoffset=0&page=1&searchview=All&groupfiltersheader=Default&filters=searchquery%2c").timeout(10*1000).get();

		Element noHits = doc.select("h2.filtersHeader").first();
		
		int hits = 0;
		if (noHits != null) {
			hits = ViewHelper.getNoHitsFromString(noHits.text());
		}
		
		Elements result = doc.select("tbody > tr");
		
		if (result != null) {
			return new SearchResult(hits, getBeveragesFromSearchResult(result.iterator(), helper));
		}
		
		return new SearchResult();
	}

	private static List<Beverage> getBeveragesFromSearchResult(Iterator<Element> result, BeverageDatabaseHelper helper) {
		List<Beverage> beverages = new ArrayList<Beverage>();
		
		while(result.hasNext()) {
			Element e = result.next();
			
			String type = e.select("td.col1").first().text();
			BeverageType beverageType = helper.getBeverageTypeFromName(type);
			
			if(!beverageType.isOther()) {
				Beverage b = new Beverage();
				b.setBeverageType(beverageType);
				
				Element col0 = e.select("td.col0").first();

				String no = col0.select("a").first().attr("varunr");
				String name = col0.select("a > strong").first().text();
				
				String country = e.select("td.col3").first().text();
				String price = e.select("td.col5").first().text();
				
				b.setNo(Integer.valueOf(no));
				b.setName(name);
				
				Country c = new Country(country, null);
				b.setCountry(c);
				
				b.setPrice(Double.valueOf(ViewHelper.getDoubleFromDecimalString(price)));
				
				beverages.add(b);
			}
		}
		
		return beverages;
	}

	private static void updatePrice(Beverage beverage, Element price) {
		String p = null;

		try {
			if (price != null) {
				p = price.text();
				int idx = p.indexOf(" ");
				if (idx != -1) {
					p = p.substring(0, idx);					
					beverage.setPrice(ViewHelper.getDoubleFromDecimalString(p));
				} else {
					Log.d(SystembolagetParser.class.getName(), "Invalid price tag[" + price.text() + "]");			
				}
			}
		} catch (NumberFormatException e) {
			Log.d(SystembolagetParser.class.getName(), "Failed to parse price data [" + p + "]");			
		}
	}

	private static void updateBeverageFacts(Beverage beverage, Elements beverageFacts) {
		for(Element e : beverageFacts) {
			Elements facts = e.select("li");
			for(Element fact : facts) {
				String key = fact.select("span").text();
				String data = fact.select("strong").text().replaceAll("[\\s\\u00A0]+$", "");
				
				updateBeverageFact(beverage, key, data);
			}
		}
	}

	private static void updateBeverageFact(Beverage beverage, String key, String data) {
		if(key.equals(YEAR)) {
			beverage.setYear(Integer.valueOf(data));
		} else if(key.equals(STRENGTH)) {
			beverage.setStrength(getStrengthFromString(data));
		} else if(key.equals(USAGE)) {				
			beverage.setUsage(data);
		} else if(key.equals(TASTE)) {				
			beverage.setTaste(data);
		} else if(key.equals(PRODUCER)) {				
			beverage.setProducer(new Producer(data));
		} else if(key.equals(PROVIDER)) {				
			beverage.setProvider(new Provider(data));			
		} else if(key.equals(SWEETNESS)) {				
			//Not used
		} else {
			Log.d(SystembolagetParser.class.getName(), "Unused Beverage Fact [" +  key + ":" + data + "]");
		}
	}

	private static double getStrengthFromString(String data) {
		String percentData = data.substring(0, data.lastIndexOf("%") - 1).replace(',', '.');
		
		return Double.valueOf(percentData);
	}

	private static boolean isValidResponse(Document doc) {
		return doc.select("div.top_exception_message").first() == null;
	}	

}
