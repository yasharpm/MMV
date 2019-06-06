package com.yashoid.mmv;

import com.yashoid.mmv.target.Target;

import java.util.ArrayList;
import java.util.List;

public class Managers {

    private static Managers mInstance = null;

    public static Managers getInstance() {
        if (mInstance == null) {
            mInstance = new Managers();
        }

        return mInstance;
    }

    private List<ManagerInfo> mManagersInfo = new ArrayList<>();

    private List<TypeProvider> mTypeProviders = new ArrayList<>();

    public void registerTarget(Target target, ModelFeatures modelFeatures) {
        TargetManager manager = findTargetManager(modelFeatures, null);

        if (manager == null) {
            manager = defineManager(modelFeatures);
        }
        else {
            manager.getModel().getFeatures().updateWith(modelFeatures);
        }

        manager.register(target);
    }

    public void unregisterTarget(Target target, Model model) {
        TargetManager manager = findTargetManager(model.getFeatures(), null);

        if (manager != null) {
            manager.unregister(target);
        }
    }

    public void addTypeProvider(TypeProvider typeProvider) {
        mTypeProviders.add(typeProvider);
    }

    public void removeTypeProvider(TypeProvider typeProvider) {
        mTypeProviders.remove(typeProvider);
    }

    protected List<Action> getActions(ModelFeatures features, String actionName, Object... params) {
        List<Action> actions = new ArrayList<>();

        for (TypeProvider typeProvider: mTypeProviders) {
            List<Action> typeActions = typeProvider.getActions(features, actionName, params);

            if (typeActions != null) {
                actions.addAll(typeActions);
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

    private class ManagerInfo {

        private ModelFeatures modelFeatures;

        private TargetManager manager;

        protected ManagerInfo(ModelFeatures features, TargetManager manager) {
            modelFeatures = features;
            this.manager = manager;
        }

    }

}
