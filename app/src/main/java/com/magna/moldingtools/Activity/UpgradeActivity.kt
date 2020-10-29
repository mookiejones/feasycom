package com.magna.moldingtools.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnCheckedChanged
import butterknife.OnClick
import com.feasycom.bean.BluetoothDeviceWrapper
import com.feasycom.controler.FscSppApi
import com.feasycom.controler.FscSppApiImp
import com.feasycom.util.FeasyBeaconUtil
import com.feasycom.util.FileUtil
import com.feasycom.util.ToastUtil
import com.magna.moldingtools.Controler.FscBeaconCallbacksImpOta
import com.magna.moldingtools.R
import com.magna.moldingtools.Widget.CircleNumberProgress
import java.io.Serializable
import java.lang.ref.WeakReference

/**
 * Copyright 2017 Shenzhen Feasycom Technology co.,Ltd
 */
private const val    TAG = "UpgradeActivity"
class UpgradeActivity : BaseActivity() {
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
    @BindView(R.id.selectFile)
    var selectFile: Button? = null

    @JvmField
    @BindView(R.id.selectFileLL)
    var selectFileLL: LinearLayout? = null

    @JvmField
    @BindView(R.id.otaFileName)
    var otaFileName: TextView? = null

    @JvmField
    @BindView(R.id.currentModule)
    var currentModule: TextView? = null

    @JvmField
    @BindView(R.id.currentVersion)
    var currentVersion: TextView? = null

    @JvmField
    @BindView(R.id.exceptModule)
    var exceptModule: TextView? = null

    @JvmField
    @BindView(R.id.exceptVersion)
    var exceptVersion: TextView? = null

    @JvmField
    @BindView(R.id.reset)
    var reset: CheckBox? = null

    @JvmField
    @BindView(R.id.otaProgress)
    var otaProgress: CircleNumberProgress? = null

    @BindView(R.id.startOTA)
    var startOTA: Button? = null

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
    @BindView(R.id.OTAInfoLL)
    var OTAInfoLL: LinearLayout? = null

    @JvmField
    @BindView(R.id.resetLL)
    var resetLL: LinearLayout? = null

    @JvmField
    @BindView(R.id.otaProgressLL)
    var otaProgressLL: LinearLayout? = null

