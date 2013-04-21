package cz.fit.lentaruand.data.dao;

public interface DaoObject {
	long ID_NONE = -1;
	
	void setId(long id);
	long getId();
	
	String getKeyValue();
}
