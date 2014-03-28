package com.xeppaka.lentareader.ui.widgets.fullnews;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.utils.LentaTextUtils;

/**
 * Created by nnm on 3/15/14.
 */
public class FullNewsFooter extends FullNewsElementBase {
    private final String date;
    private final String rubric;

    public FullNewsFooter(String date, String rubric, Context context, Fragment fragment) {
        super(context, fragment);

        this.date = date;
        this.rubric = rubric;
    }

    public FullNewsFooter(News news, Context context, Fragment fragment) {
        this(news.getFormattedPubDate(), news.getRubric().getLabel(), context, fragment);
    }

    @Override
    protected View createRootView(LayoutInflater inflater, ViewGroup parent) {
        final View footer = inflater.inflate(R.layout.full_news_footer, parent);
        final ElementOptions options = getOptions();

        final TextView dateView = (TextView)footer.findViewById(R.id.full_news_date);
        final TextView rubricView = (TextView)footer.findViewById(R.id.full_news_rubric);
        final TextView rubricViewLabel = (TextView)footer.findViewById(R.id.full_news_rubric_label);

        dateView.setText(date);
        dateView.setTextSize(LentaTextUtils.getNewsFullDateTextSize(options.getTextSize()));
        rubricViewLabel.setText(" " + rubric);
        rubricViewLabel.setTextSize(LentaTextUtils.getNewsFullRubricTextSize(options.getTextSize()));
        rubricView.setTextSize(LentaTextUtils.getNewsFullRubricTextSize(options.getTextSize()));

        final int px = getPadding();
        dateView.setPadding(px, 0, px, 0);
        rubricView.setPadding(px, 0, px, 0);

        return footer;
    }
}
