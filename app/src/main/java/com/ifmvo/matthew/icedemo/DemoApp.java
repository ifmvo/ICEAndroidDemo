package com.ifmvo.matthew.icedemo;

import android.app.Application;

import com.ifmvo.matthew.icedemo.ice.IceClient;

/**
 * Created by ifmvo on 17-3-17.
 */

public class DemoApp extends Application{


    @Override
    public void onCreate() {
        super.onCreate();
        IceClient.init(getApplicationContext());
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        IceClient.closeIce();
    }
}
