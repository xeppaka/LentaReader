package cz.fit.lentaruand.parser;

import cz.fit.lentaruand.parser.exceptions.PageParseException;
import cz.fit.lentaruand.site.Page;

public interface NewsParser<T> {
	public T parse(Page page) throws PageParseException;
}
