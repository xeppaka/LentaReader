package com.xeppaka.lentareader.ui.widgets.fullnews;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
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
    protected View createRootView(LayoutInflater inflater) {
        final View footer = inflater.inflate(R.layout.full_news_footer, null);
        final ElementOptions options = getOptions();

        final TextView dateView = (TextView)footer.findViewById(R.id.full_news_date);
        final TextView rubricView = (TextView)footer.findViewById(R.id.full_news_rubric);

        dateView.setText(date);
        dateView.setTextSize(LentaTextUtils.getNewsFullDateTextSize(options.getTextSize()));
        rubricView.setText(rubric);
        rubricView.setTextSize(LentaTextUtils.getNewsFullRubricTextSize(options.getTextSize()));

        return footer;
    }
}
