package com.xeppaka.lentareader.ui.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.async.AsyncListener;
import com.xeppaka.lentareader.data.comments.Comment;
import com.xeppaka.lentareader.data.comments.Comments;
import com.xeppaka.lentareader.data.dao.daoobjects.BitmapReference;
import com.xeppaka.lentareader.data.dao.daoobjects.imagedaoobjects.ImageDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by nnm on 3/1/14.
 */
public class CommentsAdapter extends BaseAdapter {
    public static final int COMMENT_INDENT_MARGIN = 9;

    private boolean downloadImages = true;
    private int textSize;
    private final String commentDeleted;
    private final String parentCommentText;
    private final LayoutInflater inflater;
    private final ImageDao imageDao;
    private final int ratingColorPositive;
    private final int ratingColorNegative;

    private final HypercommentsAvatarUrlBuilder hypercommentsAvatarUrlBuilder;

    private Comments comments;
    private List<Comment> commentsList = Collections.emptyList();

    public CommentsAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        imageDao = ImageDao.getInstance(context);
        hypercommentsAvatarUrlBuilder = new HypercommentsAvatarUrlBuilder();

        final Resources resources = context.getResources();
        commentDeleted = resources.getString(R.string.comment_deleted);
        parentCommentText = resources.getString(R.string.comment_parent_text);
        ratingColorPositive = resources.getColor(R.color.comment_rating_positive);
        ratingColorNegative = resources.getColor(R.color.comment_rating_negative);
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
        private ImageView expandView;
        private TextView nickView;
        private TextView parentView;
        private TextView textView;
        private TextView answersCount;
        private TextView ratingCount;
        private LinearLayout containerImage;
        private String accountId;
        private AsyncTask asyncTask;

