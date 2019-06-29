package com.yashoid.mmv;

import android.os.Handler;

import java.util.ArrayList;
import java.util.Arrays;
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

    private Handler mHandler;

    private HashMap<String, Object> mFeatures = new HashMap<>();

    private List<OnFeatureChangedListener> mListeners = new ArrayList<>();

    private List<String> mChangedFeatureNames = new ArrayList<>();

    private ModelFeatures() {
        mHandler = new Handler();
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
        mChangedFeatureNames.addAll(Arrays.asList(featureNames));

        mHandler.removeCallbacks(mFeaturesChangedNotifier);
        mHandler.post(mFeaturesChangedNotifier);
    }

    private Runnable mFeaturesChangedNotifier = new Runnable() {

        @Override
        public void run() {
            String[] featureNames = mChangedFeatureNames.toArray(new String[mChangedFeatureNames.size()]);

            mChangedFeatureNames.clear();

            List<OnFeatureChangedListener> listeners = new ArrayList<>(mListeners);

            for (OnFeatureChangedListener listener: listeners) {
                listener.onFeatureChanged(featureNames);
            }
        }

    };

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
