package com.xeppaka.lentareader.parser.convertednews;

import com.xeppaka.lentareader.data.NewsObject;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by nnm on 11/4/13.
 */
public interface ConvertedNewsObjectsParser<T extends NewsObject> {
    Collection<T> parse(String xml) throws XmlPullParserException, IOException;
}
