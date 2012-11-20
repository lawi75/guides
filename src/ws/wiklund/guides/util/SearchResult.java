package ws.wiklund.guides.util;

import java.io.Serializable;
import java.util.List;

import ws.wiklund.guides.model.Beverage;

public class SearchResult implements Serializable {
	private static final long serialVersionUID = -4037052731287903532L;

	private int totalNoHits;
	private List<Beverage> result;

	public SearchResult(int totalNoHits, List<Beverage> result) {
		this.totalNoHits = totalNoHits;
		this.result = result;
	}

	public SearchResult() {
	}

	public int getTotalNoHits() {
		return totalNoHits;
	}

	public List<Beverage> getResult() {
		return result;
	}

	public boolean isEmpty() {
		return result == null || result.isEmpty();
	}

	public Beverage get(int i) {
		return result.get(i);
	}

}
