package com.xeppaka.lentareader.data.comments;

import android.support.v4.util.SimpleArrayMap;
import android.util.ArrayMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

/**
 * Created by nnm on 2/26/14.
 */
public class Comments implements Iterable<Comment> {
    private final List<Comment> rootComments = new ArrayList<Comment>();
    private final SimpleArrayMap<String, Comment> commentById = new SimpleArrayMap<String, Comment>();

    private static class CommentsIterator implements Iterator<Comment> {
        private List<Comment> orderedComments = new ArrayList<Comment>();
        private Iterator<Comment> iterator;

        private CommentsIterator(List<Comment> rootComments) {
            addCommentsInTimeOrder(rootComments);

            iterator = orderedComments.iterator();
        }

        private void addCommentsInTimeOrder(List<Comment> comments) {
            Collections.sort(comments);

            for (Comment comment : comments) {
                orderedComments.add(comment);
                addCommentsInTimeOrder(comment.getChildren());
            }
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Comment next() {
            return iterator.next();
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }

    @Override
    public Iterator<Comment> iterator() {
        return new CommentsIterator(rootComments);
    }

    public void addComment(Comment comment) {
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
}
