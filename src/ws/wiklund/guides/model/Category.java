package ws.wiklund.guides.model;

@TableName(name = "category")
public class Category extends BaseModel implements Comparable<Category> {
	private static final long serialVersionUID = 1163582987915361095L;

	public static final int EMPTY_ID = -4;
	public static final int NEW_ID = -45;

	public Category(String name) {
		super(name);
	}

	public Category(int id, String name) {
		super(id, name);
	}
	
	public boolean isCreateNew() {
		return getId() == NEW_ID;
	}

	public boolean isValid() {
		return !isCreateNew() && getId() != EMPTY_ID;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public int compareTo(Category another) {
		if(!isValid() || !another.isValid()) {
			if(getId() == EMPTY_ID) {
				return -1;
			} else if(another.getId() == EMPTY_ID) {
				return 1;
			}
			
			if(getId() == NEW_ID) {
				return -1;
			} else if(another.getId() == NEW_ID) {
				return 1;
			}
		}

		return getName().compareTo(another.getName());
	}

}
