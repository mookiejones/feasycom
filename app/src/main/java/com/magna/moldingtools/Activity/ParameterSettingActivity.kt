package com.magna.moldingtools.Activity

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.*
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.feasycom.bean.BeaconBean
import com.feasycom.bean.BluetoothDeviceWrapper
import com.feasycom.bean.FeasyBeacon
import com.feasycom.controler.FscBeaconApi
import com.feasycom.controler.FscBeaconApiImp
import com.feasycom.util.FeasyBeaconUtil
import com.magna.moldingtools.Adapter.SettingBeaconParameterListAdapter
import com.magna.moldingtools.BeaconView.*
import com.magna.moldingtools.Bean.BaseEvent
import com.magna.moldingtools.Controler.FscBeaconCallbacksImpParameter
import com.magna.moldingtools.R
import com.magna.moldingtools.Utils.ViewUtil.*
import com.magna.moldingtools.Widget.InfoDialog
import com.magna.moldingtools.Widget.OTADetermineDialog
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.lang.ref.WeakReference
import java.util.*
private const val TAG="ParameterSettingActivity"
private const val PIN_VALUE = "000000"
/**
 * Copyright 2017 Shenzhen Feasycom Technology co.,Ltd
 */
class ParameterSettingActivity : BaseActivity() {
    override val tag: String?=TAG
    // do not  injected here otherwise listViewHeader can not use annotation injection
    var parameterlistview: ListView? = null
    var listViewHeader: View? = null
    var listViewFooter: View? = null

    @BindView(R.id.TxPower)
    var txPower: LableSpinnerView? = null

    @JvmField
    @BindView(R.id.TxPowerDivider)
    var TxPowerDivider: View? = null

    @BindView(R.id.adv_interval)
    var advInterval: IntervalSpinnerView? = null

    @JvmField
    @BindView(R.id.adv_gsensor)
    var adv_gsensor: GsensorSpinnerView? = null

    @JvmField
    @BindView(R.id.adv_keycfg)
    var adv_keycfg: KeycfgSpinnerView? = null

    // @BindView(R.id.AdvIntervalDivider)
    //View AdvIntervalDivider;
    @JvmField
    @BindView(R.id.ConnectableDivider)
    var ConnectableDivider: View? = null

    @BindView(R.id.PIN)
    var pIN: LableEditView? = null

    @JvmField
    @BindView(R.id.PIN_divider)
    var PINDivider: View? = null

    @JvmField
    @BindView(R.id.add_LL)
    var addLL: LinearLayout? = null

    @JvmField
    @BindView(R.id.header_left)
    var headerLeft: TextView? = null

    @JvmField
    @BindView(R.id.headerTitle)
    var headerTitle: TextView? = null

    @JvmField
    @BindView(R.id.headerRight)
    var headerRight: TextView? = null

    /*
    @BindView(R.id.Search_Button)
    ImageView SearchButton;
    @BindView(R.id.Set_Button)
    ImageView SetButton;
    @BindView(R.id.About_Button)
    ImageView AboutButton;
    */
    @BindView(R.id.Module)
    var module: LableEditView? = null

    @BindView(R.id.Version)
    var version: LableEditView? = null

    @BindView(R.id.Name)
    var name: LableEditView? = null

    @BindView(R.id.Interval)
    var interval: LableEditView? = null

    @JvmField
    @BindView(R.id.Gsensor)
    var Gsensor: LableEditView? = null

    @JvmField
    @BindView(R.id.Keycfg)
    var Keycfg: LableEditView? = null

    @BindView(R.id.Connectable)
    var connectable: LableButtonView? = null

    @BindView(R.id.ExtEnd)
    var extEnd: LableEditView? = null
    var connectDialog: InfoDialog? = null
    var otaDetermineDialog: OTADetermineDialog? = null

    //for BLE password
    var pin2Connect: String? = null
    lateinit var fscBeaconApi: FscBeaconApi
        private set
    private var device: BluetoothDeviceWrapper? = null
    val handler = Handler()
    var adapter: SettingBeaconParameterListAdapter? = null
        private set

    var checkConnect = Runnable {
        Log("run")
        if (!fscBeaconApi!!.isConnected && ("connecting..." == connectDialog!!.info
                        || "check password..." == connectDialog!!.info)) {
            fscBeaconApi!!.setCallbacks(null)
            connectDialog!!.info = "timeout"
            connectFailedHandler()
        }
    }
    private var moduleString: String? = null
    var encryptWay: String? = null
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG, "onCreate")
        back = false
        setContentView(R.layout.activity_parameter_configuration)

        EventBus.getDefault().register(this)
        device = intent.getSerializableExtra("device") as BluetoothDeviceWrapper
        pin2Connect = intent.getSerializableExtra("pin") as String
        connectDialog = InfoDialog(this, "connecting...")
        otaDetermineDialog = OTADetermineDialog(this)
        parameterlistview = findViewById(R.id.parameter_listview)
        adapter = SettingBeaconParameterListAdapter(this, layoutInflater, device!!.feasyBeacon)
        adapterInit(adapter!!)
        listViewHeader = layoutInflater.inflate(R.layout.setting_parameter_header, null, false)
        listViewFooter = layoutInflater.inflate(R.layout.setting_parameter_footer, null, false)
        parameterlistview?.addHeaderView(listViewHeader)
        parameterlistview?.addFooterView(listViewFooter, null, false)
        /**
         * inject here or listViewHeader can not use annotation injection
         */
        ButterKnife.bind(this)


