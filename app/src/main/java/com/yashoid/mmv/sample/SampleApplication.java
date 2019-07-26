package com.yashoid.mmv.sample;

import android.app.Application;

import com.yashoid.mmv.Managers;
import com.yashoid.office.task.TaskManager;
import com.yashoid.office.task.TaskManagerBuilder;

public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Managers.bindLifeCycle(this);
        Managers.enableCache(this, createTaskManager());

        Managers.addTypeProvider(new DefaultTypeProvider());
    }

    private TaskManager createTaskManager() {
        return new TaskManagerBuilder()
                .addDatabaseReadSection(1)
                .addDatabaseReadWriteSection(1)
                .build();
    }

}
