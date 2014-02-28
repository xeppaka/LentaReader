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

    @Override
    public Iterator<Comment> iterator() {
        return getOrderedComments().iterator();
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

    public List<Comment> getOrderedComments() {
        if (rootComments.isEmpty()) {
            return Collections.emptyList();
        }

        final List<Comment> orderedComments = new ArrayList<Comment>();
        addCommentsInTimeOrder(rootComments, orderedComments);

        return orderedComments;
    }

    private void addCommentsInTimeOrder(List<Comment> comments, List<Comment> orderedComments) {
        Collections.sort(comments);

        for (Comment comment : comments) {
            orderedComments.add(comment);
            addCommentsInTimeOrder(comment.getChildren(), orderedComments);
        }
    }
}
