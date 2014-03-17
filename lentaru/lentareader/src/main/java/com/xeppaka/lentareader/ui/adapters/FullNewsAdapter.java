package com.xeppaka.lentareader.ui.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.xeppaka.lentareader.ui.widgets.fullnews.FullNewsListElement;

import java.util.List;

/**
 * Created by nnm on 3/16/14.
 */
public class FullNewsAdapter extends BaseAdapter {
    private List<FullNewsListElement> elementList;

    public FullNewsAdapter(List<FullNewsListElement> elementList) {
        this.elementList = elementList;
    }

    @Override
    public int getCount() {
        return elementList.size();
    }

    @Override
    public FullNewsListElement getItem(int i) {
        return elementList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final FullNewsListElement element = getItem(i);
        final View result = element.getView();
        element.becomeVisible();

        return result;
    }
}
