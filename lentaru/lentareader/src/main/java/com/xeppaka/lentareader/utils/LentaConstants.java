package com.xeppaka.lentareader.utils;

import java.util.Calendar;

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

    public static final int DAO_CACHE_MAX_OBJECTS = 200;
	public static final int BITMAP_CACHE_MAX_SIZE_IN_BYTES = 10 * 1024 * 1024; // 10 MB
    public static final int THUMBNAILS_BITMAP_CACHE_MAX_SIZE_IN_BYTES = 4 * 1024 * 1024; // 4 MB

	public static final boolean DEVELOPER_MODE = true;

    public static final String[] MONTHS_RUS = new String[12];
    public static final String[] MONTHS_SHORT_RUS = new String[12];
    public static final String[] DAYS_RUS = new String[8];
    public static final String[] DAYS_SHORT_RUS = new String[8];

    public static final int WITHOUT_PICTURE_LIMIT = 10;

    static {
        MONTHS_RUS[Calendar.JANUARY] = "января";
        MONTHS_RUS[Calendar.FEBRUARY] = "февраля";
        MONTHS_RUS[Calendar.MARCH] = "марта";
        MONTHS_RUS[Calendar.APRIL] = "апреля";
        MONTHS_RUS[Calendar.MAY] = "мая";
        MONTHS_RUS[Calendar.JUNE] = "июня";
        MONTHS_RUS[Calendar.JULY] = "июля";
        MONTHS_RUS[Calendar.AUGUST] = "августа";
        MONTHS_RUS[Calendar.SEPTEMBER] = "сентября";
        MONTHS_RUS[Calendar.OCTOBER] = "октября";
        MONTHS_RUS[Calendar.NOVEMBER] = "ноября";
        MONTHS_RUS[Calendar.DECEMBER] = "декабря";

        MONTHS_SHORT_RUS[Calendar.JANUARY] = "янв.";
        MONTHS_SHORT_RUS[Calendar.FEBRUARY] = "фев.";
        MONTHS_SHORT_RUS[Calendar.MARCH] = "мрт.";
        MONTHS_SHORT_RUS[Calendar.APRIL] = "апр.";
        MONTHS_SHORT_RUS[Calendar.MAY] = "мая";
        MONTHS_SHORT_RUS[Calendar.JUNE] = "июн";
        MONTHS_SHORT_RUS[Calendar.JULY] = "июл";
        MONTHS_SHORT_RUS[Calendar.AUGUST] = "авг.";
        MONTHS_SHORT_RUS[Calendar.SEPTEMBER] = "сен.";
        MONTHS_SHORT_RUS[Calendar.OCTOBER] = "окт.";
        MONTHS_SHORT_RUS[Calendar.NOVEMBER] = "нбр.";
        MONTHS_SHORT_RUS[Calendar.DECEMBER] = "дек.";

        DAYS_RUS[Calendar.MONDAY] = "Понедельник";
        DAYS_RUS[Calendar.TUESDAY] = "Вторник";
        DAYS_RUS[Calendar.WEDNESDAY] = "Среда";
        DAYS_RUS[Calendar.THURSDAY] = "Четверг";
        DAYS_RUS[Calendar.FRIDAY] = "Пятница";
        DAYS_RUS[Calendar.SATURDAY] = "Суббота";
        DAYS_RUS[Calendar.SUNDAY] = "Воскресенье";

        DAYS_SHORT_RUS[Calendar.MONDAY] = "Пн";
        DAYS_SHORT_RUS[Calendar.TUESDAY] = "Вт";
        DAYS_SHORT_RUS[Calendar.WEDNESDAY] = "Ср";
        DAYS_SHORT_RUS[Calendar.THURSDAY] = "Чт";
        DAYS_SHORT_RUS[Calendar.FRIDAY] = "Пт";
        DAYS_SHORT_RUS[Calendar.SATURDAY] = "Сб";
        DAYS_SHORT_RUS[Calendar.SUNDAY] = "Вс";
    }
}
