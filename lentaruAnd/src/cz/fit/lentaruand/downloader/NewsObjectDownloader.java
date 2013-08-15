package cz.fit.lentaruand.downloader;

import java.io.IOException;
import java.util.Collection;

import cz.fit.lentaruand.data.NewsObject;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.downloader.exceptions.HttpStatusCodeException;
import cz.fit.lentaruand.parser.exceptions.ParseWithRegexException;
import cz.fit.lentaruand.parser.exceptions.ParseWithXPathException;

public interface NewsObjectDownloader<T extends NewsObject> {
	Collection<T> downloadRubricBrief(Rubrics rubric) throws ParseWithXPathException, HttpStatusCodeException, IOException;
	void downloadFull(T brief) throws HttpStatusCodeException, IOException, ParseWithRegexException;
}
