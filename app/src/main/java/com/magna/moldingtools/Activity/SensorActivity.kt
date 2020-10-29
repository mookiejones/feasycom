package com.magna.moldingtools.Activity

import android.Manifest
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
import com.feasycom.controler.FscBeaconApi
import com.feasycom.controler.FscBeaconApiImp
import com.magna.moldingtools.Adapter.SensorDeviceListAdapter
import com.magna.moldingtools.Bean.BaseEvent
import com.magna.moldingtools.Controler.FscBeaconCallbacksImpSensor
import com.magna.moldingtools.R
import com.magna.moldingtools.Widget.PinDialog
import com.magna.moldingtools.Widget.RefreshableView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.lang.ref.WeakReference
import java.util.*

private const val TAG = "SensorActivity"
private const val ENABLE_BT_REQUEST_ID = 1
private const val REQUEST_EXTERNAL_STORAGE = 1
class SensorActivity : BaseActivity() {
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
    var devicesAdapter: SensorDeviceListAdapter? = null
        private set
    lateinit var fscBeaconApi: FscBeaconApi
        private set

    private var pinDialog: PinDialog? = null
    val handler = Handler()
    private var timerUI: Timer? = null
    private var timerTask: TimerTask? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (fscBeaconApi != null) {
            Log.e("Set", fscBeaconApi!!.isConnected.toString() + "")
        }
        setContentView(R.layout.activity_set)

        ButterKnife.bind(this)
        initView()
        devicesAdapter = SensorDeviceListAdapter(this, layoutInflater)
        devicesList!!.adapter = devicesAdapter
        /**
         * remove the dividing line
         */
        devicesList!!.dividerHeight = 0
        pinDialog = PinDialog(this)
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume")
        /**
         * registered EventBus
         */
        EventBus.getDefault().register(this)
        fscBeaconApi = FscBeaconApiImp.getInstance(this)
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
        fscBeaconApi.setCallbacks(FscBeaconCallbacksImpSensor(WeakReference(this)))
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
        fscBeaconApi.startScan(0)
        timerUI = Timer()
        timerTask = UITimerTask(WeakReference<SensorActivity>(this))
        timerUI!!.schedule(timerTask, 100, 100)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.repeatCount == 0) {
            MainActivity.actionStart(this)
            finishActivity()
        }
        return true
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
                Log.e(TAG, "run 223: 开始扫描")
                fscBeaconApi!!.startScan(0)
                //fscBeaconApi.startScan(0);
                refreshableView!!.finishRefreshing()
            }
        }, 0)
    }

    @Subscribe
    fun onEventMainThread(event: BaseEvent) {
        when (event.eventId) {
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
        /*BluetoothDeviceWrapper deviceDetail = (BluetoothDeviceWrapper) devicesAdapter.getItem(position);
        if (null != deviceDetail.getFeasyBeacon() && null != deviceDetail.getFeasyBeacon() && FeasyBeacon.BLE_KEY_WAY.equals(deviceDetail.getFeasyBeacon().getEncryptionWay().substring(1))) {
            Log.e(TAG, "deviceItemClick: 1" );
            pinDialog.show();
            pinDialog.setPosition(position);
        } else {
            Log.e(TAG, "deviceItemClick: 2" );
            fscBeaconApi.stopScan();
            ParameterSettingActivity.actionStart(activity, (BluetoothDeviceWrapper) devicesAdapter.getItem(position), null);
            finishActivity();
        }*/
    }

    /**
     * search button binding event
     */
    @OnClick(R.id.Search_Button)
    fun searchClick() {
        Log.e(TAG, "searchClick")
        Log.e(TAG, "stoppingScan")
        fscBeaconApi!!.stopScan()
        MainActivity.actionStart(this)
        finishActivity()
    }

    @OnClick(R.id.Sensor_Button)
    fun sensorClick() {
        Log.e(TAG, "sensorClick")
    }

    /**
     * about button binding events
     */
    @OnClick(R.id.About_Button)
    fun aboutClick() {
        Log.e(TAG, "aboutClick")
        fscBeaconApi!!.stopScan()
        AboutActivity.actionStart(this)
        finishActivity()
    }

    /**
     * set the button binding event
     */
    @OnClick(R.id.Set_Button)
    override fun setClick() {
        Log.e(TAG, "setClick")
        fscBeaconApi!!.stopScan()
        SetActivity.actionStart(this)
        finishActivity()
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

    override val tag: String?
        get() = TAG

    internal inner class UITimerTask(private val activityWeakReference: WeakReference<SensorActivity>) : TimerTask() {
        override fun run() {
            activityWeakReference.get()!!.runOnUiThread {
                activityWeakReference.get()!!.devicesAdapter!!.addDevice(activityWeakReference.get()!!.deviceQueue.poll())
                activityWeakReference.get()!!.devicesAdapter!!.notifyDataSetChanged()
            }
        }
    }

    companion object {


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
            Log.e(TAG, "Starting SensorActivity")
            val intent = Intent(context, SensorActivity::class.java)
            context.startActivity(intent)
        }
    }
}