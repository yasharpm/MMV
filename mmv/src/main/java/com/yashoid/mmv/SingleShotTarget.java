package com.yashoid.mmv;

public class SingleShotTarget {

    public interface ModelCallback {

        void onModelReady(Model model);

    }

    public static void get(ModelFeatures modelFeatures, final ModelCallback callback) {
        Managers.registerTarget(new PersistentTarget() {

            @Override
            public void setModel(Model model) {
                callback.onModelReady(model);

                Managers.unregisterTarget(this);
            }

            @Override
            public void onFeaturesChanged(String... featureNames) { }

        }, modelFeatures);
    }

    public static void perform(ModelFeatures modelFeatures, final String action, final Object... params) {
        get(modelFeatures, new ModelCallback() {

            @Override
            public void onModelReady(Model model) {
                model.perform(action, params);
            }

        });
    }

}
