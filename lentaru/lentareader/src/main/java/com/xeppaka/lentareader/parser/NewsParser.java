package com.xeppaka.lentareader.parser;

import com.xeppaka.lentareader.downloader.Page;
import com.xeppaka.lentareader.parser.exceptions.ParseWithRegexException;

/**
 * NewsParser is the interface for all parsers that are able to parse mobile
 * version of Lenta.ru page.
 * 
 * @author kacpa01
 * 
 * @param <T>
 *            is the data object which holds parsed information from mobile
 *            page.
 */
public interface NewsParser<T> {
	/**
	 * Parses mobile version of the page.
	 * 
	 * @param page
	 *            is downloaded page (basically String object inside Page object
	 *            instance).
	 * @return Data object filled with parsed information.
	 * @throws ParseWithRegexException
	 *             if parser is unable to get some mandatory information from
	 *             the page. For example from the news page it unable to get main
	 *             news text which is mandatory for news.
	 */
	public T parse(Page page) throws ParseWithRegexException;
}
