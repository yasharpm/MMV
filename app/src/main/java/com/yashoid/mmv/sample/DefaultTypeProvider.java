package com.yashoid.mmv.sample;

import com.yashoid.mmv.Action;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.TypeProvider;

import java.util.List;

public class DefaultTypeProvider implements TypeProvider {

    @Override
    public Action getAction(ModelFeatures features, String actionName, Object... params) {
        if (Action.ACTION_MODEL_CREATED.equals(actionName)) {
            return null;
        }

        return new DefaultAction();
    }

    @Override
    public void getIdentifyingFeatures(ModelFeatures features, List<String> identifyingFeatures) {

    }

    private class DefaultAction implements Action {

        @Override
        public Object perform(Model model, Object... params) {
            model.set("Hello", "World!");

            return null;
        }

    }

}
