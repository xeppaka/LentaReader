package com.xeppaka.lentareader.data.body;

/**
 * Created by kacpa01 on 11/4/13.
 */
public class LentaOrigBody implements Body {
    private String xmlBody;

    public LentaOrigBody(String xmlBody) {
        this.xmlBody = xmlBody;
    }

    @Override
    public String toXml() {
        return xmlBody;
    }
}
