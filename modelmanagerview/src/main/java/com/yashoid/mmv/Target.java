package com.yashoid.mmv;

public interface Target {

    void setModel(Model model);

    void onFeaturesChanged(String... featureNames);

}
