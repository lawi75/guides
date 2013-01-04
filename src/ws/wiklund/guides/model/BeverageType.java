package ws.wiklund.guides.model;

@TableName(name = "beverage_type")
public class BeverageType extends BaseModel {
	private static final long serialVersionUID = 4316915341600275071L;

	public static final BeverageType OTHER = new BeverageType(999, "…vriga");
	
	public BeverageType(int id, String name) {
		super(id, name);
	}

	public boolean isOther() {
		return getId() == OTHER.getId();
	}

	@Override
	public String toString() {
		return getName();
	}

}
