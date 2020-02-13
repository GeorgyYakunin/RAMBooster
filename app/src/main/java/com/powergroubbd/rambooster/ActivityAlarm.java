package com.powergroubbd.rambooster;

import android.app.Application;

/**
 * Created by Osman Goni Nahid on 11/8/2015.
 */
public class ActivityAlarm extends Application {
    public int notificationCount;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationCount = 0;
    }

    public void incrementCount(){
        notificationCount ++;
    }

    public int getNotificationCount(){
        return notificationCount;
    }
}
