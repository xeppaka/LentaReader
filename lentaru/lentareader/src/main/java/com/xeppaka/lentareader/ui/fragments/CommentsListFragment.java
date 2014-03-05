package com.xeppaka.lentareader.ui.fragments;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.async.AsyncListener;
import com.xeppaka.lentareader.data.comments.Comment;
import com.xeppaka.lentareader.data.comments.Comments;
import com.xeppaka.lentareader.downloader.LentaCommentsDownloader;
import com.xeppaka.lentareader.parser.comments.CommentsParser;
import com.xeppaka.lentareader.parser.exceptions.ParseException;
import com.xeppaka.lentareader.ui.adapters.CommentsAdapter;

/**
 * Created by kacpa01 on 2/28/14.
 */
public class CommentsListFragment extends ListFragment {
    private CommentsAdapter commentsAdapter;
    private Comments loadedComments;
    private String xid;
    private String errorLoadingComments;
    private View emptyLoadingView;
    private View emptyLoadedView;

    public CommentsListFragment(String xid) {
        this.xid = xid;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        emptyLoadingView = activity.getLayoutInflater().inflate(R.layout.comments_empty_list_loading, null);
        emptyLoadedView = activity.getLayoutInflater().inflate(R.layout.comments_empty_list_loaded, null);

        final Resources resources = activity.getResources();
        errorLoadingComments = resources.getString(R.string.comment_error_loading);
        setListAdapter(commentsAdapter = new CommentsAdapter(activity));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ListView listView = getListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Comment comment = commentsAdapter.getItem(position);

                if (comment.hasChildren()) {
                    comment.setExpanded(!comment.isExpanded());
                    commentsAdapter.notifyDataSetChanged();
                }
            }
        });

        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Comment comment = commentsAdapter.getItem(position);

                if (comment.hasChildren()) {
                    comment.setExpandedRecursive(!comment.isExpanded());
                    commentsAdapter.notifyDataSetChanged();
                }

                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (loadedComments == null) {
            refreshComments();
        }
    }

    private void refreshComments() {
        setEmptyLoadingView();

        commentsAdapter.clear();
        commentsAdapter.notifyDataSetChanged();

        final LentaCommentsDownloader commentsDownloader = new LentaCommentsDownloader();

        commentsDownloader.downloadAsync(xid, new AsyncListener<String>() {
            @Override
            public void onSuccess(String value) {
                if (isResumed()) {
                    final CommentsParser parser = new CommentsParser();
                    try {
                        loadedComments = parser.parse(value);
                        showComments();
                    } catch (ParseException e) {
                        Toast.makeText(getActivity(), errorLoadingComments, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
                if (isResumed()) {
                    Toast.makeText(getActivity(), errorLoadingComments, Toast.LENGTH_SHORT).show();

                    setEmptyViewWithText();
                }
            }
        });
    }

    private void showComments() {
        commentsAdapter.setComments(loadedComments);
        commentsAdapter.notifyDataSetChanged();

        setEmptyViewWithText();
    }

    private void setEmptyLoadingView() {
        final ListView listView = getListView();
        listView.setEmptyView(emptyLoadingView);
    }

    private void setEmptyViewWithText() {
        final ListView listView = getListView();
        listView.setEmptyView(emptyLoadedView);
    }
}
