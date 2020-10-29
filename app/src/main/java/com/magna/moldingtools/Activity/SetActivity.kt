package com.magna.moldingtools.Activity

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
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
import com.feasycom.bean.FeasyBeacon
import com.feasycom.controler.FscBeaconApi
import com.feasycom.controler.FscBeaconApiImp
import com.magna.moldingtools.Activity.ParameterSettingActivity.Companion.actionStart
import com.magna.moldingtools.Adapter.SettingDeviceListAdapter
import com.magna.moldingtools.Bean.BaseEvent
import com.magna.moldingtools.Controler.FscBeaconCallbacksImpSet
import com.magna.moldingtools.R
import com.magna.moldingtools.Widget.RefreshableView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.lang.ref.WeakReference
import java.util.*
private const val ENABLE_BT_REQUEST_ID = 1
private const val REQUEST_EXTERNAL_STORAGE = 1
private const val TAG="SetActivity"
/**
 * Copyright 2017 Shenzhen Feasycom Technology co.,Ltd
 */
class SetActivity : BaseActivity() {
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
    @BindView(R.id.devicesList)
    var devicesList: ListView? = null

    @JvmField
    @BindView(R.id.refreshableView)
    var refreshableView: RefreshableView? = null

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
    @BindView(R.id.Sensor_Button)
    var SensorButton: ImageView? = null
    var deviceQueue: Queue<BluetoothDeviceWrapper> = LinkedList()
    var devicesAdapter: SettingDeviceListAdapter? = null
        private set
    lateinit var fscBeaconApi: FscBeaconApi 
        private set
    private var activity: Activity? = null
    val handler = Handler()
    private var timerUI: Timer? = null
    private var timerTask: TimerTask? = null
    override val tag: String?=TAG

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG, "onCreate")
        if (fscBeaconApi != null) {
            Log.e(TAG, "fscBeacon.isConnected: " + fscBeaconApi!!.isConnected)
        }
        setContentView(R.layout.activity_set)
        activity = this
        ButterKnife.bind(this)
        initView()
        devicesAdapter = SettingDeviceListAdapter(activity, layoutInflater)
        devicesList!!.adapter = devicesAdapter
        /**
         * remove the dividing line
         */
        devicesList!!.dividerHeight = 0
        //        pinDialog = new PinDialog(activity);
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume")
        /**
         * registered EventBus
         */
        EventBus.getDefault().register(this)
        fscBeaconApi = FscBeaconApiImp.getInstance(activity)
        fscBeaconApi.initialize()
        if (fscBeaconApi.checkBleHardwareAvailable() == false) {
            bleMissing()
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
        }
        fscBeaconApi.setCallbacks(FscBeaconCallbacksImpSet(WeakReference(activity as SetActivity?)))
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
        if (OPEN_TEST_MODE) {
            fscBeaconApi.startScan(25000)
        } else {
            if (SCAN_FIXED_TIME) {
                fscBeaconApi.startScan(15000)
            } else {
                fscBeaconApi.startScan(15000)
            }
        }
        timerUI = Timer()
        timerTask = UITimerTask(WeakReference<SetActivity>(this))
        timerUI!!.schedule(timerTask, 100, 100)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        Log.e(TAG, "onKeyDown")
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.repeatCount == 0) {
            MainActivity.actionStart(this)
            finishActivity()
        }
        return true
    }

    override fun onPause() {
        super.onPause()
        Log.e(TAG, "onPause")
        if (timerTask != null) {
            timerTask!!.cancel()
            timerTask = null
        }
        if (timerUI != null) {
            timerUI!!.cancel()
            timerUI = null
        }
        EventBus.getDefault().unregister(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e(TAG, "onActivityResult")
        /**
         * user didn't want to turn on BT
         */
        if (requestCode == ENABLE_BT_REQUEST_ID) {
            if (resultCode == RESULT_CANCELED) {
                btDisabled()
                return
            }
        }
    }

    override fun initView() {
        Log.e(TAG, "initView")
        refreshableView!!.setOnRefreshListener({
            runOnUiThread {
                deviceQueue.clear()
                devicesAdapter!!.clearList()
                devicesAdapter!!.notifyDataSetChanged()
                fscBeaconApi!!.stopScan()
                fscBeaconApi!!.startScan(15000)
                //fscBeaconApi.startScan(0);
                refreshableView!!.finishRefreshing()
            }
        }, 0)
    }

    @Subscribe
    fun onEventMainThread(event: BaseEvent) {
        Log.e(TAG, "onEventMainThread")
        when (event.eventId) {
            BaseEvent.PIN_EVENT -> {
                val position = event.getObject("position") as Int
                val pin = event.getObject("pin") as String
                fscBeaconApi!!.stopScan()
                actionStart(activity!!, devicesAdapter!!.getItem(position) as BluetoothDeviceWrapper, pin)
                finishActivity()
            }
        }
    }

    @OnClick(R.id.header_left)
    fun deviceSort() {
        Log.e(TAG, "deviceSort")
        runOnUiThread {
            devicesAdapter!!.sort()
            devicesAdapter!!.notifyDataSetChanged()
        }
    }

    @OnClick(R.id.headerRight)
    fun deviceFilterClick() {
        Log.e(TAG, "deviceFilterClick")
    }

    @OnItemClick(R.id.devicesList)
    fun deviceItemClick(position: Int) {
        Log.e(TAG, "deviceItemClick")
        val deviceDetail = devicesAdapter!!.getItem(position) as BluetoothDeviceWrapper
        if (null != deviceDetail.feasyBeacon && null != deviceDetail.feasyBeacon && FeasyBeacon.BLE_KEY_WAY == deviceDetail.feasyBeacon.encryptionWay.substring(1)) {
            Log.e(TAG, "deviceItemClick: 1")
            Log.e(TAG, "showing Pin Dialog")
            //            pinDialog.show();
//            pinDialog.setPosition(position);
            fscBeaconApi!!.stopScan()
            actionStart(activity!!, devicesAdapter!!.getItem(position) as BluetoothDeviceWrapper, PIN)
            finishActivity()
        } else {
            Log.e(TAG, "deviceItemClick: 2")
            fscBeaconApi!!.stopScan()
            actionStart(activity!!, devicesAdapter!!.getItem(position) as BluetoothDeviceWrapper, null)
            finishActivity()
        }
    }

    /**
     * search button binding event
     */
    @OnClick(R.id.Search_Button)
    fun searchClick() {
        Log.e(TAG, "searchClick")
        fscBeaconApi!!.stopScan()
        MainActivity.actionStart(activity!!)
        finishActivity()
    }

    @OnClick(R.id.Sensor_Button)
    fun sensorClick() {
        Log.e(TAG, "sensorClick")
        fscBeaconApi!!.stopScan()
        SensorActivity.actionStart(activity!!)
        finishActivity()
    }

    /**
     * about button binding events
     */
    @OnClick(R.id.About_Button)
    fun aboutClick() {
        Log.e(TAG, "aboutClick")
        fscBeaconApi!!.stopScan()
        AboutActivity.actionStart(activity!!)
        finishActivity()
    }

    /**
     * set the button binding event
     */
    @OnClick(R.id.Set_Button)
    override fun setClick() {
        Log.e(TAG, "setClick")
        //        fscBeaconApi.startScan(15000);
        //fscBeaconApi.startScan(0);
    }

    /**
     * bluetooth is not turned on
     */
    private fun btDisabled() {
        Log.e(TAG, "btDisabled")
        Toast.makeText(this, "Sorry, BT has to be turned ON for us to work!", Toast.LENGTH_LONG).show()
        finishActivity()
    }

    /**
     * does not support BLE
     */
    private fun bleMissing() {
        Log.e(TAG, "bleMissing")
        Toast.makeText(this, "BLE Hardware is required but not available!", Toast.LENGTH_LONG).show()
        finishActivity()
    }

    internal inner class UITimerTask(private val activityWeakReference: WeakReference<SetActivity>) : TimerTask() {
        override fun run() {
            activityWeakReference.get()!!.runOnUiThread {
                activityWeakReference.get()!!.devicesAdapter!!.addDevice(activityWeakReference.get()!!.deviceQueue.poll())
                activityWeakReference.get()!!.devicesAdapter!!.notifyDataSetChanged()
            }
        }
    }

    companion object {
        const val OPEN_TEST_MODE = false
        const val SCAN_FIXED_TIME = false
       
        /**
         * location permissions
         */
        private val PERMISSIONS_LOCATION = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
                Manifest.permission.BLUETOOTH_PRIVILEGED
        )
        const val PIN = "000000"
        @JvmStatic
        fun actionStart(context: Context) {
            Log.e(TAG, "actionStart")
            val intent = Intent(context, SetActivity::class.java)
            context.startActivity(intent)
        }
    }
}