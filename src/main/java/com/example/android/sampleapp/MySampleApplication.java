package com.example.android.sampleapp;

import android.app.Application;
import roboguice.RoboGuice;

public class MySampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        RoboGuice.setBaseApplicationInjector(this, RoboGuice.DEFAULT_STAGE,
                RoboGuice.newDefaultRoboModule(this), new ApplicationModule());
    }
}
