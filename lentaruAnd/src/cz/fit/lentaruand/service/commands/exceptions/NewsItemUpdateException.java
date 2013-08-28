package cz.fit.lentaruand.service.commands.exceptions;

public class NewsItemUpdateException extends Exception {
	private static final long serialVersionUID = -1794006347209895206L;

	public NewsItemUpdateException(String detailMessage) {
		super(detailMessage);
	}
}
