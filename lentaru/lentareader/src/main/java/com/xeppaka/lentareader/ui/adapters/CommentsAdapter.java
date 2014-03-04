package com.xeppaka.lentareader.ui.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.data.comments.Comment;
import com.xeppaka.lentareader.data.comments.Comments;
import com.xeppaka.lentareader.utils.LentaConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by nnm on 3/1/14.
 */
public class CommentsAdapter extends BaseAdapter {
    public static final int COMMENT_INDENT_MARGIN = 8;

    private boolean downloadImages;
    private int textSize;
    private LayoutInflater inflater;
    private String commentDeleted;
    private String parentCommentText;

    private Comments comments;
    private List<Comment> commentsList = Collections.emptyList();

    public CommentsAdapter(Context context) {
        inflater = LayoutInflater.from(context);

        final Resources resources = context.getResources();
        commentDeleted = resources.getString(R.string.comment_deleted);
        parentCommentText = resources.getString(R.string.comment_parent_text);
    }

    public boolean isDownloadImages() {
        return downloadImages;
    }

    public void setDownloadImages(boolean downloadImages) {
        this.downloadImages = downloadImages;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public void setComments(Comments comments) {
        this.comments = comments;

        if (commentsList == Collections.<Comment>emptyList()) {
            commentsList = new ArrayList<Comment>(comments.size());
        }

        comments.getExpandedOrderedComments(commentsList);
    }

    @Override
    public int getCount() {
        return commentsList.size();
    }

    private static class ViewHolder {
        private ImageView imageView;
        private LinearLayout.LayoutParams imageViewLayoutParams;
        private TextView nickView;
        private TextView parentView;
        private TextView textView;

        private ViewHolder(ImageView imageView, LinearLayout.LayoutParams imageViewLayoutParams, TextView nickView, TextView parentView, TextView textView) {
            this.imageView = imageView;
            this.imageViewLayoutParams = imageViewLayoutParams;
            this.nickView = nickView;
            this.parentView = parentView;
            this.textView = textView;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public LinearLayout.LayoutParams getImageViewLayoutParams() {
            return imageViewLayoutParams;
        }

        public TextView getNickView() {
            return nickView;
        }

        public TextView getParentView() {
            return parentView;
        }

        public TextView getTextView() {
            return textView;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout containerView;
        ImageView imageView;
        TextView nickView;
        TextView parentView;
        TextView textView;

        ViewHolder holder;

        if (convertView == null) {
            containerView = (LinearLayout) inflater.inflate(R.layout.comment_item, null);
            imageView = (ImageView) containerView.findViewById(R.id.comment_image);
            nickView = (TextView) containerView.findViewById(R.id.comment_nick);
            parentView = (TextView) containerView.findViewById(R.id.comment_parent);
            textView = (TextView) containerView.findViewById(R.id.comment_text);

            final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(imageView.getLayoutParams());

            containerView.setTag(holder = new ViewHolder(imageView, layoutParams, nickView, parentView, textView));
        } else {
            containerView = (LinearLayout) convertView;
            holder = (ViewHolder) containerView.getTag();

            imageView = holder.getImageView();
            nickView = holder.getNickView();
            parentView = holder.getParentView();
            textView = holder.getTextView();
        }

        final Comment comment = getItem(position);
        nickView.setText(comment.getNick());

        if (comment.hasParent()) {
            final String parentId = comment.getParentId();
            final Comment parentComment = comments.getComment(parentId);

            if (parentComment != null) {
                parentView.setText(String.format(parentCommentText, parentComment.getNick()));
                parentView.setVisibility(View.VISIBLE);
            }
        } else {
            parentView.setVisibility(View.GONE);
        }

        if (comment.getState() == Comment.STATE_ACCEPTED) {
            textView.setText(comment.getText());
        } else {
            textView.setText(commentDeleted);
        }

        final LinearLayout.LayoutParams imageViewLayoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        final int marginLeft = comment.getDepth() > 5 ? 5 * COMMENT_INDENT_MARGIN : comment.getDepth() * COMMENT_INDENT_MARGIN;

        imageViewLayoutParams.setMargins(marginLeft, 0, 0, 0);

        return containerView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Comment getItem(int position) {
        return commentsList.get(position);
    }

    @Override
    public void notifyDataSetChanged() {
        if (commentsList == Collections.<Comment>emptyList()) {
            commentsList = new ArrayList<Comment>(comments.size());
        }

        comments.getExpandedOrderedComments(commentsList);

        super.notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetInvalidated() {
        if (commentsList == Collections.<Comment>emptyList()) {
            commentsList = new ArrayList<Comment>(comments.size());
        }

        comments.getExpandedOrderedComments(commentsList);

        super.notifyDataSetInvalidated();
    }
}
