package com.xeppaka.lentareader.ui.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.xeppaka.lentareader.ui.widgets.fullnews.FullNewsElement;

import java.util.List;

/**
 * Created by kacpa01 on 3/17/14.
 */
public abstract class FullNewsAdapterBase extends BaseAdapter {
    private List<FullNewsElement> elementList;

    protected FullNewsAdapterBase(List<FullNewsElement> elementList) {
        this.elementList = elementList;
    }

    public List<FullNewsElement> getElementList() {
        return elementList;
    }

    @Override
    public int getCount() {
        return elementList.size();
    }

    @Override
    public FullNewsElement getItem(int i) {
        return elementList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final FullNewsElement element = getItem(i);
        final View result = element.getView();
        element.becomeVisible();

        return result;
    }

    public void becomeVisible() {
        for (FullNewsElement element : elementList) {
            if (element != null) {
                element.becomeVisible();
            }
        }
    }

    public void becomeInvisible() {
        for (FullNewsElement element : elementList) {
            if (element != null) {
                element.becomeInvisible();
            }
        }
    }
}
