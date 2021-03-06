package com.yashoid.mmv.cache;

import com.yashoid.mmv.ModelFeatures;
import com.yashoid.office.AsyncOperation;
import com.yashoid.office.task.TaskManager;

import java.util.HashMap;
import java.util.Map;

public class PutTask extends AsyncOperation {

    private final ModelCache mModelCache;

    private ModelDatabase mDatabase;

    private final HashMap<String, String> mQueryFeatures;
    private final Map<String, Object> mModelFeatures;


    protected PutTask(HashMap<String, String> queryFeatures, ModelFeatures modelFeatures, ModelCache modelCache) {
        super(modelCache.getTaskManager(), TaskManager.MAIN, TaskManager.DATABASE_READWRITE);

        mDatabase = new ModelDatabase(modelCache.getContext());

        mModelCache = modelCache;

        mQueryFeatures = queryFeatures;
        mModelFeatures = new HashMap<>(modelFeatures.getAll());
    }

    @Override
    protected void doInBackground() {
        boolean modelExists = mDatabase.modelExists(mQueryFeatures);

        if (modelExists) {
            mDatabase.updateModel(mQueryFeatures, mModelFeatures);
        }
        else {
            mDatabase.addModel(mQueryFeatures, mModelFeatures);
        }
    }

}
