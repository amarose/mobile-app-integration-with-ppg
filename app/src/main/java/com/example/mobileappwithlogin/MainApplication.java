package com.example.mobileappwithlogin;


import android.app.Application;

import com.pushpushgo.sdk.PushPushGo;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Instantiate PPG sdk in main app
        PushPushGo.getInstance(this);
    }
}
