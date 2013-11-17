package com.xeppaka.lentareader.data.body;

import com.xeppaka.lentareader.data.body.items.Item;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by nnm on 11/9/13.
 */
public class EmptyBody implements Body {
    private static final Body INSTANCE = new EmptyBody();

    public static Body getInstance() {
        return INSTANCE;
    }

    @Override
    public String toXml() {
        return "";
    }

    @Override
    public List<Item> getItems() {
        return Collections.emptyList();
    }
}
