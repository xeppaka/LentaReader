package com.xeppaka.lentareader.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

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

    public CommentsListFragment(String xid) {
        this.xid = xid;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        setListAdapter(commentsAdapter = new CommentsAdapter(activity));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Comment comment = commentsAdapter.getItem(position);
                comment.setExpanded(!comment.isExpanded());

                commentsAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        showComments();
    }

    private void showComments() {
        final LentaCommentsDownloader commentsDownloader = new LentaCommentsDownloader();
        commentsDownloader.downloadAsync(xid, new AsyncListener<String>() {
            @Override
            public void onSuccess(String value) {
                final CommentsParser parser = new CommentsParser();
                try {
                    loadedComments = parser.parse(value);
                    commentsAdapter.setComments(loadedComments);
                    commentsAdapter.notifyDataSetChanged();
                } catch (ParseException e) {
                    Toast.makeText(getActivity(), "АШЫПКА ПАРСЕРА!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getActivity(), "АШЫПКА ЗАГРУЗКИ!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