    //BLE 密码
    var pin: String? = null
    private var activity: Activity? = null
    var goBackRunnable = Runnable {
        Log.e(TAG, "run: ")
        SetActivity.actionStart(activity!!)
        finishActivity()
    }
    private var bluetoothDeviceWrapper: BluetoothDeviceWrapper? = null
    private var currentVersionString: String? = null
    private var currentModuleNumberString: String? = null
    private var addr: String? = null
    private var encryptWay: String? = null
    private var fileByte: ByteArray? = null
    private var fscSppApi: FscSppApi? = null
    var reConnect = Runnable {
        if (!fscSppApi!!.isConnected) {
            fscSppApi!!.connect(addr, pin)
        }
    }
    var handler: Handler? = null
        private set
    override val tag: String?
        get() = TAG

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upgrade)
        ButterKnife.bind(this)
        activity = this
        bluetoothDeviceWrapper = intent.getSerializableExtra("device") as BluetoothDeviceWrapper
        pin = intent.getSerializableExtra("pin") as String
        try {
            currentVersionString = bluetoothDeviceWrapper!!.feasyBeacon.version
            currentModuleNumberString = bluetoothDeviceWrapper!!.feasyBeacon.module
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if ("" == currentVersionString || null == currentVersionString || currentVersionString!!.length != 3) {
            currentVersionString = "unknow"
        }
        addr = bluetoothDeviceWrapper!!.address
        try {
            encryptWay = bluetoothDeviceWrapper!!.feasyBeacon.encryptionWay
        } catch (e: Exception) {
            e.printStackTrace()
        }
        pin = if ("" == intent.getStringExtra("pin") || null == intent.getStringExtra("pin")) {
            null
        } else {
            intent.getStringExtra("pin")
        }
        initView()
        handler = Handler()
        OTAInfoAdapter()
        fscSppApi = FscSppApiImp.getInstance(activity)
        fscSppApi?.initialize()
        fscSppApi?.setCallbacks(FscBeaconCallbacksImpOta(WeakReference(activity as UpgradeActivity)))
        fscSppApi?.connect(addr, pin)
    }

    override fun onDestroy() {
        try {
            handler!!.removeCallbacks(reConnect)
            handler!!.removeCallbacks(goBackRunnable)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        fscSppApi!!.disconnect()
        super.onDestroy()
    }

    override fun initView() {
        // selectFileLL.setVisibility(View.VISIBLE);
        currentModule!!.text = FeasyBeaconUtil.moduleAdapter(currentModuleNumberString)
        currentVersion!!.text = currentVersionString
    }

    fun OTAFinish() {
        handler!!.postDelayed(goBackRunnable, 3000)
    }

    private fun OTAInfoAdapter() {
        val typeNumber = resources.getStringArray(R.array.type_number)
        val dfuFile = resources.getStringArray(R.array.dfu_file)
        val dfuFileName = resources.getStringArray(R.array.dfu_file_name)
        if (typeNumber.size == dfuFile.size && dfuFile.size == dfuFileName.size) {
        } else {
//            Log.e("failed", "please check version table");
        }
        for (i in typeNumber.indices) {
            if (typeNumber[i] == currentModuleNumberString) {
                fileByte = FileUtil.hexToByte(dfuFile[i])
                otaFileName!!.text = dfuFileName[i]
                exceptModule!!.text = FeasyBeaconUtil.getModelByFileName(dfuFileName[i])
                exceptVersion!!.text = FeasyBeaconUtil.getVersionByFileName(dfuFileName[i])
            }
        }
    }

    @OnCheckedChanged(R.id.reset)
    fun restCheck(check: Boolean) {
    }

    @OnClick(R.id.startOTA)
    fun startOTA() {
        if (fscSppApi!!.isConnected) {
            fscSppApi!!.startOTA(fileByte, reset!!.isChecked)
        } else {
            runOnUiThread {
                ToastUtil.show(activity, "failed device is not connected")
                startOTA!!.isEnabled = false
            }
            OTAFinish()
        }
    }

    fun OTAViewSwitch(begin: Boolean) {
        runOnUiThread {
            if (begin) {
                OTAInfoLL!!.visibility = View.GONE
                resetLL!!.visibility = View.GONE
                otaProgressLL!!.visibility = View.VISIBLE
            } else {
                OTAInfoLL!!.visibility = View.VISIBLE
                resetLL!!.visibility = View.VISIBLE
                otaProgressLL!!.visibility = View.GONE
            }
            otaProgress!!.setProgress(0)
        }
    }

    @OnClick(R.id.header_left)
    fun goBack() {
        fscSppApi!!.disconnect()
        SetActivity.actionStart(activity!!)
        finishActivity()
    }

    @OnClick(R.id.Set_Button)
    override fun setClick() {
        // 什么也不干
    }

    @OnClick(R.id.About_Button)
    fun aboutClick() {
        AboutActivity.actionStart(activity!!)
        finishActivity()
    }

    @OnClick(R.id.Search_Button)
    fun searchClick() {
        MainActivity.actionStart(activity!!)
        finishActivity()
    }

    @OnClick(R.id.Sensor_Button)
    fun sensorClick() {
        SensorActivity.actionStart(activity!!)
        finishActivity()
    }

    companion object {

        fun actionStart(context: Context, bluetoothDeviceWrapper: BluetoothDeviceWrapper?, pin: String?) {
            Log.e(TAG, "actionStart")
            val intent = Intent(context, UpgradeActivity::class.java)
            val mBundle = Bundle()
            mBundle.putSerializable("device", bluetoothDeviceWrapper as Serializable?)
            mBundle.putSerializable("pin", pin)
            intent.putExtras(mBundle)
            context.startActivity(intent)
        }
    }
}