package com.yashoid.mmv.fullsample;

import android.app.Application;

import com.yashoid.mmv.Managers;
import com.yashoid.mmv.fullsample.login.Login;
import com.yashoid.mmv.fullsample.person.PersonList;
import com.yashoid.mmv.fullsample.post.PostList;

public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Managers.bindLifeCycle(this);

        Managers.addTypeProvider(new LoggerType());
        Managers.addTypeProvider(new Login.LoginType());
        Managers.addTypeProvider(new PersonList.PersonListType());
        Managers.addTypeProvider(new PostList.PostListType());


    }

}
