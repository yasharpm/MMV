package com.yashoid.mmv.target;

import androidx.appcompat.app.AppCompatActivity;

import com.yashoid.mmv.Model;

public class ActivityTarget extends AppCompatActivity implements Target {

    private Model mModel;

    @Override
    public void setModel(Model model) {
        mModel = model;

        onModelChanged();
    }

    protected Model getModel() {
        return mModel;
    }

    @Override
    public void onFeaturesChanged(String... featureNames) {
        onModelChanged(featureNames);
    }

    protected void onModelChanged(String... featureNames) {

    }

}
