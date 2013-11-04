package com.xeppaka.lentareader.data.body;

import com.xeppaka.lentareader.data.body.items.Item;

import java.util.Collection;

/**
 * Created by kacpa01 on 11/4/13.
 */
public class LentaBody implements Body {
    private Collection<Item> items;

    public LentaBody(Collection<Item> items) {
        this.items = items;
    }

    public static LentaBody create(String xml) {
        return null;
    }

    public Collection<Item> getItems() {
        return items;
    }

    @Override
    public String toXml() {
        return null;
    }
}
