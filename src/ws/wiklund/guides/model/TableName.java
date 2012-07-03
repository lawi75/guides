package ws.wiklund.guides.model;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention( value=RetentionPolicy.RUNTIME )
public @interface TableName {
	String name();
}
