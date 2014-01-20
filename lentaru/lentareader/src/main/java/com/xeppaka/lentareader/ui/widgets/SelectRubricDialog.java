package com.xeppaka.lentareader.ui.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.data.Rubrics;

/**
 * Created by nnm on 12/29/13.
 */
public class SelectRubricDialog extends AlertDialog implements View.OnClickListener, DialogInterface.OnCancelListener {
    private Rubrics selectedRubric;

    public SelectRubricDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = View.inflate(getContext(), R.layout.select_rubric, null);

        view.findViewById(R.id.button_rubric_latest).setOnClickListener(this);
        view.findViewById(R.id.button_rubric_russia).setOnClickListener(this);
        view.findViewById(R.id.button_rubric_world).setOnClickListener(this);
        view.findViewById(R.id.button_rubric_ussr).setOnClickListener(this);
        view.findViewById(R.id.button_rubric_economics).setOnClickListener(this);
        view.findViewById(R.id.button_rubric_science).setOnClickListener(this);
        view.findViewById(R.id.button_rubric_sport).setOnClickListener(this);
        view.findViewById(R.id.button_rubric_culture).setOnClickListener(this);
        view.findViewById(R.id.button_rubric_internet).setOnClickListener(this);
        view.findViewById(R.id.button_rubric_life).setOnClickListener(this);

        setContentView(view);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_rubric_latest:
                selectRubric(Rubrics.LATEST);
                break;
            case R.id.button_rubric_russia:
                selectRubric(Rubrics.RUSSIA);
                break;
            case R.id.button_rubric_culture:
                selectRubric(Rubrics.CULTURE);
                break;
            case R.id.button_rubric_economics:
                selectRubric(Rubrics.ECONOMICS);
                break;
            case R.id.button_rubric_internet:
                selectRubric(Rubrics.MEDIA);
                break;
            case R.id.button_rubric_life:
                selectRubric(Rubrics.LIFE);
                break;
            case R.id.button_rubric_science:
                selectRubric(Rubrics.SCIENCE);
                break;
            case R.id.button_rubric_sport:
                selectRubric(Rubrics.SPORT);
                break;
            case R.id.button_rubric_ussr:
                selectRubric(Rubrics.USSR);
                break;
            case R.id.button_rubric_world:
                selectRubric(Rubrics.WORLD);
                break;
        }
    }

    private void selectRubric(Rubrics rubric) {
        selectedRubric = rubric;
        dismiss();
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        selectedRubric = null;
    }

    public Rubrics getSelectedRubric() {
        return selectedRubric;
    }
}
