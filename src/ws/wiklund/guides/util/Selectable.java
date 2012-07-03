package ws.wiklund.guides.util;

public interface Selectable {
	public static final int DELETE_ACTION = 3234;
	public static final int ADD_ACTION = 5435;
	public static final int REMOVE_ACTION = 1554;

	int getAction();
	String getHeader();
	int getDrawable();
	
}
