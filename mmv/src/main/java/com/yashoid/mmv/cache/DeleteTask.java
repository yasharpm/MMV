package com.yashoid.mmv.cache;

import com.yashoid.mmv.ModelFeatures;
import com.yashoid.office.AsyncOperation;
import com.yashoid.office.task.TaskManager;

import java.util.HashMap;

public class DeleteTask extends AsyncOperation {

    private final ModelCache mModelCache;

    private ModelDatabase mDatabase;

    private final HashMap<String, String> mQueryFeatures;


    protected DeleteTask(HashMap<String, String> queryFeatures, ModelCache modelCache) {
        super(modelCache.getTaskManager(), TaskManager.MAIN, TaskManager.DATABASE_READWRITE);

        mDatabase = new ModelDatabase(modelCache.getContext());

        mModelCache = modelCache;

        mQueryFeatures = queryFeatures;
    }

    @Override
    protected void doInBackground() {
        mDatabase.deleteModel(mQueryFeatures);
    }

}
