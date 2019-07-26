package com.yashoid.mmv.cache;

import com.yashoid.mmv.ModelFeatures;
import com.yashoid.office.AsyncOperation;
import com.yashoid.office.task.TaskManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QueryTask extends AsyncOperation {

    private final ModelCache mModelCache;

    private ModelDatabase mDatabase;

    private final HashMap<String, String> mQueryFeatures;

    private List<GetModelCallback> mCallbacks = new ArrayList<>();

    private boolean mExecutionFinished = false;

    private HashMap<String, Object> mQueryResult = null;

    private ModelFeatures mModelFeatures = null;

    protected QueryTask(HashMap<String, String> queryFeatures, ModelCache modelCache) {
        super(modelCache.getTaskManager(), TaskManager.MAIN, TaskManager.DATABASE_READ);

        mDatabase = new ModelDatabase(modelCache.getContext());

        mModelCache = modelCache;

        mQueryFeatures = queryFeatures;
    }

    protected HashMap<String, String> getQueryFeatures() {
        return mQueryFeatures;
    }

    protected void addCallback(GetModelCallback callback) {
        if (mExecutionFinished) {
            callback.onGetModelResult(mModelFeatures);
            return;
        }

        mCallbacks.add(callback);
    }

    @Override
    protected void doInBackground() {
        mQueryResult = mDatabase.queryModel(mQueryFeatures);
    }

    @Override
    protected void onPostExecute() {
        if (mQueryResult != null) {
            mModelFeatures = new ModelFeatures.Builder()
                    .addAll(mQueryResult)
                    .build();
        }

        mExecutionFinished = true;

        for (GetModelCallback callback: mCallbacks) {
            callback.onGetModelResult(mModelFeatures);
        }

        mModelCache.onTaskExecuted(this);
    }

}
