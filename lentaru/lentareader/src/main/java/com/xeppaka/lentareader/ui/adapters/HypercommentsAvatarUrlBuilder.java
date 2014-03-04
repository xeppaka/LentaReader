package com.xeppaka.lentareader.ui.adapters;

/**
 * Created by kacpa01 on 3/4/14.
 */
public class HypercommentsAvatarUrlBuilder {
    private static final String HYPERCOMMENTS_URL = "http://static.hypercomments.com/data/avatars/%1s/avatar";

    public String build(String accountId) {
        return String.format(HYPERCOMMENTS_URL, accountId);
    }
}
