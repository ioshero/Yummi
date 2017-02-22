package com.almabranding.yummi.utils;


import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;

import com.almabranding.yummi.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by ioshero on 10/05/16.
 */
public class CounterClass  extends CountDownTimer {
    TextView tvTime;

    public CounterClass(long millisInFuture, long countDownInterval, TextView time) {
        super(millisInFuture, countDownInterval);
        tvTime = time;
    }


    MainActivity activity;

    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onFinish() {
        try {
            if (activity != null)
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //az ido veglegesitese
                            if (tvTime != null)
                                tvTime.setText("00:00");

                        } catch (Exception e) {
                            Log.e("ERROR", e.toString());
                        }
                        //ha eltunik a pop up visszaterunk a menube

                    }
                });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    boolean ticking = false;

    @Override
    public void onTick(final long millisUntilFinished) {
        if (tvTime != null) {
//            long millis = millisUntilFinished;
//            String hms = String.format("%02d:%02d:%02d",
//                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
//                    Math.round(Math.ceil(millis/1000f)));
            tvTime.setText(""+String.format("%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
            ));
        }

    }
}

