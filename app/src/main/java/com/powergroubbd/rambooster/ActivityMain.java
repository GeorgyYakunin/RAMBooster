package com.powergroubbd.rambooster;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.skyfishjy.library.RippleBackground;

import java.util.Calendar;

public class ActivityMain extends AppCompatActivity {
    private Toolbar mToolbar;
    SharedPreferences sharedpreferences;
    RippleBackground rippleBackground;
    public AlarmManager alarmManager;

    Intent alarmIntent;
    PendingIntent pendingIntent;

    TextView txtTotalMemory, txtUsedMemory, txtFreeMemory, tvTotal, tvUsed, tvFree, txtMemoryStatus;
    ImageView stateImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        rippleBackground = (RippleBackground) findViewById(R.id.content);
        Button boostButton = (Button) findViewById(R.id.button);
        initialize();
        updatePref("not clean");
        boostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityMain.this, ActivityProcess.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });
    }

    private void initialize() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "RobotoThin.ttf");

        tvTotal = (TextView) findViewById(R.id.total_memory);
        tvUsed = (TextView) findViewById(R.id.used_memory);
        tvFree = (TextView) findViewById(R.id.free_memory);

        txtMemoryStatus = (TextView) findViewById(R.id.memory_status);
        tvTotal.setTypeface(font);
        tvUsed.setTypeface(font);
        tvFree.setTypeface(font);

        stateImage = (ImageView) findViewById(R.id.stateImage);
        setAlarm();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updatePref("not clean");
    }


    @Override
    protected void onStop() {
        super.onStop();
        //updatePref("clean");
    }

    private void updateMemoryStatus() {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        long freeMemory = mi.availMem / 1048576L;
        long totalMemory = mi.totalMem / 1048576L;
        long usedMemory = totalMemory - freeMemory;
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Util.MyPREFERENCES, 0);
        String status = sharedPreferences.getString(Util.KEY_STATUS, null);
        if (status.equals("clean")) {

            tvFree.setText(freeMemory + " MB");
            tvTotal.setText(totalMemory + " MB");
            tvUsed.setText(usedMemory + " MB");
            stateImage.setImageResource(R.drawable.greenstate);
            rippleBackground.stopRippleAnimation();

        } else {
            rippleBackground.startRippleAnimation();
            tvFree.setText(freeMemory - 40L + " MB");
            tvTotal.setText(totalMemory + " MB");
            tvUsed.setText(usedMemory + 40L + " MB");
            stateImage.setImageResource(R.drawable.redstate);
        }

    }

    private void updatePref(String status) {
        sharedpreferences = getSharedPreferences(Util.MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Util.KEY_STATUS, status);
        editor.commit();

        updateMemoryStatus();

    }


    private int getInterval() {
        int seconds = 60;
        int milliseconds = 1000;
        int repeatMS = seconds * 2 * milliseconds;
        return repeatMS;
    }

    public void setAlarm() {
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmIntent = new Intent(ActivityMain.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(ActivityMain.this, 0, alarmIntent, 0);

        Calendar alarmStartTime = Calendar.getInstance();
        alarmStartTime.add(Calendar.HOUR, 1);
        alarmManager.setRepeating(AlarmManager.RTC, alarmStartTime.getTimeInMillis(), getInterval(), pendingIntent);
        Log.i("Booster Notification", "Alarms set every one minutes.");

    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.i("Booster Alarm Intent", "onNewIntent(), intent = " + intent);
        if (intent.getExtras() != null) {
        }
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        countNotification();
    }

    public void countNotification() {
        ActivityAlarm app = (ActivityAlarm) getApplicationContext();
        int mNotificationCount = app.getNotificationCount();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.getIntent().getExtras() != null) {
            Log.i("POWER BOOSTER", "extras: " + this.getIntent().getExtras());
            countNotification();

        }
    }
}
