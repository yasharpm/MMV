package com.yashoid.mmv.fullsample.post;

import com.yashoid.mmv.Action;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.TypeProvider;
import com.yashoid.mmv.fullsample.Basics;

import java.util.List;

public interface Post {

    String TYPE = "Post";

    String ID = "id";
    String PERSON_ID = "personId";
    String CONTENT = "content";
    String POINTS = "points";

    class PostType implements TypeProvider {

        @Override
        public Action getAction(ModelFeatures features, String actionName, Object... params) {
            return null;
        }

        @Override
        public void getIdentifyingFeatures(ModelFeatures features, List<String> identifyingFeatures) {
            if (TYPE.equals(features.get(Basics.TYPE))) {
                identifyingFeatures.add(Basics.TYPE);
                identifyingFeatures.add(ID);
            }
        }

    }

}
