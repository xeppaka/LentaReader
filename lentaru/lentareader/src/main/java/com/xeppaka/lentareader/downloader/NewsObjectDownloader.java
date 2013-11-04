package com.xeppaka.lentareader.downloader;

import java.io.IOException;
import java.util.Collection;

import com.xeppaka.lentareader.data.NewsObject;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.downloader.exceptions.HttpStatusCodeException;
import com.xeppaka.lentareader.parser.exceptions.ParseWithRegexException;
import com.xeppaka.lentareader.parser.exceptions.ParseWithXPathException;

public interface NewsObjectDownloader<T extends NewsObject> {
    Collection<T> download(Rubrics rubric) throws HttpStatusCodeException, IOException, ParseWithXPathException;
	Collection<T> downloadRubricBrief(Rubrics rubric) throws ParseWithXPathException, HttpStatusCodeException, IOException;
	void downloadFull(T brief) throws HttpStatusCodeException, IOException, ParseWithRegexException;
}
