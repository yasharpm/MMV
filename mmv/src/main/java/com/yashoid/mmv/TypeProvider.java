package com.yashoid.mmv;

import java.util.List;

public interface TypeProvider {

    boolean isOfType(ModelFeatures features);

    Action getAction(ModelFeatures features, String actionName, Object... params);

    void getIdentifyingFeatures(ModelFeatures features, List<String> identifyingFeatures);

}
