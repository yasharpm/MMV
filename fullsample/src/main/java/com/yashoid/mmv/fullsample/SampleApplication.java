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

        Managers managers = Managers.getInstance();

        managers.addTypeProvider(new LoggerType());
        managers.addTypeProvider(new Login.LoginType());
        managers.addTypeProvider(new PersonList.PersonListType());
        managers.addTypeProvider(new PostList.PostListType());
    }

}
