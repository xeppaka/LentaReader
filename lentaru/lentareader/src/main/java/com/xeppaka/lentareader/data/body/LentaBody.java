package com.xeppaka.lentareader.data.body;

import com.xeppaka.lentareader.data.body.items.Item;

import java.util.Collection;
import java.util.List;

/**
 * Created by kacpa01 on 11/4/13.
 */
public class LentaBody implements Body {
    private List<Item> items;

    public LentaBody(List<Item> items) {
        this.items = items;
    }

    @Override
    public List<Item> getItems() {
        return items;
    }

    @Override
    public String toXml() {
        StringBuilder sb = new StringBuilder("<lentabody>");

        for (Item item : getItems()) {
            sb.append(item.toXml());
        }

        return sb.append("</lentabody>").toString();
    }
}
