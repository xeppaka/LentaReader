package cz.fit.lentaruand.service.commands.exceptions;

public class ImageUpdateException extends Exception {
	private static final long serialVersionUID = -3058629692461081260L;

	public ImageUpdateException(String detailMessage) {
		super(detailMessage);
	}
}
