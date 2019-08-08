package com.yashoid.mmv.fullsample;

import com.yashoid.mmv.Action;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.TypeProvider;

import java.util.List;

public interface MainFlow {

    String TYPE = "MainFlow";

    String PERSON_ID = "personId";

    class MainFlowType implements TypeProvider {

        @Override
        public Action getAction(ModelFeatures features, String actionName, Object... params) {
            return null;
        }

        @Override
        public void getIdentifyingFeatures(ModelFeatures features, List<String> identifyingFeatures) {
            identifyingFeatures.add(Basics.TYPE);
        }

    }

}
