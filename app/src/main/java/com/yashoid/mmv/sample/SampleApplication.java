package com.yashoid.mmv.sample;

import android.app.Application;

import com.yashoid.mmv.Managers;

public class SampleApplication extends Application {

    private Managers mManagers;

    @Override
    public void onCreate() {
        super.onCreate();

        mManagers = new Managers();

        mManagers.addTypeProvider(new DefaultTypeProvider());
    }

    public Managers getManagers() {
        return mManagers;
    }

}
