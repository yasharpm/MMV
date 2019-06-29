package com.yashoid.mmv.fullsample;

import android.util.Log;

import com.yashoid.mmv.Action;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.TypeProvider;

public class LoggerType implements TypeProvider {

    private static final String TAG = "Logger";

    @Override
    public Action getAction(ModelFeatures features, String actionName, Object... params) {
        Log.i(TAG, ((System.currentTimeMillis() % 60_000) / 1000) + " Calling '" + actionName + "' on " + features.get(Basics.TYPE) + ".");
        return null;
    }

}
