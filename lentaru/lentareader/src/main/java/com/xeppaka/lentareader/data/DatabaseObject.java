package com.xeppaka.lentareader.data;

import java.io.Serializable;

/**
 * This interface should be implemented by every object that can be saved into
 * the database.
 * <p>
 * Methods {@link hashCode} and {@link equals} must be implemented for every
 * database objects.
 * 
 * @author nnm
 * 
 */
public interface DatabaseObject extends Serializable {
	long ID_NONE = -1;
	
	void setId(long id);
	long getId();
	long getParentId();
	
	String getKeyValue();
	
	public int hashCode();
	public boolean equals(Object other);
}
