package com.yashoid.mmv;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class TargetManager {

    private Managers mManagers;

    private Model mModel;

    private List<WeakReference<Target>> mTargets = new ArrayList<>();
    private List<Target> mPersistentTargets = new ArrayList<>();

    protected TargetManager(Managers managers, ModelFeatures modelFeatures) {
        mManagers = managers;

        mModel = new Model(this, modelFeatures);

        mModel.addOnFeatureChangedListener(mModelFeatureChangedListener);
    }

    protected Object perform(String actionName, Object... params) {
        List<Action> actions = mManagers.getActions(mModel.getFeatures(), actionName, params);

        List<Object> results = new ArrayList<>(actions.size());

        for (Action action: actions) {
            Object result = action.perform(mModel, params);

            if (result != null) {
                results.add(result);
            }
        }

        if (results.isEmpty()) {
            return null;
        }

        if (results.size() == 1) {
            return results.get(0);
        }

        return results.toArray(new Object[results.size()]);
    }

    private OnFeatureChangedListener mModelFeatureChangedListener = new OnFeatureChangedListener() {

        @Override
        public void onFeatureChanged(String... features) {
            TargetManager contestingTargetManager = findTargetManagerForSameModel();

            if (contestingTargetManager != null) {
                mModel.removeOnFeatureChangedListener(mModelFeatureChangedListener);

                unregisterAll();

                contestingTargetManager.registerTargets(mTargets);
                contestingTargetManager.registerPersistentTargets(mPersistentTargets);

                removeSelfFromManagers();

                contestingTargetManager.getModel().mergeWith(mModel);

                return;
            }

            notifyTargets(features);
        }

    };

    protected void register(Target target) {
        if (target instanceof PersistentTarget) {
            mPersistentTargets.add(target);
        }
        else {
            mTargets.add(new WeakReference<>(target));
        }

        target.setModel(mModel);
    }

    private void registerTargets(List<WeakReference<Target>> targets) {
        ListIterator<WeakReference<Target>> targetIterator = targets.listIterator();

        List<Target> existingTargets = new ArrayList<>(targets.size());

        while (targetIterator.hasNext()) {
            WeakReference<Target> targetReference = targetIterator.next();
            Target target = targetReference.get();

            if (target == null) {
                targetIterator.remove();
            }
            else {
                existingTargets.add(target);
            }
        }

        mTargets.addAll(targets);

        for (Target target: existingTargets) {
            target.setModel(mModel);
        }
    }

    private void registerPersistentTargets(List<Target> targets) {
        mPersistentTargets.addAll(targets);

        for (Target target: targets) {
            target.setModel(mModel);
        }
    }

    private void unregisterAll() {
        mTargets.clear();
        mPersistentTargets.clear();
    }

    public boolean unregister(Target target) {
        if (mPersistentTargets.remove(target)) {
            return true;
        }

        ListIterator<WeakReference<Target>> targetIterator = mTargets.listIterator();

        while (targetIterator.hasNext()) {
            WeakReference<Target> targetReference = targetIterator.next();

            Target t = targetReference.get();

            if (t == null) {
                targetIterator.remove();
            }
            else if (t == target) {
                targetIterator.remove();

                return true;
            }
        }

        return false;
    }

    private TargetManager findTargetManagerForSameModel() {
        return mManagers.findTargetManager(mModel.getFeatures(), this);
    }

    private void notifyTargets(String... featureNames) {
        List<Target> targets = new ArrayList<>(mTargets.size() + mPersistentTargets.size());

        ListIterator<WeakReference<Target>> targetIterator = mTargets.listIterator();

        while (targetIterator.hasNext()) {
            WeakReference<Target> targetReference = targetIterator.next();

            Target target = targetReference.get();

            if (target == null) {
                targetIterator.remove();
            }
            else {
                targets.add(target);
            }
        }

        targets.addAll(mPersistentTargets);

        for (Target target: targets) {
            target.onFeaturesChanged(featureNames);
        }
    }

    private void removeSelfFromManagers() {
        mManagers.removeManager(this);
    }

    protected Model getModel() {
        return mModel;
    }

}
