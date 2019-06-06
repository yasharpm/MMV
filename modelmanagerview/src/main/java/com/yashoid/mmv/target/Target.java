package com.yashoid.mmv.target;

import com.yashoid.mmv.Model;

public interface Target {

    void setModel(Model model);

    void onFeaturesChanged(String... featureNames);

}
