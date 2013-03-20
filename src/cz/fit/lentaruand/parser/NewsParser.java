package cz.fit.lentaruand.parser;

import cz.fit.lentaruand.parser.exceptions.MobileNewsParseException;
import cz.fit.lentaruand.site.Page;

public interface NewsParser<T> {
	public T parse(Page page) throws MobileNewsParseException;
}
