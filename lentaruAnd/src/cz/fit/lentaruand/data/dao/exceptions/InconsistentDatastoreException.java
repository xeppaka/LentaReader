package cz.fit.lentaruand.data.dao.exceptions;

public class InconsistentDatastoreException extends RuntimeException {
	private static final long serialVersionUID = -2948499172944838623L;
	
	public InconsistentDatastoreException(String message) {
		super(message);
	}
}
