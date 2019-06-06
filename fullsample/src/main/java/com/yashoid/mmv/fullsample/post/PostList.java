package com.yashoid.mmv.fullsample.post;

import android.os.Handler;

import com.yashoid.mmv.Action;
import com.yashoid.mmv.Managers;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.TypeProvider;
import com.yashoid.mmv.fullsample.Basics;
import com.yashoid.mmv.fullsample.Stateful;
import com.yashoid.mmv.fullsample.person.Person;
import com.yashoid.mmv.Target;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface PostList extends Stateful {

    String TYPE = "PostList";

    String PERSON_ID = "personId";
    String POSTS = "posts";

    String GET_POSTS = "getPosts";

    class PostListType implements TypeProvider {

        private Action getPosts = new GetPostsAction();

        @Override
        public List<Action> getActions(ModelFeatures features, String actionName, Object... params) {
            if (!TYPE.equals(features.get(Basics.TYPE))) {
                return null;
            }

            switch (actionName) {
                case GET_POSTS:
                    return Arrays.asList(getPosts);
            }

            return null;
        }

        class GetPostsAction implements Action {

            private List<Target> performingActions = new ArrayList<>();

            @Override
            public Object perform(final Model model, Object... params) {
                model.set(STATUS, STATUS_LOADING);

                ModelFeatures personFeatures = new ModelFeatures.Builder()
                        .add(Basics.TYPE, Person.TYPE)
                        .add(Person.ID, model.get(PERSON_ID))
                        .build();

                Target performingAction = new Target() {

                    @Override
                    public void setModel(final Model personModel) {
                        Managers.unregisterTarget(this, personModel);

                        performingActions.remove(this);

                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                Integer personId = model.get(PERSON_ID);
                                String personName = personModel.get(Person.NAME);

                                List<ModelFeatures> postsFeatures = new ArrayList<>();

                                ModelFeatures postFeatures = new ModelFeatures.Builder()
                                        .add(Post.ID, personId * 100 + 1)
                                        .add(Post.PERSON_ID, personId)
                                        .add(Post.CONTENT, personName + " has said something new.")
                                        .add(Post.POINTS, 6)
                                        .build();
                                postsFeatures.add(postFeatures);

                                postFeatures = new ModelFeatures.Builder()
                                        .add(Post.ID, personId * 100 + 2)
                                        .add(Post.PERSON_ID, personId)
                                        .add(Post.CONTENT, personName + " has said something not very new.")
                                        .add(Post.POINTS, 3)
                                        .build();
                                postsFeatures.add(postFeatures);

                                postFeatures = new ModelFeatures.Builder()
                                        .add(Post.ID, personId * 100 + 3)
                                        .add(Post.PERSON_ID, personId)
                                        .add(Post.CONTENT, personName + " has said something really old.")
                                        .add(Post.POINTS, 1)
                                        .build();
                                postsFeatures.add(postFeatures);

                                model.set(POSTS, postsFeatures);
                                model.set(STATUS, STATUS_SUCCESS);
                            }

                        }, 2000);
                    }

                    @Override
                    public void onFeaturesChanged(String... featureNames) { }

                };

                performingActions.add(performingAction);

                Managers.registerTarget(performingAction, personFeatures);

                return null;
            }

        }

    }

}
