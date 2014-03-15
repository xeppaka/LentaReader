package com.xeppaka.lentareader.ui.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.async.AsyncListener;
import com.xeppaka.lentareader.data.comments.Comment;
import com.xeppaka.lentareader.data.comments.Comments;
import com.xeppaka.lentareader.downloader.comments.LentaCommentsDownloader;
import com.xeppaka.lentareader.downloader.comments.LentaCommentsStream;
import com.xeppaka.lentareader.downloader.comments.StreamListener;
import com.xeppaka.lentareader.parser.comments.CommentsParser;
import com.xeppaka.lentareader.parser.exceptions.ParseException;
import com.xeppaka.lentareader.ui.adapters.CommentsAdapter;

import org.java_websocket.handshake.ServerHandshake;

/**
 * Created by kacpa01 on 2/28/14.
 */
public class CommentsListFragment extends ListFragment implements StreamListener {
    private CommentsAdapter commentsAdapter;
    private Comments loadedComments;
    private String xid;
    private String errorLoadingComments;
    private String errorLoadingCommentsListMessage;
    private String noCommentsText;
    private LentaCommentsStream commentsStream;
    private TextView nowReadCountView;
    private int tmp;

    public CommentsListFragment(String xid, TextView nowReadCountView) {
        this.xid = xid;
        this.nowReadCountView = nowReadCountView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context context = getActivity();
        final Resources resources = context.getResources();

        noCommentsText = resources.getString(R.string.comment_no_comments);
        errorLoadingComments = resources.getString(R.string.comment_error_loading);
        errorLoadingCommentsListMessage = resources.getString(R.string.comment_error_loading_list_message);
        commentsAdapter = new CommentsAdapter(context);

        return super.onCreateView(inflater, container, savedInstanceState);
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
                    commentsAdapter.clearJustExpanded();

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
                    commentsAdapter.clearJustExpanded();

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

    @Override
    public void onPause() {
        super.onPause();

        disconnectWebSocket();
    }

    private void refreshComments() {
        setEmptyText(noCommentsText);

        final LentaCommentsDownloader commentsDownloader = new LentaCommentsDownloader();

        commentsDownloader.downloadAsync(xid, new AsyncListener<String>() {
            @Override
            public void onSuccess(String value) {
                if (isResumed()) {
                    final CommentsParser parser = new CommentsParser();
                    try {
                        loadedComments = parser.parse(value);
                        showComments();

                        if (loadedComments.getStreamId() != null) {
                            connectWebSocket(loadedComments.getStreamId());
                        }
                    } catch (ParseException e) {
                        setEmptyText(errorLoadingCommentsListMessage);

                        loadedComments = new Comments();
                        showComments();
                        Toast.makeText(getActivity(), errorLoadingComments, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
                if (isResumed()) {
                    setEmptyText(errorLoadingCommentsListMessage);

                    loadedComments = new Comments();
                    showComments();
                }
            }
        });
    }

    private void connectWebSocket(String streamId) {
        if (commentsStream == null) {
            commentsStream = new LentaCommentsStream(streamId);
            commentsStream.connect(CommentsListFragment.this);
        }
    }

    private void disconnectWebSocket() {
        if (commentsStream != null) {
            commentsStream.disconnect();
        }
    }

    private void showComments() {
        commentsAdapter.setComments(loadedComments);
        setListAdapter(commentsAdapter);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        new Handler(CommentsListFragment.this.getActivity().getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), "Web Socket connected.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMessage(final String message) {
        new Handler(CommentsListFragment.this.getActivity().getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), "Message received: " + message, Toast.LENGTH_SHORT).show();
                nowReadCountView.setText(" " + tmp++);
            }
        });
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        new Handler(CommentsListFragment.this.getActivity().getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), "Web Socket closed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onError(final Exception ex) {
        new Handler(CommentsListFragment.this.getActivity().getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), "Web Socket error: " + ex.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
