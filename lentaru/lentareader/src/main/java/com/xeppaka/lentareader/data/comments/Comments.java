package com.xeppaka.lentareader.data.comments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by nnm on 2/26/14.
 */
public class Comments implements Iterable<Comment> {
    private List<Comment> rootComments = Collections.emptyList();
    private Map<String, Comment> commentById = Collections.emptyMap();

    private static class CommentsIterator implements Iterator<Comment> {


        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Comment next() {
            return null;
        }

        @Override
        public void remove() {

        }
    }

    @Override
    public Iterator<Comment> iterator() {
        return null;
    }

    public void addComment(Comment comment) {
        if (rootComments == Collections.<Comment>emptyList()) {
            rootComments = new ArrayList<Comment>();
        }

        if (commentById == Collections.<String, Comment>emptyMap()) {
            commentById = new HashMap<String, Comment>();
        }

        commentById.put(comment.getId(), comment);

        if (comment.isRoot()) {
            rootComments.add(comment);
        } else {
            commentById.put(comment.getId(), comment);

            final Comment parent = commentById.get(comment.getParentId());

            if (parent != null) {
                parent.addChild(comment);
                comment.setDepth(parent.getDepth() + 1);
            }
        }
    }

    public Comment getComment(String id) {
        return commentById.get(id);
    }

    public List<Comment> getRootComments() {
        return rootComments;
    }


}
