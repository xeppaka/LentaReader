package cz.fit.lentaruand.data.dao;

import java.io.Serializable;

public interface DaoObject extends Serializable {
	long ID_NONE = -1;
	
	void setId(long id);
	long getId();
	
	String getKeyValue();
}
