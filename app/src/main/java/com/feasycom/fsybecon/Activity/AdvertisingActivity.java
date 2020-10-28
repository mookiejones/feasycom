package com.feasycom.fsybecon.Activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.feasycom.fsybecon.R;

/**
 * Copyright 2017 Shenzhen Feasycom Technology co.,Ltd
 */

public class AdvertisingActivity extends BaseActivity {
    public static final String TAG = "AdvertisingActivity";
    private Handler handler = new Handler();
    private Activity activity;

    @Override
    protected String getTag() {
        return TAG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertising);


        activity = this;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isFirstIn()) {
                    GuideActivity.actionStart(activity);
                } else {
                    MainActivity.actionStart(activity);
                }
                activity.finish();
            }
        }, 2500);
    }

    private boolean isFirstIn() {
        SharedPreferences sf = getSharedPreferences("data", MODE_PRIVATE);
        boolean isFirstIn = sf.getBoolean("isFirstIn", true);
        SharedPreferences.Editor editor = sf.edit();
        if (isFirstIn) {
            editor.putBoolean("isFirstIn", false);
            editor.commit();
            return true;
        } else {
            editor.commit();
            return false;
        }
    }

    @Override
    public void refreshFooter() {

    }

    @Override
    public void refreshHeader() {

    }

    @Override
    public void initView() {

    }

    @Override
    public void setClick() {

    }

    @Override
    public void aboutClick() {

    }


    @Override
    public void searchClick() {

    }

    @Override
    public void sensorClick() {

    }

}
