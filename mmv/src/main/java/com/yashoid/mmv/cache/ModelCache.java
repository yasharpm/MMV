package com.yashoid.mmv.cache;

import android.content.Context;

import com.yashoid.mmv.ModelFeatures;
import com.yashoid.office.task.TaskManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ModelCache {

    private final Context mContext;
    private final TaskManager mTaskManager;

    private final List<QueryTask> mQueryTasks = new ArrayList<>();

    public ModelCache(Context context, TaskManager taskManager) {
        mContext = context;
        mTaskManager = taskManager;
    }

    protected Context getContext() {
        return mContext;
    }

    protected TaskManager getTaskManager() {
        return mTaskManager;
    }

    protected void onTaskExecuted(QueryTask task) {
        mQueryTasks.remove(task);
    }

    public void get(ModelFeatures features, List<String> identifyingFeatures, GetModelCallback callback) {
        HashMap<String, String> queryFeatures = takeQueryFeatures(features, identifyingFeatures);

        QueryTask task = prepareQueryTask(queryFeatures);

        task.addCallback(callback);
    }

    public void put(ModelFeatures features, List<String> identifyingFeatures) {
        new PutTask(takeQueryFeatures(features, identifyingFeatures), features, this).execute();
    }

    public void remove(ModelFeatures features, List<String> identifyingFeatures) {
        new DeleteTask(takeQueryFeatures(features, identifyingFeatures), this).execute();
    }

    private QueryTask prepareQueryTask(HashMap<String, String> queryFeatures) {
        QueryTask task = findQueryTask(queryFeatures);

        if (task == null) {
            task = new QueryTask(queryFeatures, this);

            mQueryTasks.add(task);

            task.execute();
        }

        return task;
    }

    private QueryTask findQueryTask(HashMap<String, String> queryFeatures) {
        for (QueryTask task: mQueryTasks) {
            if (task.getQueryFeatures().equals(queryFeatures)) {
                return task;
            }
        }

        return null;
    }

    private HashMap<String, String> takeQueryFeatures(ModelFeatures features, List<String> identifyingFeatures) {
        HashMap<String, String> queryFeatures = new HashMap<>();

        for (String featureName: identifyingFeatures) {
            queryFeatures.put(featureName, String.valueOf(features.get(featureName)));
        }

        return queryFeatures;
    }

}
