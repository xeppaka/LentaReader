package com.xeppaka.lentareader.ui.fragments;

import android.app.Activity;
import android.support.v4.app.ListFragment;
import android.widget.Toast;

import com.xeppaka.lentareader.async.AsyncListener;
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
                    commentsAdapter.setComments(loadedComments.getOrderedComments());
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
