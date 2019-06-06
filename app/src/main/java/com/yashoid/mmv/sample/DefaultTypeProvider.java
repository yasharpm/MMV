package com.yashoid.mmv.sample;

import com.yashoid.mmv.Action;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.TypeProvider;

import java.util.Arrays;
import java.util.List;

public class DefaultTypeProvider implements TypeProvider {

    @Override
    public List<Action> getActions(ModelFeatures features, String actionName, Object... params) {
        return Arrays.asList((Action) new DefaultAction());
    }

    private class DefaultAction implements Action {

        @Override
        public Object perform(Model model, Object... params) {
            model.set("Hello", "World!");
            return null;
        }

    }

}
