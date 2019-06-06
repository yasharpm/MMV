package com.yashoid.mmv;

import java.util.Map;

public class Model {

    private TargetManager mManager;

    private ModelFeatures mFeatures;

    protected Model(TargetManager manager, ModelFeatures features) {
        mManager = manager;

        mFeatures = features;
    }

    public Map<String, Object> getAllFeatures() {
        return mFeatures.getAll();
    }

    public<T> T get(String name) {
        return mFeatures.get(name);
    }

    public void set(String name, Object value) {
        mFeatures.set(name, value);
    }

    protected ModelFeatures getFeatures() {
        return mFeatures;
    }

    public Object perform(String action, Object... params) {
        return mManager.perform(action, params);
    }

    protected void mergeWith(Model model) {
         mFeatures.updateWith(model.mFeatures);
    }

    public void addOnFeatureChangedListener(OnFeatureChangedListener listener) {
        mFeatures.addOnFeatureChangedListener(listener);
    }

    public void removeOnFeatureChangedListener(OnFeatureChangedListener listener) {
        mFeatures.removeOnFeatureChangedListener(listener);
    }

}
