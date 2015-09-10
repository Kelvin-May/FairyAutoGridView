package com.k;

import android.app.Application;

/**
 * Created by Kelvin on 15/9/8.
 */
public class AppApplication extends Application {

    public static Application application;

    @Override
    public void onCreate() {
        application = this;
        super.onCreate();
    }
}
