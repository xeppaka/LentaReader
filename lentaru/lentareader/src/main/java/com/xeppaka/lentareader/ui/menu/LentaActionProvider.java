package com.xeppaka.lentareader.ui.menu;

import android.content.Context;
import android.view.ActionProvider;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import com.xeppaka.lentareader.R;

/**
 * Created by nnm on 1/21/14.
 */
public class LentaActionProvider extends ActionProvider {
    private static View menuItemRefresh;

    public LentaActionProvider(Context context) {
        super(context);
    }

    @Override
    public View onCreateActionView(MenuItem forItem) {
        final View menuItem = super.onCreateActionView(forItem);

        if (menuItem.getId() == R.id.action_refresh) {
            menuItemRefresh = menuItem;
        }

        return menuItem;
    }

    @Override
    public View onCreateActionView() {
        return null;
    }


    public static View getMenuItemRefresh() {
        return menuItemRefresh;
    }
}
