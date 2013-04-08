package cz.fit.lentaruand.parser;

import cz.fit.lentaruand.downloader.Page;
import cz.fit.lentaruand.parser.exceptions.PageParseException;

public interface NewsParser<T> {
	public T parse(Page page) throws PageParseException;
}
