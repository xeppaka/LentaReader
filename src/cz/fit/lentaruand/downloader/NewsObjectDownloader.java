package cz.fit.lentaruand.downloader;

import java.io.IOException;
import java.util.Collection;

import javax.xml.xpath.XPathExpressionException;

import cz.fit.lentaruand.data.NewsObject;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.parser.exceptions.PageParseException;

public interface NewsObjectDownloader<T extends NewsObject<T>> {
	Collection<T> downloadRubricBrief(Rubrics rubric) throws XPathExpressionException, IOException;
	void downloadFull(T brief) throws IOException, PageParseException;
}
