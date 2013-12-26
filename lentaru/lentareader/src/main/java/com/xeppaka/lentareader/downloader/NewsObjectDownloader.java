package com.xeppaka.lentareader.downloader;

import com.xeppaka.lentareader.data.NewsObject;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.downloader.exceptions.HttpStatusCodeException;
import com.xeppaka.lentareader.parser.exceptions.ParseWithRegexException;
import com.xeppaka.lentareader.parser.exceptions.ParseWithXPathException;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Collection;

public interface NewsObjectDownloader<T extends NewsObject> {
    Collection<T> download(Rubrics rubric) throws HttpStatusCodeException, IOException, XmlPullParserException;
    Collection<T> download(Rubrics rubric, long fromDate) throws HttpStatusCodeException, IOException, XmlPullParserException;
	Collection<T> downloadRubricBrief(Rubrics rubric) throws ParseWithXPathException, HttpStatusCodeException, IOException;
	void downloadFull(T brief) throws HttpStatusCodeException, IOException, ParseWithRegexException;
}
