package com.xeppaka.lentareader.ui.widgets.fullnews;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.async.AsyncListener;
import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.data.dao.daoobjects.BitmapReference;
import com.xeppaka.lentareader.data.dao.daoobjects.imagedaoobjects.ImageDao;
import com.xeppaka.lentareader.data.dao.daoobjects.imagedaoobjects.NewsImageKeyCreator;

import java.util.Date;

/**
 * Created by nnm on 3/15/14.
 */
public class FullNewsListFooter extends FullNewsListElementBase {
    private final String date;
    private final String rubric;

    public FullNewsListFooter(String date, String rubric, Context context, Fragment fragment) {
        super(context, fragment);

        this.date = date;
        this.rubric = rubric;
    }

    public FullNewsListFooter(News news, Context context, Fragment fragment) {
        this(news.getFormattedPubDate(), news.getRubric().getLabel(), context, fragment);
    }

    @Override
    protected View createRootView(LayoutInflater inflater) {
        final View footer = inflater.inflate(R.layout.full_news_footer, null);

        final TextView dateView = (TextView)footer.findViewById(R.id.full_news_date);
        final TextView rubricView = (TextView)footer.findViewById(R.id.full_news_rubric);

        dateView.setText(date);
        rubricView.setText(rubric);

        return footer;
    }
}
