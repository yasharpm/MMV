package com.yashoid.mmv;

import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelFeatures implements Parcelable {

    public static class Builder {

        private Map<String, Object> mFeatures = new HashMap<>();

        public Builder add(String name, Object value) {
            mFeatures.put(name, value);

            return this;
        }

        public Builder addAll(Map<String, Object> features) {
            mFeatures.putAll(features);

            return this;
        }

        public ModelFeatures build() {
            ModelFeatures modelFeatures = new ModelFeatures();

            modelFeatures.mFeatures.putAll(mFeatures);

            return modelFeatures;
        }

    }

    private Handler mHandler;

    private HashMap<String, Object> mFeatures = new HashMap<>();

    private List<OnFeatureChangedListener> mListeners = new ArrayList<>();

    private List<String> mChangedFeatureNames = new ArrayList<>();

    private ModelFeatures() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    protected ModelFeatures(Parcel in) {
        this();

        in.readMap(mFeatures, getClass().getClassLoader());
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

    protected void notifyFeaturesChanged(String... featureNames) {
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

    protected boolean matchesWith(ModelFeatures features, List<String> identifyingFeatures) {
        for (String name: mFeatures.keySet()) {
            if (!identifyingFeatures.contains(name)) {
                continue;
            }

            if (!features.mFeatures.containsKey(name) || !featureValuesEquals(mFeatures.get(name), features.mFeatures.get(name))) {
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
            return slave == null;
        }
        else {
            return master.equals(slave);
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(mFeatures.entrySet().toArray());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeMap(mFeatures);
    }

    public static final Creator<ModelFeatures> CREATOR = new Creator<ModelFeatures>() {

        @Override
        public ModelFeatures createFromParcel(Parcel in) {
            return new ModelFeatures(in);
        }

        @Override
        public ModelFeatures[] newArray(int size) {
            return new ModelFeatures[size];
        }

    };

}
