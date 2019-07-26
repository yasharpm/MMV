package com.yashoid.mmv;

import java.util.List;

public interface TypeProvider {

    Action getAction(ModelFeatures features, String actionName, Object... params);

    void getIdentifyingFeatures(ModelFeatures features, List<String> identifyingFeatures);

}
