package cz.fit.lentaruand.parser;

import cz.fit.lentaruand.Page;
import cz.fit.lentaruand.parser.exceptions.NewsDateNotFoundException;
import cz.fit.lentaruand.parser.exceptions.NewsDateParseException;
import cz.fit.lentaruand.parser.exceptions.NewsImageCreditsNotFoundException;
import cz.fit.lentaruand.parser.exceptions.NewsImageNotFoundException;
import cz.fit.lentaruand.parser.exceptions.NewsImageTitleNotFoundException;
import cz.fit.lentaruand.parser.exceptions.NewsTextNotFoundException;
import cz.fit.lentaruand.parser.exceptions.NewsTitleNotFoundException;

public interface NewsParser<T> {
	public T parse(Page page) throws NewsDateNotFoundException,
			NewsDateParseException, NewsTitleNotFoundException,
			NewsImageNotFoundException, NewsImageTitleNotFoundException,
			NewsImageCreditsNotFoundException, NewsTextNotFoundException;
}
