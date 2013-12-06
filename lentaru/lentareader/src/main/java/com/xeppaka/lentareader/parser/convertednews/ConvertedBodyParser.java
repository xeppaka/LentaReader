package com.xeppaka.lentareader.parser.convertednews;

import android.text.TextUtils;
import android.util.Xml;

import com.xeppaka.lentareader.data.body.Body;
import com.xeppaka.lentareader.data.body.EmptyBody;
import com.xeppaka.lentareader.data.body.LentaBody;
import com.xeppaka.lentareader.data.body.VideoType;
import com.xeppaka.lentareader.data.body.items.Item;
import com.xeppaka.lentareader.data.body.items.LentaBodyItemImage;
import com.xeppaka.lentareader.data.body.items.LentaBodyItemImageGallery;
import com.xeppaka.lentareader.data.body.items.LentaBodyItemVideo;
import com.xeppaka.lentareader.data.body.items.LentaBodyTextItem;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by nnm on 11/4/13.
 */
public class ConvertedBodyParser extends PullParserBase implements BodyParser {
    @Override
    public Body parse(XmlPullParser parser, String ns) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "lentabody");

        List<Item> items = new ArrayList<Item>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            final String name = parser.getName();

            if (name.equals("text")) {
                items.add(new LentaBodyTextItem(readValue(parser, "text", ns)));
            } else if (name.equals("images")) {
                items.add(parseGallery(parser, ns));
            } else if (name.equals("video")) {
                items.add(parseVideo(parser, ns));
            } else {
                skip(parser);
            }
        }

        parser.require(XmlPullParser.END_TAG, ns, "lentabody");
        return new LentaBody(items);
    }

    private LentaBodyItemVideo parseVideo(XmlPullParser parser, String ns) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "video");
        LentaBodyItemVideo result = new LentaBodyItemVideo(parser.getAttributeValue(ns, "url"), VideoType.valueOf(parser.getAttributeValue(ns, "type")));

        parser.nextTag();

        parser.require(XmlPullParser.END_TAG, ns, "video");
        return result;
    }

    private LentaBodyItemImageGallery parseGallery(XmlPullParser parser, String ns) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "images");

        List<LentaBodyItemImage> images = new ArrayList<LentaBodyItemImage>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            final String name = parser.getName();

            if (name.equals("image")) {
                String preview_url = parser.getAttributeValue(ns, "preview_url");
                String original_url = parser.getAttributeValue(ns, "original_url");
                String caption = parser.getAttributeValue(ns, "caption");
                String credits = parser.getAttributeValue(ns, "credits");

                images.add(new LentaBodyItemImage(preview_url, original_url, caption, credits));
                parser.nextTag();
            } else {
                skip(parser);
            }
        }

        parser.require(XmlPullParser.END_TAG, ns, "images");
        return new LentaBodyItemImageGallery(images);
    }

    @Override
    public Body parse(String xml) throws XmlPullParserException, IOException {
        if (xml == null || TextUtils.isEmpty(xml)) {
            return EmptyBody.getInstance();
        }

        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(new StringReader(xml));
        parser.nextTag();

        return parse(parser, null);
    }
}