        private ViewHolder(ImageView imageView, ImageView expandView, TextView nickView, TextView parentView, TextView textView, TextView answersCount, TextView ratingCount, LinearLayout containerImage, String accountId) {
            this.imageView = imageView;
            this.expandView = expandView;
            this.nickView = nickView;
            this.parentView = parentView;
            this.textView = textView;
            this.answersCount = answersCount;
            this.ratingCount = ratingCount;
            this.containerImage = containerImage;
            this.accountId = accountId;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public ImageView getExpandView() {
            return expandView;
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

        public TextView getAnswersCount() {
            return answersCount;
        }

        public TextView getRatingCount() {
            return ratingCount;
        }

        public LinearLayout getContainerImage() {
            return containerImage;
        }

        public String getAccountId() {
            return accountId;
        }

        public void setAccountId(String accountId) {
            this.accountId = accountId;
        }

        public void setAsyncTask(AsyncTask asyncTask) {
            this.asyncTask = asyncTask;
        }

        public void cancelAsyncTask() {
            if (asyncTask != null) {
                asyncTask.cancel(true);
                asyncTask = null;
            }
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LinearLayout containerView;
        LinearLayout containerImage;
        ImageView imageView;
        ImageView expandView;
        TextView nickView;
        TextView parentView;
        TextView textView;
        TextView answersCount;
        TextView ratingCount;

        final Comment comment = getItem(position);
        ViewHolder holder;

        if (convertView == null) {
            containerView = (LinearLayout) inflater.inflate(R.layout.comment_item, null);
            imageView = (ImageView) containerView.findViewById(R.id.comment_image);
            expandView = (ImageView) containerView.findViewById(R.id.comment_image_expand);
            nickView = (TextView) containerView.findViewById(R.id.comment_nick);
            parentView = (TextView) containerView.findViewById(R.id.comment_parent);
            textView = (TextView) containerView.findViewById(R.id.comment_text);
            answersCount = (TextView) containerView.findViewById(R.id.comments_answers_count);
            ratingCount = (TextView) containerView.findViewById(R.id.comment_rating_count);
            containerImage = (LinearLayout) containerView.findViewById(R.id.comment_image_container);

            containerView.setTag(holder = new ViewHolder(imageView, expandView, nickView, parentView, textView, answersCount, ratingCount, containerImage, comment.getAccountId()));
        } else {
            containerView = (LinearLayout) convertView;
            holder = (ViewHolder) containerView.getTag();

            imageView = holder.getImageView();
            expandView = holder.getExpandView();
            nickView = holder.getNickView();
            parentView = holder.getParentView();
            textView = holder.getTextView();
            answersCount = holder.getAnswersCount();
            containerImage = holder.getContainerImage();
            ratingCount = holder.getRatingCount();
        }

        holder.cancelAsyncTask();
        holder.setAccountId(comment.getAccountId());

        if (comment.isJustExpanded()) {
            containerView.setBackgroundResource(R.drawable.comment_just_expanded_item);
        } else {
            containerView.setBackgroundColor(0x00000000);
        }
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

        final int childrenSize = comment.childrenSize();
        answersCount.setText(String.valueOf(childrenSize));

        if (comment.getVoteUp() == 0 && comment.getVoteDown() == 0) {
            ratingCount.setVisibility(View.GONE);
        } else {
            final int rating = comment.getVoteUp() - comment.getVoteDown();
            ratingCount.setText(String.valueOf(rating));

            if (rating < 0) {
                ratingCount.setTextColor(ratingColorNegative);
            } else {
                ratingCount.setTextColor(ratingColorPositive);
            }

            ratingCount.setVisibility(View.VISIBLE);
        }

        if (childrenSize <= 0) {
            expandView.setVisibility(View.GONE);
        } else {
            expandView.setVisibility(View.VISIBLE);

            if (comment.isExpanded()) {
                expandView.setImageResource(R.drawable.ic_navigation_collapse);
            } else {
                expandView.setImageResource(R.drawable.ic_navigation_expand);
            }
        }

        imageView.setImageDrawable(ImageDao.getLoadingThumbnailImage().getDrawableIfCached());

        if (isDownloadImages()) {
            final BitmapReference bitmapRef = imageDao.read(hypercommentsAvatarUrlBuilder.build(comment.getAccountId()), comment.getAccountId());
            imageView.setImageDrawable(ImageDao.getLoadingThumbnailImage().getDrawableIfCached());

            final ViewHolder holderForAsync = holder;
            final String accountId = comment.getAccountId();

            final AsyncTask asyncTask = bitmapRef.getDrawableAsync(imageView, new AsyncListener<Drawable>() {
                @Override
                public void onSuccess(Drawable drawable) {
                    if (!accountId.equals(holderForAsync.getAccountId())) {
                        return;
                    }

                    final ImageView iv = holderForAsync.getImageView();
                    iv.setImageDrawable(drawable);
                }

                @Override
                public void onFailure(Exception e) {
                    if (!accountId.equals(holderForAsync.getAccountId())) {
                        return;
                    }

                    holderForAsync.getImageView().setImageDrawable(ImageDao.getNotAvailableThumbnailImage().getDrawableIfCached());
                }
            });

            holder.setAsyncTask(asyncTask);
        } else {
            final BitmapReference bitmapRef = imageDao.read(hypercommentsAvatarUrlBuilder.build(comment.getAccountId()), comment.getAccountId());
            final Drawable drawable;

            if ((drawable = bitmapRef.getDrawableIfCached(imageView)) != null) {
                imageView.setImageDrawable(drawable);
            } else {
                imageView.setImageDrawable(ImageDao.getLoadingThumbnailImage().getDrawableIfCached());
            }
            holder.setAsyncTask(null);
        }

        final LinearLayout.LayoutParams containerImageLayoutParams = (LinearLayout.LayoutParams) containerImage.getLayoutParams();
        final int marginLeft = comment.getDepth() > 5 ? 5 * COMMENT_INDENT_MARGIN : comment.getDepth() * COMMENT_INDENT_MARGIN;
        containerImageLayoutParams.setMargins(marginLeft, containerImageLayoutParams.topMargin, containerImageLayoutParams.rightMargin, containerImageLayoutParams.bottomMargin);

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
        if (comments != null) {
            if (commentsList == Collections.<Comment>emptyList()) {
                commentsList = new ArrayList<Comment>(comments.size());
            }

            comments.getExpandedOrderedComments(commentsList);
        }

        super.notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetInvalidated() {
        if (comments != null) {
            if (commentsList == Collections.<Comment>emptyList()) {
                commentsList = new ArrayList<Comment>(comments.size());
            }

            comments.getExpandedOrderedComments(commentsList);
        }

        super.notifyDataSetInvalidated();
    }

    public void clear() {
        if (comments != null) {
            comments.clear();
        }
    }

    public void clearJustExpanded() {
        for (Comment comment : commentsList) {
            comment.setJustExpanded(false);
        }
    }
}
