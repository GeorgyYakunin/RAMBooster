package com.powergroubbd.rambooster;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.skyfishjy.library.RippleBackground;

/**
 * Created by Osman Goni Nahid on 11/4/2015.
 */
public class ActivityAfterClean extends AppCompatActivity {
    TextView txtTotal, txtFree, txtUsed, txtMemoryStatus;
    Toolbar mToolbar;
    boolean shouldShowAdd = false;
//    InterstitialAd fullScreenAdd;
//    AdView adView;
//    AdRequest fullScreenAdRequest,bannerAdRequest;

    RippleBackground rippleBackground;
    Button boostButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_after_clean);
        initialize();
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "RobotoThin.ttf");
        setSupportActionBar(mToolbar);
        boostButton = (Button) findViewById(R.id.button);
        boostButton.setEnabled(false);
        getSupportActionBar().setTitle("  Completed!!");
        txtTotal = (TextView) findViewById(R.id.total_memory);
        rippleBackground = (RippleBackground) findViewById(R.id.content);
        txtFree = (TextView) findViewById(R.id.free_memory);
        txtUsed = (TextView) findViewById(R.id.used_memory);
//        adView=(AdView)findViewById(R.id.adView);
        txtMemoryStatus = (TextView) findViewById(R.id.memory_status);
        txtTotal.setTypeface(font);
        txtFree.setTypeface(font);
        txtUsed.setTypeface(font);
        int freespace = getIntent().getIntExtra("freespace", 0);
        int totalspace = getIntent().getIntExtra("totalspace", 0);
        int processNo = getIntent().getIntExtra("processno", 0);
        rippleBackground.startRippleAnimation();
        int usedspace = totalspace - freespace;
        txtTotal.setText(totalspace + " MB");
        txtUsed.setText(usedspace + " MB");
        txtFree.setText(freespace + " MB");
        Toast.makeText(getApplicationContext(), "Harmful Process are cleaned!!", Toast.LENGTH_SHORT).show();
        boostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityAfterClean.this, ActivityProcess.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

//        enableAdd();
        new CTimer(10000, 1000).start();
    }

    private void initialize() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
    }

//    private void enableAdd() {
//
//
//
//        fullScreenAdd = new InterstitialAd(ActivityAfterClean.this);
//        fullScreenAdd.setAdUnitId(getResources().getString(R.string.fullscreen_ad_uint_id));
//        fullScreenAdRequest = new AdRequest.Builder().build();
//        fullScreenAdd.loadAd(fullScreenAdRequest);
//
//        fullScreenAdd.setAdListener(new AdListener() {
//
//            @Override
//            public void onAdLoaded() {
//
//                Log.i("FullScreenAdd", "Loaded successfully");
//                // fullScreenAdd.show();
//                shouldShowAdd = true;
//
//            }
//
//            @Override
//            public void onAdFailedToLoad(int errorCode) {
//                Log.i("FullScreenAdd", "failed to Load");
//                shouldShowAdd = false;
//            }
//
//            @Override
//            public void onAdClosed() {
//                super.onAdClosed();
//
//
//            }
//
//
//        });
//    }

//    private void enableBannerAd(){
//        bannerAdRequest = new AdRequest.Builder().build();
//        adView.loadAd(bannerAdRequest);
//    }


    private class CTimer extends CountDownTimer {


        public CTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {


            boostButton.setText("Please wait..(" + (int) (millisUntilFinished / 1000) + ")");
            if (millisUntilFinished <= 5000) {

                if (shouldShowAdd) {


//                    fullScreenAdd.show();
                    shouldShowAdd = false;
                }

            }

        }

        @Override
        public void onFinish() {
            boostButton.setText("Boost");
            boostButton.setEnabled(true);

        }
    }
}
