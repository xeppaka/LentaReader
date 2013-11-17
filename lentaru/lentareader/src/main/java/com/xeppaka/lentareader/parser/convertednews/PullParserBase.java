package com.xeppaka.lentareader.parser.convertednews;

import com.xeppaka.lentareader.data.NewsObject;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by kacpa01 on 11/6/13.
 */
public abstract class PullParserBase {
    protected void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }

        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    protected String readValue(XmlPullParser parser, String tagName, String ns) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, tagName);
        String value = "";

        final int next = parser.next();
        if (next == XmlPullParser.TEXT || next == XmlPullParser.CDSECT) {
            value = parser.getText();
            parser.nextTag();
        }

        parser.require(XmlPullParser.END_TAG, ns, tagName);
        return value;
    }

}
