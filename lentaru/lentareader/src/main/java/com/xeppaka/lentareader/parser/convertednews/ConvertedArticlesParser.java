package com.xeppaka.lentareader.parser.convertednews;

import android.util.Log;
import android.util.Xml;

import com.xeppaka.lentareader.data.Article;
import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.data.body.Body;
import com.xeppaka.lentareader.utils.LentaConstants;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by nnm on 11/4/13.
 */
public class ConvertedArticlesParser extends PullParserBase implements ConvertedNewsObjectsParser<Article> {
    private static final String ns = null;

    private ConvertedBodyParser bodyParser = new ConvertedBodyParser();

    @Override
    public List<Article> parse(String xml) throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(new StringReader(xml));
        parser.nextTag();

        return readItems(parser);
    }

    private List<Article> readItems(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Article> entries = new ArrayList<Article>();

        parser.require(XmlPullParser.START_TAG, ns, "lentasnapshot");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            final String name = parser.getName();

            if (name.equals("lentaarticle")) {
                try {
                    final Article n = readArticleEntry(parser);
                    if (n != null) {
                        entries.add(n);
                    }
                } catch (XmlPullParserException e) {
                    Log.e(LentaConstants.LoggerAnyTag, "Error while parsing articles.", e);
                } catch (IOException e) {
                    Log.e(LentaConstants.LoggerAnyTag, "Error while parsing articles.", e);
                }
            } else {
                skip(parser);
            }
        }

        return entries;
    }

    private Article readArticleEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "lentaarticle");

        String guid = null;
        String title = null;
        String secondTitle = null;
        String link = null;
        String image = null;
        String imageTitle = null;
        String imageCredits = null;
        String description = null;
        String author = null;
        Date pubDate = null;
        Rubrics rubric = null;
        Body body = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            if (name.equals("guid")) {
                guid = readValue(parser, "guid", ns);
            } else if (name.equals("title")) {
                title = readValue(parser, "title", ns);
            } else if (name.equals("secondTitle")) {
                secondTitle = readValue(parser, "secondTitle", ns);
            } else if (name.equals("link")) {
                link = readValue(parser, "link", ns);
            } else if (name.equals("image")) {
                image = readValue(parser, "image", ns);
            } else if (name.equals("imageTitle")) {
                imageTitle = readValue(parser, "imageTitle", ns);
            } else if (name.equals("imageCredits")) {
                imageCredits = readValue(parser, "imageCredits", ns);
            } else if (name.equals("description")) {
                description = readValue(parser, "description", ns);
            } else if (name.equals("author")) {
                author = readValue(parser, "author", ns);
            } else if (name.equals("pubDate")) {
                pubDate = new Date(Long.parseLong(readValue(parser, "pubDate", ns)));
            } else if (name.equals("lentabody")) {
                body = bodyParser.parse(parser, ns);
            } else if (name.equals("rubric")) {
                try {
                    rubric = Rubrics.valueOf(readValue(parser, "rubric", ns));
                } catch (IllegalArgumentException e) {
                    return null;
                }
            } else {
                skip(parser);
            }
        }

        return new Article(guid, title, link, pubDate, image, imageTitle, imageCredits, rubric, description,
                secondTitle, author, false, false, false, false, body);
    }
}
