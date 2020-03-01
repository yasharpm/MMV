package com.yashoid.mmv;

import android.app.Application;
import android.content.Context;

import com.yashoid.mmv.cache.GetModelCallback;
import com.yashoid.mmv.cache.ModelCache;
import com.yashoid.office.task.TaskManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Managers {

    private static Managers mInstance = null;

    public static void bindLifeCycle(Application application) {
        getInstance()._bindLifeCycle(application);
    }

    public static void enableCache(Context context, TaskManager taskManager) {
        getInstance()._enableCache(context, taskManager);
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

    private Map<TypeProvider, Map<String, Action>> mActionCache = new HashMap<>();

    private ModelCache mModelCache = null;

    private Managers() {

    }

    private void _bindLifeCycle(Application application) {
        // TODO Bind to activities life cycles so the state would be restored.
    }

    private void _enableCache(Context context, TaskManager taskManager) {
        if (mModelCache != null) {
            throw new RuntimeException("Cache has already been enabled on Managers.");
        }

        mModelCache = new ModelCache(context, taskManager);
    }

    private TargetManager _registerModel(ModelFeatures modelFeatures) {
        TargetManager manager = findTargetManager(modelFeatures, null);

        if (manager == null) {
            manager = defineManager(modelFeatures);

            manager.getModel().perform(Action.ACTION_MODEL_CREATED);

            updateModelFromCache(manager.getModel());
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
            if (!typeProvider.isOfType(features)) {
                continue;
            }

            Action typeAction = typeProvider.getAction(features, actionName, params);

            if (typeAction != null) {
                actions.add(typeAction);
            }
        }

        return actions;
    }

    private Action getAction(TypeProvider typeProvider, ModelFeatures features, String actionName, Object... params) {
        Action action = typeProvider.getAction(features, actionName, params);

        if (action != null) {
            return action;
        }

        action = findCachedAction(typeProvider, actionName);

        if (action != null) {
            return action;
        }

        Method method = getActionMethod(typeProvider.getClass(), actionName);

        if (method != null) {
            action = new MethodAction(typeProvider, method);

            cacheAction(typeProvider, actionName, action);

            return action;
        }

        return null;
    }

    private Action findCachedAction(TypeProvider typeProvider, String actionName) {
        Map<String, Action> actionMap = mActionCache.get(typeProvider);

        if (actionMap != null) {
            return actionMap.get(actionName);
        }

        return null;
    }

    private void cacheAction(TypeProvider typeProvider, String actionName, Action action) {
        Map<String, Action> actionMap = mActionCache.get(typeProvider);

        if (actionMap == null) {
            actionMap = new HashMap<>();

            mActionCache.put(typeProvider, actionMap);
        }

        actionMap.put(actionName, action);
    }

    private Method getActionMethod(Class clazz, String actionName) {
        try {
            return clazz.getMethod(actionName, Model.class, Object[].class);
        } catch (NoSuchMethodException e) { }

        if (!clazz.getSuperclass().equals(Object.class)) {
            return getActionMethod(clazz.getSuperclass(), actionName);
        }

        return null;
    }

    protected TargetManager findTargetManager(ModelFeatures modelFeatures, TargetManager exception) {
        List<String> identifyingFeatures = getIdentifyingFeatures(modelFeatures);

        for (ManagerInfo managerInfo: mManagersInfo) {
            if (managerInfo.manager == exception) {
                continue;
            }

            if (managerInfo.modelFeatures.matchesWith(modelFeatures, identifyingFeatures)) {
                return managerInfo.manager;
            }
        }

        return null;
    }

    private List<String> getIdentifyingFeatures(ModelFeatures modelFeatures) {
        List<String> identifyingFeatures = new ArrayList<>();

        for (TypeProvider typeProvider: mTypeProviders) {
            if (!typeProvider.isOfType(modelFeatures)) {
                continue;
            }

            typeProvider.getIdentifyingFeatures(modelFeatures, identifyingFeatures);
        }

        return identifyingFeatures;
    }

    private TargetManager defineManager(ModelFeatures features) {
        TargetManager targetManager = new TargetManager(this, features);

        ManagerInfo managerInfo = new ManagerInfo(features, targetManager);

        mManagersInfo.add(managerInfo);

        return targetManager;
    }

    private void updateModelFromCache(final Model model) {
        if (mModelCache == null) {
            return;
        }

        List<String> identifyingFeatures = getIdentifyingFeatures(model.getFeatures());

        mModelCache.get(model.getFeatures(), identifyingFeatures, new GetModelCallback() {

            @Override
            public void onGetModelResult(ModelFeatures modelFeatures) {
                if (modelFeatures == null) {
                    model.perform(Action.ACTION_MODEL_NOT_EXISTED_IN_CACHE);
                    return;
                }

                model.getFeatures().updateWith(modelFeatures);

                model.perform(Action.ACTION_MODEL_LOADED_FROM_CACHE);

                model.getFeatures().notifyFeaturesChanged(modelFeatures.getAll().keySet().toArray(new String[0]));
            }

        });
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

    protected void cache(ModelFeatures modelFeatures, boolean keep) {
        if (mModelCache == null) {
            return;
        }

        List<String> identifyingFeatures = getIdentifyingFeatures(modelFeatures);

        if (keep) {
            mModelCache.put(modelFeatures, identifyingFeatures);
        }
        else {
            mModelCache.remove(modelFeatures, identifyingFeatures);
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

    private static class MethodAction implements Action {

        private TypeProvider mTypeProvider;
        private Method mMethod;

        MethodAction(TypeProvider typeProvider, Method method) {
            mTypeProvider = typeProvider;
            mMethod = method;
        }

        @Override
        public Object perform(Model model, Object... params) {
            try {
                return mMethod.invoke(mTypeProvider, model, params);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("TypeProvider action method must be public.", e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException("TypeProvider action method threw an exception.", e);
            }
        }

    }

}
