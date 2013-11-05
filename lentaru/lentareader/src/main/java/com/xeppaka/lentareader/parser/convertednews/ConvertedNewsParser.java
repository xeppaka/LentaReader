package com.xeppaka.lentareader.parser.convertednews;

import android.util.Xml;

import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.data.body.LentaBody;
import com.xeppaka.lentareader.downloader.Page;
import com.xeppaka.lentareader.parser.originalnews.NewsParser;
import com.xeppaka.lentareader.parser.exceptions.ParseWithRegexException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by nnm on 11/4/13.
 */
public class ConvertedNewsParser implements ConvertedNewsObjectsParser<News> {
    private static final String ns = null;

    @Override
    public Collection<News> parse(Page page) throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(new StringReader(page.getText()));
        parser.nextTag();

        return readItems(parser);
    }

    private Collection<News> readItems(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<News> entries = new ArrayList<News>();

        parser.require(XmlPullParser.START_TAG, ns, "lentasnapshot");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            final String name = parser.getName();

            if (name.equals("lentanews")) {
                final News n = readNewsEntry(parser);
                if (n != null) {
                    entries.add(n);
                }
            } else {
                skip(parser);
            }
        }

        return entries;
    }

    private News readNewsEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "lentanews");

        String guid = null;
        String title = null;
        String link = null;
        String image = null;
        String imageTitle = null;
        String imageCredits = null;
        String description = null;
        Date pubDate = null;
        Rubrics rubric = null;
        LentaBody body = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            if (name.equals("guid")) {
                guid = readValue(parser, "guid");
            } else if (name.equals("title")) {
                title = readValue(parser, "title");
            } else if (name.equals("image")) {
                image = readValue(parser, "image");
            } else if (name.equals("imageTitle")) {
                imageTitle = readValue(parser, "imageTitle");
            } else if (name.equals("imageCredits")) {
                imageCredits = readValue(parser, "imageCredits");
            } else if (name.equals("description")) {
                description = readValue(parser, "description");
            } else if (name.equals("pubDate")) {
                pubDate = new Date(Long.parseLong(readValue(parser, "pubDate")));
            } else if (name.equals("lentabody")) {
                body = readBody();
            } else if (name.equals("rubric")) {
                try {
                    rubric = Rubrics.valueOf(readValue(parser, "rubric"));
                } catch (IllegalArgumentException e) {
                    return null;
                }
            } else {
                skip(parser);
            }
        }

        return new News(guid, title, link, pubDate, image, imageTitle, imageCredits, rubric, description, body);
    }

    private LentaBody readBody() throws XmlPullParserException, IOException {
        return null;
    }

    private String readValue(XmlPullParser parser, String tagName) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, tagName);
        String value = "";
        if (parser.next() == XmlPullParser.TEXT) {
            value = parser.getText();
            parser.nextTag();
        }

        parser.require(XmlPullParser.END_TAG, ns, tagName);
        return value;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
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
}
