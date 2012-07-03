package ws.wiklund.guides.model;

import java.util.List;

public interface BeverageTypes {

	List<BeverageType> getAllBeverageTypes();
	BeverageType findTypeFromString(String text);
	BeverageType findTypeFromId(int id);
	boolean useSubTypes();

}
