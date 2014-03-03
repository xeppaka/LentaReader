package com.xeppaka.lentareader.data.dao.daoobjects.imagedaoobjects;

import java.net.MalformedURLException;

/**
 * Created by nnm on 3/3/14.
 */
public interface ImageKeyCreator {
    String getImageKey(String imageUrl) throws MalformedURLException;
}
