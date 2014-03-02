package com.xeppaka.lentareader.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xeppaka.lentareader.data.comments.Comment;

import java.util.Collections;
import java.util.List;

/**
 * Created by nnm on 3/1/14.
 */
public class CommentsAdapter extends BaseAdapter {
    private boolean downloadImages;
    private int textSize;
    private LayoutInflater inflater;

    private List<Comment> comments = Collections.emptyList();

    private static class ViewHolder {

    }

    public CommentsAdapter(Context context) {
        inflater = LayoutInflater.from(context);
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

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final TextView tv = new TextView(inflater.getContext());
        tv.setText(comments.get(position).getText());

        return tv;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }
}
