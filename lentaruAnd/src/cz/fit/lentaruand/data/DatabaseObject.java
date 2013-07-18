package cz.fit.lentaruand.data;

import java.io.Serializable;

public interface DatabaseObject extends Serializable {
	long ID_NONE = -1;
	
	void setId(long id);
	long getId();
	
	String getKeyValue();
}
