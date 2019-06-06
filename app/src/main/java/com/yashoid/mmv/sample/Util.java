package com.yashoid.mmv.sample;

import android.content.Context;

import com.yashoid.mmv.Managers;

public class Util {

    public static Managers getManagers(Context context) {
        return ((SampleApplication) context.getApplicationContext()).getManagers();
    }

}
