package com.yashoid.mmv;

public interface TypeProvider {

    Action getAction(ModelFeatures features, String actionName, Object... params);

}
