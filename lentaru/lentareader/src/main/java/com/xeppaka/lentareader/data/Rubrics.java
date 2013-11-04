package com.xeppaka.lentareader.data;

import com.xeppaka.lentareader.utils.LentaConstants;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public enum Rubrics {
	ROOT("", ""),
	RUSSIA("/russia", "/RUSSIA"),
	WORLD("/world", "/WORLD"),
	USSR("/ussr", "/USSR"),
	ECONOMICS("/economics", "/ECONOMICS"),
	SCIENCE("/science", "/SCIENCE"),
	SPORT("/sport", "/SPORT"),
	CULTURE("/culture", "/CULTURE"),
	MEDIA("/media", "/MEDIA"),
	LIFE("/life", "/LIFE");

    private String rssSubpath;
    private String xmlSubpath;

	private String[] rssPaths = new String[NewsType.values().length];
    private String[] xmlPaths = new String[NewsType.values().length];

	private Rubrics(String rssSubpath, String xmlSubpath) {
		this.rssSubpath = rssSubpath;
        this.xmlSubpath = xmlSubpath;
		
		for (NewsType nt : NewsType.values()) {
			rssPaths[nt.ordinal()] = LentaConstants.RSS_PATH_ROOT + nt.getRssSubpath() + this.rssSubpath;
            xmlPaths[nt.ordinal()] = LentaConstants.XML_PATH_ROOT + nt.getXmlSubpath() + this.xmlSubpath;
		}
	}

    public String getRssSubpath() {
        return rssSubpath;
    }

    public String getXmlSubpath() {
        return xmlSubpath;
    }

    public String getRssPath(NewsType type) {
        return rssPaths[type.ordinal()];
    }

    public String getXmlPath(NewsType type) {
        return xmlPaths[type.ordinal()];
    }
}
