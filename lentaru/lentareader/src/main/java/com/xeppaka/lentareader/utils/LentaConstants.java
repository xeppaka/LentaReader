package com.xeppaka.lentareader.utils;

public class LentaConstants {
	public static final int SDK_VER = android.os.Build.VERSION.SDK_INT;
	public static final int AppVersion = 1;
	public static final String LoggerMainAppTag = "LentaAnd";
	public static final String LoggerServiceTag = "LentaAndService";
	public static final String LoggerProviderTag = "LentaAndProvider";
	public static final String LoggerAnyTag = "LentaAndAny";
	public static final String LENTA_URL_ROOT = "http://lenta.ru";
    public static final String OWNSERVER_URL_ROOT = "http://82.196.0.57";
	public static final String UserAgent = "Android LentaReader";

    public static final String RSS_PATH_ROOT = "/rss";
    public static final String XML_PATH_ROOT = "";

    public static final int DAO_CACHE_MAX_OBJECTS = 100;
	public static final int BITMAP_CACHE_MAX_SIZE_IN_BYTES = 4 * 1024 * 1024; // 4 MB
	public static final int BITMAP_THUMBNAIL_CACHE_MAX_SIZE_IN_BYTES = 4 * 1024 * 1024; // 4 MB
	
	public static final boolean DEVELOPER_MODE = true;
}
