package com.yashoid.mmv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelFeatures {

    public static class Builder {

        private ModelFeatures mFeatures = new ModelFeatures();

        public Builder add(String name, Object value) {
            mFeatures.mFeatures.put(name, value);

            return this;
        }

        public Builder addAll(Map<String, Object> features) {
            mFeatures.mFeatures.putAll(features);

            return this;
        }

        public ModelFeatures build() {
            return mFeatures;
        }

    }

    private HashMap<String, Object> mFeatures = new HashMap<>();

    private List<OnFeatureChangedListener> mListeners = new ArrayList<>();

    private ModelFeatures() {

    }

    public Map<String, Object> getAll() {
        return new HashMap<>(mFeatures);
    }

    public<T> T get(String name) {
        return (T) mFeatures.get(name);
    }

    public void set(String name, Object value) {
        mFeatures.put(name, value);

        notifyFeaturesChanged(name);
    }

    public void addOnFeatureChangedListener(OnFeatureChangedListener listener) {
        mListeners.add(listener);
    }

    public void removeOnFeatureChangedListener(OnFeatureChangedListener listener) {
        mListeners.remove(listener);
    }

    private void notifyFeaturesChanged(String... featureNames) {
        List<OnFeatureChangedListener> listeners = new ArrayList<>(mListeners);

        for (OnFeatureChangedListener listener: listeners) {
            listener.onFeatureChanged(featureNames);
        }
    }

    protected boolean matchesWith(ModelFeatures features) {
        for (String name: mFeatures.keySet()) {
            if (features.mFeatures.containsKey(name) && !featureValuesEquals(mFeatures.get(name), features.mFeatures.get(name))) {
                return false;
            }
        }

        return true;
    }

    protected void updateWith(ModelFeatures features) {
        mFeatures.putAll(features.mFeatures);
    }

    private static boolean featureValuesEquals(Object master, Object slave) {
        if (master == null) {
            return true;
        }
        else {
            return master.equals(slave);
        }
    }

}
