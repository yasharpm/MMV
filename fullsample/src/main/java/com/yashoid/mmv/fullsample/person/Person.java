package com.yashoid.mmv.fullsample.person;

import com.yashoid.mmv.Action;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.TypeProvider;
import com.yashoid.mmv.fullsample.Basics;

import java.util.List;

public interface Person {

    String TYPE = "Person";

    String ID = "id";
    String PHOTO = "photo";
    String NAME = "name";
    String POINTS = "points";

    class PersonType implements TypeProvider {

        @Override
        public boolean isOfType(ModelFeatures features) {
            return TYPE.equals(features.get(Basics.TYPE));
        }

        @Override
        public Action getAction(ModelFeatures features, String actionName, Object... params) {
            if (Action.ACTION_MODEL_CREATED.equals(actionName)) {
            }

            if (Action.ACTION_MODEL_LOADED_FROM_CACHE.equals(actionName)) {
                return mLoadedAction;
            }

            return null;
        }

        @Override
        public void getIdentifyingFeatures(ModelFeatures features, List<String> identifyingFeatures) {
            identifyingFeatures.add(Basics.TYPE);
            identifyingFeatures.add(ID);
        }

        private Action mLoadedAction = new Action() {

            @Override
            public Object perform(Model model, Object... params) {
                String name = model.get(NAME);
                model.set(PHOTO, PersonList.PersonListType.createBitmap(name));
                return null;
            }

        };

    }

}
