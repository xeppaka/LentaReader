package com.xeppaka.lentareader.parser.convertednews;

import android.util.Xml;

import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.downloader.Page;
import com.xeppaka.lentareader.parser.originalnews.NewsParser;
import com.xeppaka.lentareader.parser.exceptions.ParseWithRegexException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;

/**
 * Created by nnm on 11/4/13.
 */
public class ConvertedNewsParser implements ConvertedNewsObjectsParser<News> {
    @Override
    public Collection<News> parse(Page page) throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(new StringReader(page.getText()));

        
    }
}
