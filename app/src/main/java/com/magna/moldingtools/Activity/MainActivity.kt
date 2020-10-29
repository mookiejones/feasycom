package com.magna.moldingtools.Activity

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.OnItemClick
import com.feasycom.bean.BluetoothDeviceWrapper
import com.feasycom.controler.FscBeaconApi
import com.feasycom.controler.FscBeaconApiImp
import com.feasycom.util.LogUtil
import com.magna.moldingtools.Adapter.SearchDeviceListAdapter
import com.magna.moldingtools.Controler.FscBeaconCallbacksImpMain
import com.magna.moldingtools.R
import com.magna.moldingtools.Widget.RefreshableView
import java.lang.ref.WeakReference
import java.util.*

/**
 * Copyright 2017 Shenzhen Feasycom Technology co.,Ltd
 */
class MainActivity : BaseActivity() {

    //region  Fields

    //endregion
    @BindView(R.id.header_left)
    var header_left: TextView? = null

    @BindView(R.id.headerTitle)
    var headerTitle: TextView? = null

    @BindView(R.id.headerRight)
    var headerRight: TextView? = null

    @BindView(R.id.devicesList)
    var devicesList: ListView? = null

    @BindView(R.id.refreshableView)
    var refreshableView: RefreshableView? = null

    @BindView(R.id.Search_Button)
    var Search_Button: ImageView? = null

    @BindView(R.id.Set_Button)
    var Set_Button: ImageView? = null

    @BindView(R.id.About_Button)
    var About_Button: ImageView? = null

    @BindView(R.id.Sensor_Button)
    var Sensor_Button: ImageView? = null
    var deviceQueue: Queue<BluetoothDeviceWrapper> = LinkedList()
    var devicesAdapter: SearchDeviceListAdapter? = null
        private set
    private lateinit var fscBeaconApi: FscBeaconApi

    private var timerUI: Timer? = null
    private var timerTask: TimerTask? = null
    private val handler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ButterKnife.bind(this)
        initView()
        devicesAdapter = SearchDeviceListAdapter(this, layoutInflater)
        devicesList!!.adapter = devicesAdapter
        /**
         * remove the dividing line
         */
        devicesList!!.dividerHeight = 0
    }

    override fun onResume() {
        super.onResume()
        fscBeaconApi = FscBeaconApiImp.getInstance(this)
        fscBeaconApi.initialize()
        LogUtil.setDebug(true)
        if (!fscBeaconApi.checkBleHardwareAvailable()) {
            bleMissing()
        }
        /**
         * on every Resume check if BT is enabled (user could turn it off while app was in background etc.)
         */
        if (fscBeaconApi.isBtEnabled() == false) {
            /**
             * BT is not turned on - ask user to make it enabled
             */
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, ENABLE_BT_REQUEST_ID)
            /**
             * see onActivityResult to check what is the status of our request
             */
        }
        /**
         * Check if we have write permission
         */
        val permission2 = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permission2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_LOCATION,
                    REQUEST_EXTERNAL_STORAGE
            )
        } else {
//            fscBeaconApi.setCallbacks(FscBeaconCallbacksImpMain(WeakReference(this)))
            if (SetActivity.SCAN_FIXED_TIME) {
                fscBeaconApi.startScan(60000)
            } else {
                fscBeaconApi.startScan(0)
            }
        }
        timerUI = Timer()
        timerTask = UITimerTask(WeakReference<MainActivity>(this@MainActivity))
        timerUI!!.schedule(timerTask, 100, 100)
    }

    override fun onPause() {
        super.onPause()
        if (timerTask != null) {
            timerTask!!.cancel()
            timerTask = null
        }
        if (timerUI != null) {
            timerUI!!.cancel()
            timerUI = null
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        Log.i("result", "123");
        /**
         * user didn't want to turn on BT
         */
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ENABLE_BT_REQUEST_ID) {
            if (resultCode == RESULT_CANCELED) {
                btDisabled()
                return
            }
        }
    }

    override fun initView() {
        refreshableView!!.setOnRefreshListener({
            runOnUiThread {
                deviceQueue.clear()
                devicesAdapter!!.clearList()
                devicesAdapter!!.notifyDataSetChanged()
                fscBeaconApi!!.stopScan()
                android.util.Log.e(TAG, "run: Refresh")
                fscBeaconApi!!.startScan(6000)
                //fscBeaconApi.startScan(0);
                refreshableView!!.finishRefreshing()
            }
        }, 0)
    }

    @OnItemClick(R.id.devicesList)
    fun deviceItemClick(position: Int) {
//        Log.i("click", "main");
        val deviceWrapper = devicesAdapter!!.getItem(position) as BluetoothDeviceWrapper
        val eddystoneBeacon = deviceWrapper.getgBeacon()
        try {
            android.util.Log.i("click", eddystoneBeacon!!.url)
        } catch (e: Exception) {
        }
        if (null != eddystoneBeacon && "URL" == eddystoneBeacon.frameTypeString) {
            val uri = Uri.parse(eddystoneBeacon.url)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
    }

    @OnClick(R.id.header_left)
    fun deviceSort() {
        runOnUiThread {
            devicesAdapter!!.sort()
            devicesAdapter!!.notifyDataSetChanged()
        }
    }

    @OnClick(R.id.headerRight)
    fun deviceFilterClick() {
        fscBeaconApi!!.stopScan()
        FilterDeviceActivity.actionStart(this)
        finishActivity()
    }

    /**
     * search button binding event
     */
    @OnClick(R.id.Search_Button)
    fun searchClick() {
    }

    @OnClick(R.id.Sensor_Button)
    fun sensorClick() {
        fscBeaconApi!!.stopScan()
        SensorActivity.actionStart(this)
        finishActivity()
    }

    /**
     * about button binding events
     */
    @OnClick(R.id.About_Button)
    fun aboutClick() {
        fscBeaconApi!!.stopScan()
        AboutActivity.actionStart(this)
        finishActivity()
    }

    /**
     * set the button binding event
     */
    @OnClick(R.id.Set_Button)
    override fun setClick() {
        fscBeaconApi!!.stopScan()
        android.util.Log.e(TAG, "setClick: ")
        SetActivity.actionStart(this)
        finishActivity()
    }

    /**
     * bluetooth is not turned on
     */
    private fun btDisabled() {
        Toast.makeText(this, "Sorry, BT has to be turned ON for us to work!", Toast.LENGTH_LONG).show()
        finishActivity()
    }

    /**
     * does not support BLE
     */
    private fun bleMissing() {
        Toast.makeText(this, "BLE Hardware is required but not available!", Toast.LENGTH_LONG).show()
        finishActivity()
    }

    override val tag: String?
        get() = TAG

    internal inner class UITimerTask(private val activityWeakReference: WeakReference<MainActivity>) : TimerTask() {
        override fun run() {
            activityWeakReference.get()!!.runOnUiThread {
                activityWeakReference.get()!!.devicesAdapter!!.addDevice(activityWeakReference.get()!!.deviceQueue.poll())
                activityWeakReference.get()!!.devicesAdapter!!.notifyDataSetChanged()
            }
        }
    }

    companion object {
        const val TAG = "MainActivity"
        private const val ENABLE_BT_REQUEST_ID = 1
        private const val REQUEST_EXTERNAL_STORAGE = 1

        /**
         * location permissions
         */
        private val PERMISSIONS_LOCATION = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
                Manifest.permission.BLUETOOTH_PRIVILEGED
        )

        @JvmStatic
        fun actionStart(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
            //        ((Activity) context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);// 淡出淡入动画效果
        }
    }
}