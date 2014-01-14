package com.xeppaka.lentareader.ui.fragments;

import android.app.Activity;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import com.xeppaka.lentareader.data.NewsObject;
import com.xeppaka.lentareader.data.NewsType;
import com.xeppaka.lentareader.data.Rubrics;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by nnm on 12/27/13.
 */
public abstract class NewsObjectListFragment extends ListFragment implements RubricsSelector {
    protected Rubrics currentRubric = Rubrics.LATEST;

    // expanded items for each rubric
    protected Set<Long>[] expandedItemIds = new Set[Rubrics.values().length];
    protected ScrollerPosition[] scrollPositions = new ScrollerPosition[Rubrics.values().length];
    protected long[] latestNewsTime = new long[Rubrics.values().length];
    private ItemSelectionListener itemSelectionListener;

    public interface ItemSelectionListener {
        void onItemSelected(long id);
    }

    private static class ScrollerPosition {
        private int item;
        private int top;
    }

    protected NewsObjectListFragment() {
        for (Rubrics rubric : Rubrics.values()) {
            expandedItemIds[rubric.ordinal()] = new HashSet<Long>();
            scrollPositions[rubric.ordinal()] = new ScrollerPosition();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        setItemSelectionListener(null);
    }

    @Override
    public void onResume() {
        super.onResume();

        final Activity activity = getActivity();
        if (activity instanceof ItemSelectionListener) {
            setItemSelectionListener((ItemSelectionListener)activity);
        }

        if (activity instanceof RubricsSelectorContainer) {
            ((RubricsSelectorContainer)activity).setRubricsSelector(getNewsType(), this);
        }
    }

    public Rubrics getCurrentRubric() {
        return currentRubric;
    }

    public void setCurrentRubric(Rubrics currentRubric) {
        saveScrollPosition();
        this.currentRubric = currentRubric;

        refresh();
    }

    public Set<Long> getExpandedItemIds() {
        return expandedItemIds[currentRubric.ordinal()];
    }

    public void saveScrollPosition() {
        final int item = getListView().getFirstVisiblePosition();
        final View childView = getListView().getChildAt(0);
        final int top = childView == null ? 0 : childView.getTop();

        scrollPositions[currentRubric.ordinal()].item = item;
        scrollPositions[currentRubric.ordinal()].top = top;
    }

    public void restoreScrollPosition() {
        if (scrollPositions[currentRubric.ordinal()].item < getListAdapter().getCount()) {
            getListView().setSelectionFromTop(scrollPositions[currentRubric.ordinal()].item, scrollPositions[currentRubric.ordinal()].top);
        } else {
            clearScrollPosition();
        }
    }

    public void clearScrollPosition() {
        scrollPositions[currentRubric.ordinal()].item = scrollPositions[currentRubric.ordinal()].top = 0;
    }

    public Long getLatestPubDate() {
        return latestNewsTime[currentRubric.ordinal()];
    }

    public void setLatestPubDate(long date) {
        latestNewsTime[currentRubric.ordinal()] = date;
    }

    public void setItemSelectionListener(ItemSelectionListener itemSelectionListener) {
        this.itemSelectionListener = itemSelectionListener;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (itemSelectionListener != null) {
            itemSelectionListener.onItemSelected(id);
        }
    }

    public abstract NewsType getNewsType();
    public abstract NewsObjectAdapter getDataObjectsAdapter();
    public abstract void refresh();
}
