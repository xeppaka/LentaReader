package com.xeppaka.lentareader.ui.fragments;

import android.support.v4.app.ListFragment;

import com.xeppaka.lentareader.data.dao.Dao;
import com.xeppaka.lentareader.ui.widgets.fullnews.ElementOptions;

/**
 * Created by nnm on 3/19/14.
 */
public class FullFragmentBase extends ListFragment {
    private long dbId;
    private String link;
    private ElementOptions options;

    public FullFragmentBase(long dbId) {
        this.dbId = dbId;
    }

    public FullFragmentBase() {
        dbId = Dao.NO_ID;
    }

    public long getDbId() {
        return dbId;
    }

    public void setDbId(long dbId) {
        this.dbId = dbId;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public ElementOptions getOptions() {
        return options;
    }

    public void setOptions(ElementOptions options) {
        this.options = options;
    }
}
