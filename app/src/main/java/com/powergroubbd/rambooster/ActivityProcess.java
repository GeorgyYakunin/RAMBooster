package com.powergroubbd.rambooster;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;
import com.ram.speed.booster.RAMBooster;
import com.ram.speed.booster.interfaces.CleanListener;
import com.ram.speed.booster.interfaces.ScanListener;
import com.ram.speed.booster.utils.ProcessInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by Osman Goni Nahid on 11/1/2015.
 */
public class ActivityProcess extends AppCompatActivity {
    private RAMBooster booster;
    ArrayList<String> apps;
    private String TAG = "Booster.Test";
    SharedPreferences sharedpreferences;
    int total, available;
    private AnimatedCircleLoadingView animatedCircleLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_process);
        animatedCircleLoadingView = (AnimatedCircleLoadingView) findViewById(R.id.circle_loading_view);
        startLoading();

        if (booster == null)
            booster = null;
        booster = new RAMBooster(ActivityProcess.this);
        booster.setDebug(true);
        scanMemory();
        cleanMemory();
        startPercentMockThread();
    }

    private void startLoading() {
        animatedCircleLoadingView.startDeterminate();
    }

    private void startPercentMockThread() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                    for (int i = 0; i <= 100; i++) {
                        Thread.sleep(65);
                        changePercent(i);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    sharedpreferences = getSharedPreferences(Util.MyPREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(Util.KEY_STATUS, "clean");
                    editor.commit();
                    waiting();
//                    Intent intent = new Intent(ActivityProcess.this, ActivityAfterClean.class);
//                    intent.putExtra("totalspace",total);
//                    intent.putExtra("freespace",available);
//                    intent.putExtra("processno",apps.size());
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                    finish();

                }
            }
        };
        new Thread(runnable).start();
    }

    private void changePercent(final int percent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                animatedCircleLoadingView.setPercent(percent);
            }
        });
    }

    private void cleanMemory() {
        booster.setCleanListener(new CleanListener() {
            @Override
            public void onStarted() {
                Log.d(TAG, "Clean started");
            }


            @Override
            public void onFinished(long availableRam, long totalRam) {

                Log.d(TAG, String.format(Locale.US,
                        "Clean finished, available RAM: %dMB, total RAM: %dMB",
                        availableRam, totalRam));
                booster = null;

            }
        });
        booster.startScan(true);

    }

    private void waiting() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Log.d(TAG, "Memory : " + total + " " + available);
                    Intent intent = new Intent(ActivityProcess.this, ActivityAfterClean.class);
                    intent.putExtra("totalspace", total);
                    intent.putExtra("freespace", available);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                }
            }
        };
        new Thread(runnable).start();
    }

    private void scanMemory() {
        booster.setScanListener(new ScanListener() {
            @Override
            public void onStarted() {
                Log.d(TAG, "Scan started");
            }

            @Override
            public void onFinished(long availableRam, long totalRam, List<ProcessInfo> appsToClean) {

                Log.d(TAG, String.format(Locale.US,
                        "Scan finished, available RAM: %dMB, total RAM: %dMB",
                        availableRam, totalRam));
                total = (int) totalRam;
                available = (int) availableRam;
                apps = new ArrayList<String>();
                for (ProcessInfo info : appsToClean) {
                    apps.add(info.getProcessName());
                }
                Log.d(TAG, String.format(Locale.US,
                        "Going to clean founded processes: %s", Arrays.toString(apps.toArray())));
                booster.startClean();
            }
        });

    }
}
