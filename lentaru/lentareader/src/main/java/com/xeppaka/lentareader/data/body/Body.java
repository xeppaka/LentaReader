package com.xeppaka.lentareader.data.body;

import com.xeppaka.lentareader.data.body.items.Item;

import java.util.List;

/**
 * Created by kacpa01 on 11/4/13.
 */
public interface Body {
    String toXml();
    List<Item> getItems();
}
