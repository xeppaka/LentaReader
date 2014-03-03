package com.xeppaka.lentareader.data.dao.daoobjects.imagedaoobjects;

/**
 * Created by nnm on 3/3/14.
 */
public class AvatarImageKeyCreator implements ImageKeyCreator {
    private static AvatarImageKeyCreator INSTANCE;

    public static AvatarImageKeyCreator getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AvatarImageKeyCreator();
        }

        return INSTANCE;
    }

    @Override
    public String getImageKey(String imageUrl) {
        return null;
    }
}
