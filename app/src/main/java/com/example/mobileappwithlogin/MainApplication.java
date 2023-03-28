package com.example.mobileappwithlogin;


import android.app.Application;
import android.util.Log;

import com.pushpushgo.sdk.PushPushGo;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("TEST", "initialize");
        // Instantiate PPG sdk in main app
        PushPushGo.getInstance(this);
    }
}
