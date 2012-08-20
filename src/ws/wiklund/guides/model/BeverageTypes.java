package ws.wiklund.guides.model;

import java.io.Serializable;
import java.util.List;

public interface BeverageTypes extends Serializable {

	List<BeverageType> getAllBeverageTypes();
	BeverageType findTypeFromString(String text);
	BeverageType findTypeFromId(int id);
	boolean useSubTypes();

}
