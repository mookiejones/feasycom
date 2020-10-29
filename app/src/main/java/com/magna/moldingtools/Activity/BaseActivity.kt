package com.magna.moldingtools.Activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.magna.moldingtools.Controler.ActivityCollector

/**
 * Copyright 2017 Shenzhen Feasycom Technology co.,Ltd
 */
abstract class BaseActivity : AppCompatActivity() {
    abstract val tag: String?
    protected fun Log(message: String?) {
        android.util.Log.e(tag, message)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //强制竖屏
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        //干掉 activity 切换特效
//        overridePendingTransition(0, 0);
        super.onCreate(savedInstanceState)
        //        Log.d("activity", getClass().getSimpleName());
//        Log.i("onCreat", getClass().getSimpleName());
        ActivityCollector.addActivity(this)
        //        Log.i("count", ActivityCollector.getCount() + "");
    }




    override fun onDestroy() {
        super.onDestroy()
        //        Log.i("onDestory", getClass().getSimpleName());
        ActivityCollector.removeActivity(this)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.repeatCount == 0) {
            ActivityCollector.finishAllActivity()
        }
        return super.onKeyDown(keyCode, event)
    }

    abstract fun initView()
    abstract fun setClick()

    fun finishActivity() {
//        Log.i("finish",this.getClass().getSimpleName());
        finish()
    }
}