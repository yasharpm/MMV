package com.yashoid.mmv.fullsample.post;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yashoid.mmv.Managers;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.fullsample.Basics;
import com.yashoid.mmv.fullsample.R;
import com.yashoid.mmv.fullsample.person.Person;
import com.yashoid.mmv.Target;

public class PostView extends FrameLayout implements View.OnClickListener, Target, Post {

    private Model mModel;

    private TextView mTextContent;
    private TextView mTextPoints;
    private View mButtonAddPoints;
    private View mButtonRemovePoints;

    private Target mPersonPointsChangingTarget = null;

    public PostView(@NonNull Context context) {
        super(context);
        initialize(context, null, 0);
    }

    public PostView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0);
    }

    public PostView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    private void initialize(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.view_post, this, true);

        mTextContent = findViewById(R.id.text_content);
        mTextPoints = findViewById(R.id.text_points);
        mButtonAddPoints = findViewById(R.id.button_addpoints);
        mButtonRemovePoints = findViewById(R.id.button_removepoints);

        mButtonAddPoints.setOnClickListener(this);
        mButtonRemovePoints.setOnClickListener(this);
    }

    public Model getModel() {
        return mModel;
    }

    @Override
    public void onClick(View v) {
        if (mModel == null) {
            return;
        }

        Integer points = mModel.get(POINTS);
        int pointChange = 0;

        if (points == null) {
            points = 0;
        }

        switch (v.getId()) {
            case R.id.button_addpoints:
                pointChange = 1;
                points += 1;
                break;
            case R.id.button_removepoints:
                if (points > 0) {
                    pointChange = -1;
                    points -= 1;
                }
                break;
        }

        mModel.set(POINTS, points);

        if (pointChange == 0) {
            return;
        }

        ModelFeatures personFeatures = new ModelFeatures.Builder()
                .add(Basics.TYPE, Person.TYPE)
                .add(Person.ID, mModel.get(PERSON_ID))
                .build();

        final int finalPointChange = pointChange;

        mPersonPointsChangingTarget = new Target() {
            @Override
            public void setModel(Model model) {
                Integer personPoints = model.get(Person.POINTS);

                if (personPoints != null) {
                    model.set(Person.POINTS, personPoints + finalPointChange);
                }

                Managers.getInstance().unregisterTarget(this, model);

                mPersonPointsChangingTarget = null;
            }

            @Override public void onFeaturesChanged(String... featureNames) { }

        };

        Managers.getInstance().registerTarget(mPersonPointsChangingTarget, personFeatures);
    }

    @Override
    public void setModel(Model model) {
        mModel = model;

        onModelChanged();
    }

    @Override
    public void onFeaturesChanged(String... featureNames) {
        onModelChanged();
    }

    private void onModelChanged() {
        mTextContent.setText((String) mModel.get(CONTENT));
        mTextPoints.setText("" + mModel.get(POINTS));
    }

}
