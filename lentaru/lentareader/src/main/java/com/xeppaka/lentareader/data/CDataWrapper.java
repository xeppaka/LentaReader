package com.xeppaka.lentareader.data;

/**
 * Created by kacpa01 on 11/4/13.
 */
public class CDataWrapper {
    public static String wrapWithCData(String body) {
        return "<![CDATA[" + body + "]]>";
    }
}
