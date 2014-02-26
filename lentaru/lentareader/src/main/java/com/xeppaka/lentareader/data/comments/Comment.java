package com.xeppaka.lentareader.data.comments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Created by nnm on 2/26/14.
 */
public class Comment implements Comparable<Comment> {
    private String id;
    private String rootId;
    private String parentId;
    private String nick;
    private long time;
    private String accountId;
    private int voteUp;
    private int voteDown;
    private int state;
    private String text;
    private int depth;

    private List<Comment> children = Collections.emptyList();

    public Comment(String id, String rootId, String parentId, String nick, long time, String accountId, int voteUp, int voteDown, int state, String text) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("id is null or empty.");
        }

        this.id = id;
        this.rootId = rootId;
        this.parentId = parentId;
        this.nick = nick;
        this.time = time;
        this.accountId = accountId;
        this.voteUp = voteUp;
        this.voteDown = voteDown;
        this.state = state;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public String getRootId() {
        return rootId;
    }

    public String getParentId() {
        return parentId;
    }

    public String getNick() {
        return nick;
    }

    public long getTime() {
        return time;
    }

    public String getAccountId() {
        return accountId;
    }

    public int getVoteUp() {
        return voteUp;
    }

    public int getVoteDown() {
        return voteDown;
    }

    public int getState() {
        return state;
    }

    public String getText() {
        return text;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public List<Comment> getChildren() {
        return children;
    }

    public int childrenSize() {
        return children.size();
    }

    public void addChild(Comment comment) {
        if (children == Collections.<Comment>emptyList()) {
            children = new ArrayList<Comment>();
        }

        children.add(comment);
    }

    public boolean isRoot() {
        return rootId == null && parentId == null;
    }

    @Override
    public int compareTo(Comment another) {
        if (time > another.time)
            return 1;
        else if (time < another.time)
            return -1;
        else
            return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Comment comment = (Comment) o;

        if (!id.equals(comment.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
