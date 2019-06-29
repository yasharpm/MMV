package com.yashoid.mmv;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

public class Managers {

    private static Managers mInstance = null;

    public static void bindLifeCycle(Application application) {
        getInstance()._bindLifeCycle(application);
    }

    public static void registerModel(ModelFeatures modelFeatures) {
        getInstance()._registerModel(modelFeatures);
    }

    public static void registerTarget(Target target, ModelFeatures modelFeatures) {
        getInstance()._registerTarget(target, modelFeatures);
    }

    public static void unregisterTarget(Target target) {
        getInstance()._unregisterTarget(target);
    }

    public static void addTypeProvider(TypeProvider typeProvider) {
        getInstance()._addTypeProvider(typeProvider);
    }

    public static void removeTypeProvider(TypeProvider typeProvider) {
        getInstance()._removeTypeProvider(typeProvider);
    }

    private static Managers getInstance() {
        if (mInstance == null) {
            mInstance = new Managers();
        }

        return mInstance;
    }

    private List<ManagerInfo> mManagersInfo = new ArrayList<>();

    private List<TypeProvider> mTypeProviders = new ArrayList<>();

    private Managers() {

    }

    private void _bindLifeCycle(Application application) {
        // TODO Bind to activities life cycles so the state would be restored.
    }

    private TargetManager _registerModel(ModelFeatures modelFeatures) {
        TargetManager manager = findTargetManager(modelFeatures, null);

        if (manager == null) {
            manager = defineManager(modelFeatures);

            manager.getModel().perform(Action.ACTION_MODEL_CREATED);
        }
        else {
            manager.getModel().getFeatures().updateWith(modelFeatures);
        }

        return manager;
    }

    private void _registerTarget(Target target, ModelFeatures modelFeatures) {
        _registerModel(modelFeatures).register(target);
    }

    private void _unregisterTarget(Target target) {
        for (ManagerInfo managerInfo: mManagersInfo) {
            if (managerInfo.manager.unregister(target)) {
                return;
            }
        }
    }

    private void _addTypeProvider(TypeProvider typeProvider) {
        mTypeProviders.add(typeProvider);
    }

    private void _removeTypeProvider(TypeProvider typeProvider) {
        mTypeProviders.remove(typeProvider);
    }

    protected List<Action> getActions(ModelFeatures features, String actionName, Object... params) {
        List<Action> actions = new ArrayList<>();

        for (TypeProvider typeProvider: mTypeProviders) {
            Action typeAction = typeProvider.getAction(features, actionName, params);

            if (typeAction != null) {
                actions.add(typeAction);
            }
        }

        return actions;
    }

    protected TargetManager findTargetManager(ModelFeatures modelFeatures, TargetManager exception) {
        for (ManagerInfo managerInfo: mManagersInfo) {
            if (managerInfo.manager == exception) {
                continue;
            }

            if (managerInfo.modelFeatures.matchesWith(modelFeatures)) {
                return managerInfo.manager;
            }
        }

        return null;
    }

    private TargetManager defineManager(ModelFeatures features) {
        TargetManager targetManager = new TargetManager(this, features);

        ManagerInfo managerInfo = new ManagerInfo(features, targetManager);

        mManagersInfo.add(managerInfo);

        return targetManager;
    }

    protected void removeManager(TargetManager manager) {
        ManagerInfo managerToRemove = null;

        for (ManagerInfo managerInfo: mManagersInfo) {
            if (managerInfo.manager == manager) {
                managerToRemove = managerInfo;
                break;
            }
        }

        if (managerToRemove != null) {
            mManagersInfo.remove(managerToRemove);
        }
    }

    private static class ManagerInfo {

        private ModelFeatures modelFeatures;

        private TargetManager manager;

        protected ManagerInfo(ModelFeatures features, TargetManager manager) {
            modelFeatures = features;
            this.manager = manager;
        }

    }

}
