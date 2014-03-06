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
    private String streamId;

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

        final List<Comment> orderedComments = new ArrayList<Comment>(commentById.size());
        addCommentsInTimeOrder(rootComments, orderedComments);

        return orderedComments;
    }

    public void getExpandedOrderedComments(List<Comment> expandedComments) {
        expandedComments.clear();

        if (rootComments.isEmpty()) {
            return;
        }

        addExpandedCommentsInTimeOrder(rootComments, expandedComments);
    }

    public List<Comment> getExpandedOrderedComments() {
        if (rootComments.isEmpty()) {
            return Collections.emptyList();
        }

        final List<Comment> expandedComments = new ArrayList<Comment>(commentById.size());
        addExpandedCommentsInTimeOrder(rootComments, expandedComments);

        return expandedComments;
    }

    private void addExpandedCommentsInTimeOrder(List<Comment> comments, List<Comment> orderedComments) {
        Collections.sort(comments);

        for (Comment comment : comments) {
            orderedComments.add(comment);

            if (comment.isExpanded()) {
                addExpandedCommentsInTimeOrder(comment.getChildren(), orderedComments);
            }
        }
    }

    public void expand(String id) {
        final Comment comment = commentById.get(id);

        if (comment != null) {
            comment.setExpanded(true);
        }
    }

    public void collapse(String id) {
        final Comment comment = commentById.get(id);

        if (comment != null) {
            comment.setExpanded(false);
        }
    }

    public List<Comment> getRootComments() {
        return rootComments;
    }

    public SimpleArrayMap<String, Comment> getCommentById() {
        return commentById;
    }

    private void addCommentsInTimeOrder(List<Comment> comments, List<Comment> orderedComments) {
        Collections.sort(comments);

        for (Comment comment : comments) {
            orderedComments.add(comment);
            addCommentsInTimeOrder(comment.getChildren(), orderedComments);
        }
    }

    public int size() {
        return commentById.size();
    }

    public boolean isEmpty() {
        return commentById.isEmpty();
    }

    public void clear() {
        commentById.clear();
        rootComments.clear();
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }
}
