package com.magna.moldingtools.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.OnItemSelected
import com.feasycom.bean.BeaconBean
import com.feasycom.controler.FscBeaconApi
import com.feasycom.controler.FscBeaconApiImp
import com.magna.moldingtools.BeaconView.AltBeaconView
import com.magna.moldingtools.BeaconView.Eddystone_UIDView
import com.magna.moldingtools.BeaconView.Eddystone_URLView
import com.magna.moldingtools.BeaconView.iBeaconView
import com.magna.moldingtools.R
import java.util.*

/**
 * Copyright 2017 Shenzhen Feasycom Technology co.,Ltd
 */
private const val TAG="AddBeaconActivity"


class AddBeaconActivity : BaseActivity() {

    @JvmField
    @BindView(R.id.header_left)
    var headerLeft: TextView? = null

    @JvmField
    @BindView(R.id.headerTitle)
    var headerTitle: TextView? = null

    @JvmField
    @BindView(R.id.headerRight)
    var headerRight: TextView? = null

    @JvmField
    @BindView(R.id.beaconType)
    var beaconType: Spinner? = null

    @JvmField
    @BindView(R.id.setting_parameter_ibeacon)
    var settingParameterIbeacon: iBeaconView? = null

    @JvmField
    @BindView(R.id.setting_parameter_eddystone_uid)
    var settingParameterEddystoneUid: Eddystone_UIDView? = null

    @JvmField
    @BindView(R.id.setting_parameter_eddystone_url)
    var settingParameterEddystoneUrl: Eddystone_URLView? = null

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
    @BindView(R.id.setting_parameter_altbeacon)
    var settingParameterAltbeacon: AltBeaconView? = null
    private var activity: Activity? = null
    private var fscBeaconApi: FscBeaconApi? = null
    private lateinit var beaconTypelist: List<String>
    private var spinnerAdapter: ArrayAdapter<String>? = null
    private var beaconBean: BeaconBean? = null
    override val tag: String=TAG

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_beacon)
        ButterKnife.bind(this)
        activity = this
        fscBeaconApi = FscBeaconApiImp.getInstance()
        beaconTypelist = Arrays.asList(*resources.getStringArray(R.array.beacon_table))
        initView()
        beaconBean = BeaconBean()
        settingParameterEddystoneUrl!!.setBeaconBean(beaconBean)
        settingParameterEddystoneUid!!.setBeaconBean(beaconBean)
        settingParameterIbeacon!!.setBeaconBean(beaconBean)
        settingParameterAltbeacon!!.setBeaconBean(beaconBean)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun initView() {
        spinnerAdapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_dropdown_item, beaconTypelist)
        beaconType!!.adapter = spinnerAdapter
    }

    fun refreshFooter() {
        //footer image src init
        SetButton!!.setImageResource(R.drawable.setting_on)
        AboutButton!!.setImageResource(R.drawable.about_off)
        SearchButton!!.setImageResource(R.drawable.search_off)
    }

    @OnClick(R.id.Set_Button)
    override fun setClick() {
    }

    @OnClick(R.id.About_Button)
    fun aboutClick() {
        fscBeaconApi!!.disconnect()
        AboutActivity.actionStart(activity!!)
        activity!!.finish()
    }

    @OnClick(R.id.Search_Button)
    fun searchClick() {
        fscBeaconApi!!.disconnect()
        MainActivity.actionStart(this)
        activity!!.finish()
    }

    fun sensorClick() {
        fscBeaconApi!!.disconnect()
        SensorActivity.actionStart(this)
        activity!!.finish()
    }

    @OnClick(R.id.header_left)
    fun goBack() {
        activity!!.finish()
        //        activity.overridePendingTransition(0, 0);
    }

    @OnClick(R.id.headerRight)
    fun add() {
        val intent = Intent()
        val bundle = Bundle()
        bundle.putSerializable("beaconBean", beaconBean)
        intent.putExtras(bundle)
        setResult(REQUEST_BEACON_ADD_OK, intent)
        activity!!.finish()
    }

    @OnItemSelected(R.id.beaconType)
    fun beaconSelect(v: View?, id: Int) {
        when (id) {
            0 -> {
                beaconBean!!.beaconType = ""
                settingParameterIbeacon!!.visibility = View.GONE
                settingParameterEddystoneUid!!.visibility = View.GONE
                settingParameterEddystoneUrl!!.visibility = View.GONE
                settingParameterAltbeacon!!.visibility = View.GONE
            }
            1 -> {
                beaconBean!!.beaconType = "UID"
                settingParameterIbeacon!!.visibility = View.GONE
                settingParameterEddystoneUid!!.visibility = View.VISIBLE
                settingParameterEddystoneUrl!!.visibility = View.GONE
                settingParameterAltbeacon!!.visibility = View.GONE
            }
            2 -> {
                beaconBean!!.beaconType = "URL"
                settingParameterIbeacon!!.visibility = View.GONE
                settingParameterEddystoneUid!!.visibility = View.GONE
                settingParameterEddystoneUrl!!.visibility = View.VISIBLE
                settingParameterAltbeacon!!.visibility = View.GONE
            }
            3 -> {
                beaconBean!!.beaconType = "iBeacon"
                settingParameterIbeacon!!.visibility = View.VISIBLE
                settingParameterEddystoneUid!!.visibility = View.GONE
                settingParameterEddystoneUrl!!.visibility = View.GONE
                settingParameterAltbeacon!!.visibility = View.GONE
            }
            4 -> {
                beaconBean!!.beaconType = "AltBeacon"
                settingParameterIbeacon!!.visibility = View.GONE
                settingParameterEddystoneUid!!.visibility = View.GONE
                settingParameterEddystoneUrl!!.visibility = View.GONE
                settingParameterAltbeacon!!.visibility = View.VISIBLE
            }
        }
    }

   companion object{
       val REQUEST_BEACON_ADD_OK = 2
   }
}