/*
        txPowerlist = Arrays.asList(getResources().getStringArray(R.array.txpower_table));
        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, txPowerlist);
        TxPower.spinnerInit(spinnerAdapter, txPowerlist);
*/if (null != device!!.feasyBeacon) {
            encryptWay = device!!.feasyBeacon.encryptionWay
            moduleString = device!!.feasyBeacon.getmodule()
            val fb = device!!.feasyBeacon
            fb.keycfg = false
            fb.gsensor = false
            fb.buzzer = false
            fb.led = false
            val advin = Arrays.asList(*resources.getStringArray(R.array.advin))
            val duration = Arrays.asList(*resources.getStringArray(R.array.duration))
            val advIntervalList: List<String>
            advIntervalList = if (device!!.feasyBeacon.gsensor || device!!.feasyBeacon.keycfg) {
                Arrays.asList(*resources.getStringArray(R.array.interval_table_1))
            } else {
                Arrays.asList(*resources.getStringArray(R.array.interval_table))
            }
            var intervalSpinnerAdapter: ArrayAdapter<String>? = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, advIntervalList)
            advInterval!!.spinnerInit(intervalSpinnerAdapter, advIntervalList, device!!.feasyBeacon)
            if (device!!.feasyBeacon.keycfg) {
                adv_keycfg!!.visibility = View.VISIBLE
                val keycfgAdvinSpinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, advin)
                val keycfgDurationSpinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, duration)
                adv_keycfg!!.spinnerAdvin(keycfgAdvinSpinnerAdapter, advin)
                adv_keycfg!!.spinnerDuration(keycfgDurationSpinnerAdapter, duration)
            }
            if (device!!.feasyBeacon.gsensor) {
                adv_gsensor!!.visibility = View.VISIBLE
                val gsensorAdvinSpinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, advin)
                val gsensorDurationSpinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, duration)
                adv_gsensor!!.spinnerAdvin(gsensorAdvinSpinnerAdapter, advin)
                adv_gsensor!!.spinnerDuration(gsensorDurationSpinnerAdapter, duration)
            }
            /**
             * We will be compatible with many modules by moduleString and versionString
             * firmware version
             */
            val versionString = device!!.feasyBeacon.version

            /**
             * TxPower bind with BP103 BP104 BP106 BP101 BP671
             */
            val txPowerlist: List<String>
            when (moduleString) {
                "26", "27", "28", "29" -> {
                    isModule_BP109 = false
                    isModule_BP101 = false
                    isModule_BP671 = false
                    txPower!!.visibility = View.VISIBLE
                    TxPowerDivider!!.visibility = View.VISIBLE
                    txPowerlist = Arrays.asList(*resources.getStringArray(R.array.txpower_table))
                    intervalSpinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, txPowerlist)
                    txPower!!.spinnerInit(intervalSpinnerAdapter, txPowerlist)
                }
                "25" -> {
                    isModule_BP109 = true
                    isModule_BP101 = false
                    isModule_BP671 = false
                    txPower!!.visibility = View.GONE
                    TxPowerDivider!!.visibility = View.GONE
                }
                "30" -> {
                    isModule_BP109 = false
                    isModule_BP671 = false
                    isModule_BP101 = true
                    txPower!!.visibility = View.VISIBLE
                    TxPowerDivider!!.visibility = View.VISIBLE
                    txPowerlist = Arrays.asList(*resources.getStringArray(R.array.BP101_txpower_table))
                    intervalSpinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, txPowerlist)
                    txPower!!.spinnerInit(intervalSpinnerAdapter, txPowerlist)
                }
                "31" -> {
                    isModule_BP109 = false
                    isModule_BP671 = true
                    isModule_BP101 = false
                    txPower!!.visibility = View.VISIBLE
                    TxPowerDivider!!.visibility = View.VISIBLE
                    txPowerlist = Arrays.asList(*resources.getStringArray(R.array.BP671_txpower_table))
                    intervalSpinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, txPowerlist)
                    txPower!!.spinnerInit(intervalSpinnerAdapter, txPowerlist)
                }
                "unknow" -> connectable!!.setCheck(true)
                else -> if (versionString.length != 3) {
                    connectable!!.setCheck(true)
                } else {
                    /**
                     * connectable button does not appear if BLE password is used
                     */
                    if (FeasyBeacon.BLE_KEY_WAY == encryptWay!!.substring(1)) {
                        connectable!!.visibility = View.GONE
                        ConnectableDivider!!.visibility = View.GONE
                    } else {
                        connectable!!.visibility = View.VISIBLE
                        ConnectableDivider!!.visibility = View.VISIBLE
                    }
                    connectable!!.setCheck(device!!.feasyBeacon.isConnectable)
                }
            }
            if (FeasyBeacon.BLE_KEY_WAY == encryptWay!!.substring(1)) {
                pIN!!.visibility = View.VISIBLE
                PINDivider!!.visibility = View.VISIBLE
            } else {
                pIN!!.visibility = View.GONE
                PINDivider!!.visibility = View.GONE
            }
        } else {
            // Log.e("ParameterSetting", "为null");
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume")
        fscBeaconApi = FscBeaconApiImp.getInstance(this)
        fscBeaconApi.initialize()
        name!!.setTextWacher(NameTextWatcher(name, fscBeaconApi))
        interval!!.setTextWacher(IntervalTextWatcher(interval, fscBeaconApi))
        pIN!!.setTextWacher(PinTextWatcher(pIN, fscBeaconApi))
        extEnd!!.setTextWacher(ExtendTextWatcher(extEnd, fscBeaconApi))
        Gsensor!!.setTextWacher(GsensorTextWatcher(Gsensor, fscBeaconApi))
        Keycfg!!.setTextWacher(KeyTextWatcher(Keycfg, fscBeaconApi))
        initView()
        if (SetActivity.OPEN_TEST_MODE) {
            connectAndGetInformation()
        } else {
            if (!fscBeaconApi.isConnected()) {
                if (null == device!!.feasyBeacon
                        || FeasyBeaconUtil.updateDetermine(device!!.feasyBeacon.version, device!!.feasyBeacon.getmodule())) {
                    otaDetermineDialog!!.show()
                } else {
                    connectAndGetInformation()
                }
            }
        }
    }

    override fun onDestroy() {
        Log.e(TAG, "onDestroy")
        EventBus.getDefault().unregister(this)
        fscBeaconApi!!.disconnect()
        handler.removeCallbacks(checkConnect)
        super.onDestroy()
    }

    override fun initView() {
        Log.e(TAG, "initView")
        parameterlistview!!.adapter = adapter
        connectable!!.setOnToggleChanged { on: Boolean ->
            Log.e(TAG, "Setting Connectable to $on")
            fscBeaconApi!!.setConnectable(on)
        }
        fscBeaconApi!!.setCallbacks(FscBeaconCallbacksImpParameter(
                WeakReference(this as ParameterSettingActivity?), fscBeaconApi, moduleString, device!!.feasyBeacon))
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        Log("onKeyDown")
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.repeatCount == 0) {
            fscBeaconApi!!.disconnect()
            SetActivity.actionStart(this)
            finishActivity()
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log("onActivityResult")
        if (resultCode == AddBeaconActivity.REQUEST_BEACON_ADD_OK) {
            val beaconBean = data!!.getSerializableExtra("beaconBean") as BeaconBean
            fscBeaconApi!!.addBeaconInfo(beaconBean)
            if (fscBeaconApi!!.isBeaconInfoFull) {
                addBeaconEnable(false)
            } else {
                addBeaconEnable(true)
            }
            runOnUiThread { adapter!!.notifyDataSetChanged() }
        }
    }

    @Subscribe
    fun onEventMainThread(event: BaseEvent) {
        Log.e(TAG, "onEventMainThread")
        when (event.eventId) {
            BaseEvent.DELE_BEACON_EVENT -> {
                fscBeaconApi!!.deleteBeaconInfo(event.index)
                adapter!!.notifyDataSetChanged()
                addBeaconEnable(true)
            }
            BaseEvent.OTA_EVENT_YES -> {
                UpgradeActivity.actionStart(this, device, pin2Connect)
                finishActivity()
            }
            BaseEvent.OTA_EVENT_NO -> connectAndGetInformation()
        }
    }

    @OnClick(R.id.add_IV)
    fun addBeacon() {
        Log("addBeacon")
        val intent = Intent(this, AddBeaconActivity::class.java)
        startActivityForResult(intent, REQUEST_BEACON_ADD)
    }

    //@OnClick(R.id.Set_Button)
    override fun setClick() {
        Log("setClick")
    }

    //@OnClick(R.id.About_Button)
    fun aboutClick() {
        Log("aboutClick")
        fscBeaconApi!!.disconnect()
        AboutActivity.actionStart(this!!)
        finishActivity()
    }

    //@OnClick(R.id.Search_Button)
    fun searchClick() {
        Log("searchClick")
        fscBeaconApi!!.disconnect()
        MainActivity.actionStart(this!!)
        finishActivity()
    }

    fun sensorClick() {
        Log("sensorClick")
        fscBeaconApi!!.disconnect()
        SensorActivity.actionStart(this)
        finishActivity()
    }

    @OnClick(R.id.header_left)
    fun goBack() {
        Log("goBack")
        back = true
        fscBeaconApi!!.disconnect()
        /* Log.e(TAG, "goBack: " );
        SetActivity.actionStart(this);
        finishActivity();*/
    }

    @OnClick(R.id.headerRight)
    fun save() {
        Log("save")
        if (!IntervalSpinnerView.verify) {
            //添加取消
            val alertDialog2 = AlertDialog.Builder(this)
                    .setTitle("ERROR")
                    .setMessage("Interval,Gsonser and Key cannot be \"Zero\" at the same time")
                    .setIcon(R.mipmap.ic_launcher)
                    .setNegativeButton(" cancel") { dialog: DialogInterface?, which: Int -> }
                    .create()
            alertDialog2.show()
        } else if (!(KeycfgSpinnerView.keycfgSend || GsensorSpinnerView.gsensorSend) && IntervalSpinnerView.position > 11) {
            //添加取消
            val alertDialog2 = AlertDialog.Builder(this)
                    .setTitle("ERROR")
                    .setMessage("Interval ,G-Sensor and Key , at least one value :0<X≤2\n")
                    .setIcon(R.mipmap.ic_launcher)
                    .setNegativeButton(" cancel") { dialog: DialogInterface?, which: Int -> }
                    .create()
            alertDialog2.show()
        } else {
            if (fscBeaconApi!!.isConnected) {
                connectDialog!!.info = "save..."
                connectDialog!!.show()
                fscBeaconApi!!.saveBeaconInfo()
            } else {
                return
            }
        }
    }

    fun connectFailedHandler() {
        Log("connectFailedHandler")
        if (back) {
            SetActivity.actionStart(this)
            finishActivity()
        } else {
            handler.postDelayed({
                connectDialog!!.dismiss()
                fscBeaconApi!!.disconnect()
                Log.e(TAG, "保存后的页面跳转 ")
                SetActivity.actionStart(this)
                finishActivity()
            }, InfoDialog.INFO_DIAOLOG_SHOW_TIME.toLong())
        }
    }

    private fun connectAndGetInformation() {
        Log("connectAndGetInformation")
        connectDialog!!.show()
        val CHECK_CONNECT_TIME = 50000
        handler.postDelayed(checkConnect, CHECK_CONNECT_TIME.toLong())
        fscBeaconApi!!.connect(device, pin2Connect)
        TOTAL_COUNT++
    }

    // enable to add beacon button
    fun addBeaconEnable(enable: Boolean) {
        Log("addBeaconEnable")
        if (enable) {
            runOnUiThread { addLL!!.visibility = View.VISIBLE }
        } else {
            runOnUiThread { addLL!!.visibility = View.GONE }
        }
    }

    /**
     * initialize 10 broadcast messages
     *
     * @param adapter
     */
    private fun adapterInit(adapter: Adapter) {
        Log("adapterInit")
        val mAdapter = adapter as SettingBeaconParameterListAdapter
        for (i in 0 until FscBeaconApi.BEACON_AMOUNT) {
            mAdapter.addBeacon(BeaconBean(Integer.valueOf(i + 1).toString(), FeasyBeacon.BEACON_TYPE_NULL))
        }
    }

    fun getGsensor(): GsensorSpinnerView? {
        return adv_gsensor
    }

    fun getKeycfg(): KeycfgSpinnerView? {
        return adv_keycfg
    }

    companion object {

        const val REQUEST_BEACON_ADD = 1

        var firstEnter = true
        var TOTAL_COUNT = 0
        @JvmField
        var SUCESSFUL_COUNT = 0
        @JvmField
        var isModule_BP109 = false
        @JvmField
        var isModule_BP101 = false
        @JvmField
        var isModule_BP671 = false
        var back = false
        @JvmStatic
        fun actionStart(context: Context, device: BluetoothDeviceWrapper?, pin: String?) {
            Log.e(TAG, "Starting ParameterSettingActivity")
            val intent = Intent(context, ParameterSettingActivity::class.java)
            val mBundle = Bundle()
            mBundle.putSerializable("device", device)
            mBundle.putSerializable("pin", pin)
            intent.putExtras(mBundle)
            context.startActivity(intent)
            firstEnter = true
        }

        fun checkLength(str: String?, fb: FeasyBeacon) {
            fb.keycfg = false
            fb.gsensor = false
            fb.buzzer = false
            fb.led = false
        }
    }
}