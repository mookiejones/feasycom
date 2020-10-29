package com.magna.moldingtools.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnCheckedChanged
import butterknife.OnClick
import com.feasycom.controler.FscBleCentralApi
import com.feasycom.controler.FscSppApi
import com.magna.moldingtools.R
import com.magna.moldingtools.Utils.SettingConfigUtil
private const val TAG="FilterDeviceActivity"
/**
 * Copyright 2017 Shenzhen Feasycom Technology co.,Ltd
 */
class FilterDeviceActivity : BaseActivity() {
    override val tag: String?=TAG
    @JvmField
    @BindView(R.id.header_left)
    var headerLeft: TextView? = null

    @JvmField
    @BindView(R.id.Search_Button)
    var SearchButton: ImageView? = null

    @JvmField
    @BindView(R.id.Set_Button)
    var SetButton: ImageView? = null

    @JvmField
    @BindView(R.id.About_Button)
    var AboutButton: ImageView? = null

    @JvmField
    @BindView(R.id.filter_switch)
    var filterSwitch: Switch? = null

    @JvmField
    @BindView(R.id.min_rssi_text)
    var minRssiText: TextView? = null

    @JvmField
    @BindView(R.id.rssi_value_text)
    var rssiValueText: TextView? = null
    private val fscBleCentralApi: FscBleCentralApi? = null
    private val fscSppApi: FscSppApi? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_device)

        ButterKnife.bind(this)
    }

    override fun onResume() {
        super.onResume()
        filterSwitch!!.isChecked = SettingConfigUtil.getData(applicationContext, "filter_switch", false) as Boolean
        rssiSeekBar!!.progress = SettingConfigUtil.getData(applicationContext, "filter_value", -100) as Int
    }

    override fun initView() {}
    public override fun onStart() {
        super.onStart()

        rssiSeekBar = findViewById(R.id.rssi_seek_bar)
        rssiSeekBar?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            //监听进度条
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                rssiValueText!!.text = "" + (progress - 100).toString() + " dB"
                filterValue = progress - 100 //让进度条初始值为-100
                SettingConfigUtil.saveData(applicationContext, "filter_value", filterValue + 100)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                val start = seekBar.progress
                rssiValueText!!.text = (start - 100).toString() + " dB"
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                val end = seekBar.progress
                rssiValueText!!.text = (end - 100).toString() + " dB"
            }
        })
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.repeatCount == 0) {
            MainActivity.actionStart(this)
            finishActivity()
            SettingConfigUtil.saveData(applicationContext, "filter_value", filterValue + 100)
        }
        return true
    }

    @OnClick(R.id.header_left)
    fun goBack() {
        MainActivity.actionStart(this)
        finishActivity()
    }

    /*监听过滤开关*/
    @OnCheckedChanged(R.id.filter_switch)
    fun rssiSwitch(v: CompoundButton?, flag: Boolean) {
        if (flag) {
            android.util.Log.i("switch", "rssiSwitch: 1")
            isFilterEnable = true
        } else {
            android.util.Log.i("switch", "rssiSwitch: 0")
            isFilterEnable = false
        }
        SettingConfigUtil.saveData(applicationContext, "filter_switch", flag)
    }

    @OnClick(R.id.Set_Button)
    override fun setClick() {
        SetActivity.actionStart(this)
         finish()
    }

    @OnClick(R.id.Search_Button)
    fun searchClick() {
        MainActivity.actionStart(this)
         finish()
    }

    fun sensorClick() {
        SensorActivity.actionStart(this)
         finish()
    } /*
    @OnClick(R.id.more)
    public void onViewClicked() {
        new AboutUsDialog(activity).show();
    }
    */

    companion object {

        var rssiSeekBar: SeekBar? = null
        var filterValue = -100
        var isFilterEnable = false
        fun actionStart(context: Context) {
            val intent = Intent(context, FilterDeviceActivity::class.java)
            context.startActivity(intent)
        }
    }
}