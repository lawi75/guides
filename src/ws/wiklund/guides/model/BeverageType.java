package ws.wiklund.guides.model;

public class BeverageType {
	public static final BeverageType OTHER = new BeverageType(999, "Övriga");
	
	private int id = -1;
	private String name = null;
	
	public BeverageType(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}
