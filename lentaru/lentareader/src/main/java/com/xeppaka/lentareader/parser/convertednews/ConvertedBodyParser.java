package com.xeppaka.lentareader.parser.convertednews;

import android.text.TextUtils;
import android.util.Xml;

import com.xeppaka.lentareader.data.body.Body;
import com.xeppaka.lentareader.data.body.EmptyBody;
import com.xeppaka.lentareader.data.body.LentaBody;
import com.xeppaka.lentareader.data.body.items.Item;
import com.xeppaka.lentareader.data.body.items.LentaBodyTextItem;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
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
            } else {
                skip(parser);
            }
        }

        return new LentaBody(items);
    }

    private String readTextItem(XmlPullParser parser, String ns) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "text");

        String result = "";
        final int token = parser.nextToken();
        if (token == XmlPullParser.CDSECT || token == XmlPullParser.TEXT) {
            result = parser.getText();
        }

        parser.next();
        return result;
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
