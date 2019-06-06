package com.yashoid.mmv.sample;

import android.app.Application;

import com.yashoid.mmv.Managers;

public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Managers.getInstance().addTypeProvider(new DefaultTypeProvider());
    }

}
