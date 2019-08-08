package com.yashoid.mmv.fullsample;

import android.app.Application;

import com.yashoid.mmv.Managers;
import com.yashoid.mmv.fullsample.login.Login;
import com.yashoid.mmv.fullsample.person.Person;
import com.yashoid.mmv.fullsample.person.PersonList;
import com.yashoid.mmv.fullsample.post.Post;
import com.yashoid.mmv.fullsample.post.PostList;
import com.yashoid.office.task.TaskManager;
import com.yashoid.office.task.TaskManagerBuilder;

public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Managers.bindLifeCycle(this);
        Managers.enableCache(this, createTaskManager());

        Managers.addTypeProvider(new LoggerType());
        Managers.addTypeProvider(new Login.LoginType());
        Managers.addTypeProvider(new MainFlow.MainFlowType());
        Managers.addTypeProvider(new PersonList.PersonListType());
        Managers.addTypeProvider(new Person.PersonType());
        Managers.addTypeProvider(new PostList.PostListType());
        Managers.addTypeProvider(new Post.PostType());
    }

    private TaskManager createTaskManager() {
        return new TaskManagerBuilder()
                .addDatabaseReadSection(1)
                .addDatabaseReadWriteSection(1)
                .build();
    }

}
