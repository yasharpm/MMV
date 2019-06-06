package com.yashoid.mmv;

import java.util.List;

public interface TypeProvider {

    List<Action> getActions(ModelFeatures features, String actionName, Object... params);

}
