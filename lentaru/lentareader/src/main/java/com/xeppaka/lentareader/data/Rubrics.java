package com.xeppaka.lentareader.data;

import com.xeppaka.lentareader.utils.LentaConstants;

public enum Rubrics {
	LATEST("", "/LATEST", "Последние"),
	RUSSIA("/russia", "/RUSSIA", "Россия"),
	WORLD("/world", "/WORLD", "Мир"),
	USSR("/ussr", "/USSR", "Бывший СССР"),
	ECONOMICS("/economics", "/ECONOMICS", "Экономика"),
	SCIENCE("/science", "/SCIENCE", "Наука и техника"),
	SPORT("/sport", "/SPORT", "Спорт"),
	CULTURE("/culture", "/CULTURE", "Культура"),
	MEDIA("/media", "/MEDIA", "Интернет и СМИ"),
	LIFE("/life", "/LIFE", "Из жизни");

    private String rssSubpath;
    private String xmlSubpath;
    private String label;

	private String[] rssPaths = new String[NewsType.values().length];
    private String[] xmlPaths = new String[NewsType.values().length];

	private Rubrics(String rssSubpath, String xmlSubpath, String label) {
		this.rssSubpath = rssSubpath;
        this.xmlSubpath = xmlSubpath;
        this.label = label;
		
		for (NewsType nt : NewsType.values()) {
			rssPaths[nt.ordinal()] = LentaConstants.RSS_PATH_ROOT + nt.getRssSubpath() + this.rssSubpath + '/';
            xmlPaths[nt.ordinal()] = LentaConstants.XML_PATH_ROOT + nt.getXmlSubpath() + this.xmlSubpath + '/';
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

    public String getLabel() {
        return label;
    }
}
