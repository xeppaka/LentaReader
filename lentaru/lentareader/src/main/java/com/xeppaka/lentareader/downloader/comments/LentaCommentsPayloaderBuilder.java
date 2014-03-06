package com.xeppaka.lentareader.downloader.comments;

import com.xeppaka.lentareader.utils.LentaConstants;

/**
 * Created by nnm on 2/27/14.
 */
public class LentaCommentsPayloaderBuilder {
    private static final String KEY_STREAM = "stream";
    private static final String VAL_STREAM_START = "\"streamstart\"";

    private static final String KEY_WIDGET_ID = "widget_id";

    private static final String KEY_XID = "xid";
    private static final String KEY_LIMIT = "limit";
    private static final String KEY_FILTER = "filter";
    private static final String KEY_REVERSE = "reverse";
    private static final String KEY_HYPERTEXT = "hypertext";

    private static final String VAL_FALSE = "false";
    private static final String VAL_TRUE = "true";

    private static final String VAL_LIMIT_DEFAULT = "200";
    private static final String VAL_FILTER_DEFAULT = "\"all\"";
    private static final String VAL_REVERSE_DEFAULT = VAL_FALSE;
    private static final String VAL_HYPERTEXT_DEFAULT = VAL_FALSE;

    private String xid;
    private String limit = VAL_LIMIT_DEFAULT;
    private String filter = VAL_FILTER_DEFAULT;
    private String reverse = VAL_REVERSE_DEFAULT;
    private String hypertext = VAL_HYPERTEXT_DEFAULT;

    public LentaCommentsPayloaderBuilder() {}

    public LentaCommentsPayloaderBuilder setLimit(int limit) {
        this.limit = String.valueOf(limit);
        return this;
    }

    public LentaCommentsPayloaderBuilder setXid(String xid) {
        this.xid = "\"" + xid + "\"";
        return this;
    }

    public LentaCommentsPayloaderBuilder setFilter(String filter) {
        this.filter = "\"" + filter + "\"";
        return this;
    }

    public LentaCommentsPayloaderBuilder setReverse(boolean val) {
        this.reverse = val ? VAL_TRUE : VAL_FALSE;
        return this;
    }

    public LentaCommentsPayloaderBuilder setHypertext(boolean val) {
        this.hypertext = val ? VAL_TRUE : VAL_FALSE;
        return this;
    }

    private void addKeyValuePair(String key, String value, StringBuilder sb, boolean comma) {
        sb.append("\"").append(key).append("\":").append(value);

        if (comma) {
            sb.append(",");
        }
    }

    public String build() {
        if (xid == null || xid.isEmpty()) {
            throw new IllegalArgumentException("xid is null or empty.");
        }

        final StringBuilder sb = new StringBuilder("{");
        addKeyValuePair(KEY_STREAM, VAL_STREAM_START, sb, true);
        addKeyValuePair(KEY_WIDGET_ID, LentaConstants.COMMENTS_WIDGET_ID, sb, true);
        addKeyValuePair(KEY_XID, xid, sb, true);
        addKeyValuePair(KEY_LIMIT, limit, sb, true);
        addKeyValuePair(KEY_FILTER, filter, sb, true);
        addKeyValuePair(KEY_REVERSE, reverse, sb, true);
        addKeyValuePair(KEY_HYPERTEXT, hypertext, sb, false);
        sb.append("}");

        return sb.toString();
    }
}
