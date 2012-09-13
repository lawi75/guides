package ws.wiklund.guides.util;

public class Sortable {
	public static final int ASC = 324;
	public static final int DESC = 532;
	
	private static final String ASC_STRING = " asc";
	private static final String DESC_STRING = " desc";

	
	private String header;
	private String sub;
	private int drawable;

	private String sortColumn;
	private int currentDirection = ASC;

	public Sortable(String header, String sub, int drawable, String sortColumn) {
		this.header = header;
		this.sub = sub;
		this.drawable = drawable;
		this.sortColumn = sortColumn;
	}

	public String getHeader() {
		return header;
	}

	public String getSub() {
		return sub;
	}

	public int getDrawable() {
		return drawable;
	}

	public String getSortColumn() {
		switch(currentDirection) {
			case ASC:
				currentDirection = DESC;
				return sortColumn + ASC_STRING;
			case DESC:
				currentDirection = ASC;
				return sortColumn + DESC_STRING;
		}
		
		return sortColumn + ASC_STRING;
	}

	@Override
	public String toString() {
		return "Sortable [header=" + header + ", sub=" + sub + ", drawable="
				+ drawable + ", sortColumn=" + sortColumn + "]";
	}

}
