package com.xeppaka.lentareader.parser.convertednews;

import com.xeppaka.lentareader.data.body.Body;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by nnm on 11/4/13.
 */
public interface BodyParser {
    Body parse(XmlPullParser parser, String ns) throws XmlPullParserException, IOException;
    Body parse(String xml) throws XmlPullParserException, IOException;
}
