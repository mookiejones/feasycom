package com.feasycom.fsybecon.Activity;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.feasycom.fsybecon.Adapter.SearchDeviceListAdapter;
import com.feasycom.fsybecon.Controler.FscBeaconCallbacksImpMain;
import com.feasycom.fsybecon.R;
import com.feasycom.fsybecon.Widget.RefreshableView;
import com.feasycom.bean.BluetoothDeviceWrapper;
import com.feasycom.bean.EddystoneBeacon;
import com.feasycom.controler.FscBeaconApi;
import com.feasycom.controler.FscBeaconApiImp;
import com.feasycom.util.LogUtil;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

import static com.feasycom.fsybecon.Activity.SetActivity.SCAN_FIXED_TIME;


/**
 * Copyright 2017 Shenzhen Feasycom Technology co.,Ltd
 */

public class MainActivity extends BaseActivity {
    public static final String TAG = "MainActivity";
    private static final int ENABLE_BT_REQUEST_ID = 1;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    /**
     * location permissions
     */
    private static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.BLUETOOTH_PRIVILEGED
    };
    @BindView(R.id.header_left)
    TextView headerLeft;
    @BindView(R.id.header_title)
    TextView headerTitle;
    @BindView(R.id.header_right)
    TextView headerRight;
    @BindView(R.id.devicesList)
    ListView devicesList;
    @BindView(R.id.refreshableView)
    RefreshableView refreshableView;
    @BindView(R.id.Search_Button)
    ImageView SearchButton;
    @BindView(R.id.Set_Button)
    ImageView SetButton;
    @BindView(R.id.About_Button)
    ImageView AboutButton;
    @BindView(R.id.Sensor_Button)
    ImageView SensorButton;
    Queue<BluetoothDeviceWrapper> deviceQueue = new LinkedList<BluetoothDeviceWrapper>();
    private SearchDeviceListAdapter devicesAdapter;
    private FscBeaconApi fscBeaconApi;
    private Activity activity;
    private Timer timerUI;
    private TimerTask timerTask;
    private Handler handler = new Handler();

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
//        ((Activity) context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);// 淡出淡入动画效果

    }

    @Override
    protected String getTag() {
        return TAG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        ButterKnife.bind(this);
        initView();
        devicesAdapter = new SearchDeviceListAdapter(activity, getLayoutInflater());
        devicesList.setAdapter(devicesAdapter);
        /**
         * remove the dividing line
         */
        devicesList.setDividerHeight(0);

    }

    @Override
    protected void onResume() {
        super.onResume();
        fscBeaconApi = FscBeaconApiImp.getInstance(activity);
        fscBeaconApi.initialize();
        LogUtil.setDebug(true);
        if (fscBeaconApi.checkBleHardwareAvailable() == false) {
            bleMissing();
        }
        /**
         *  on every Resume check if BT is enabled (user could turn it off while app was in background etc.)
         */
        if (fscBeaconApi.isBtEnabled() == false) {
            /**
             * BT is not turned on - ask user to make it enabled
             */
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, ENABLE_BT_REQUEST_ID);
            /**
             * see onActivityResult to check what is the status of our request
             */
        }
        /**
         * Check if we have write permission
         */
        int permission2 = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_LOCATION,
                    REQUEST_EXTERNAL_STORAGE
            );
        } else {
            fscBeaconApi.setCallbacks(new FscBeaconCallbacksImpMain(new WeakReference<MainActivity>((MainActivity) activity)));
            if (SCAN_FIXED_TIME) {
                fscBeaconApi.startScan(60000);
            } else {
                fscBeaconApi.startScan(0);
            }
        }
        timerUI = new Timer();
        timerTask = new UITimerTask(new WeakReference<MainActivity>((MainActivity) activity));
        timerUI.schedule(timerTask, 100, 100);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        if (timerUI != null) {
            timerUI.cancel();
            timerUI = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.i("result", "123");
        /**
         * user didn't want to turn on BT
         */
        if (requestCode == ENABLE_BT_REQUEST_ID) {
            if (resultCode == Activity.RESULT_CANCELED) {
                btDisabled();
                return;
            }
        }
    }


    @Override
    public void initView() {
        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        deviceQueue.clear();
                        devicesAdapter.clearList();
                        devicesAdapter.notifyDataSetChanged();
                        fscBeaconApi.stopScan();
                        Log.e(TAG, "run: Refresh");
                        fscBeaconApi.startScan(6000);
                        //fscBeaconApi.startScan(0);
                        refreshableView.finishRefreshing();
                    }
                });
            }
        }, 0);
    }

    @OnItemClick(R.id.devicesList)
    public void deviceItemClick(int position) {
//        Log.i("click", "main");
        BluetoothDeviceWrapper deviceWrapper = (BluetoothDeviceWrapper) devicesAdapter.getItem(position);
        EddystoneBeacon eddystoneBeacon = deviceWrapper.getgBeacon();
        try {
            Log.i("click", eddystoneBeacon.getUrl());
        } catch (Exception e) {
        }
        if ((null != eddystoneBeacon) && ("URL".equals(eddystoneBeacon.getFrameTypeString()))) {
            Uri uri = Uri.parse(eddystoneBeacon.getUrl());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    @Override
    public void refreshHeader() {
        //headerTitle.setText(getResources().getString(R.string.app_name));
        headerTitle.setText("Beacon");
        //headerLeft.setText("Sort");
        //headerRight.setText("Filter");
        headerLeft.setVisibility(View.GONE);
        headerRight.setVisibility(View.GONE);
    }

    @OnClick(R.id.header_left)
    public void deviceSort() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                devicesAdapter.sort();
                devicesAdapter.notifyDataSetChanged();
            }
        });
    }

    @OnClick(R.id.header_right)
    public void deviceFilterClick() {
        fscBeaconApi.stopScan();
        FilterDeviceActivity.actionStart(activity);
        finishActivity();
    }

    @Override
    public void refreshFooter() {
        /**
         * footer image src init
         */
        SetButton.setImageResource(R.drawable.setting_off);
        AboutButton.setImageResource(R.drawable.about_off);
        SensorButton.setImageResource(R.drawable.sensor_off);
        SearchButton.setImageResource(R.drawable.search_on);
    }


    /**
     * search button binding event
     */
    @OnClick(R.id.Search_Button)
    public void searchClick() {

    }

    @OnClick(R.id.Sensor_Button)
    public void sensorClick() {
        fscBeaconApi.stopScan();
        SensorActivity.actionStart(activity);
        finishActivity();
    }

    /**
     * about button binding events
     */
    @OnClick(R.id.About_Button)
    public void aboutClick() {
        fscBeaconApi.stopScan();
        AboutActivity.actionStart(activity);
        finishActivity();
    }


    /**
     * set the button binding event
     */
    @OnClick(R.id.Set_Button)
    public void setClick() {
        fscBeaconApi.stopScan();
        Log.e(TAG, "setClick: ");
        SetActivity.actionStart(activity);
        finishActivity();
    }

    /**
     * bluetooth is not turned on
     */
    private void btDisabled() {
        Toast.makeText(this, "Sorry, BT has to be turned ON for us to work!", Toast.LENGTH_LONG).show();
        finishActivity();
    }

    /**
     * does not support BLE
     */
    private void bleMissing() {
        Toast.makeText(this, "BLE Hardware is required but not available!", Toast.LENGTH_LONG).show();
        finishActivity();
    }

    public Queue<BluetoothDeviceWrapper> getDeviceQueue() {
        return deviceQueue;
    }

    public SearchDeviceListAdapter getDevicesAdapter() {
        return devicesAdapter;
    }

    class UITimerTask extends TimerTask {
        private WeakReference<MainActivity> activityWeakReference;

        public UITimerTask(WeakReference<MainActivity> activityWeakReference) {
            this.activityWeakReference = activityWeakReference;
        }

        @Override
        public void run() {
            activityWeakReference.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activityWeakReference.get().getDevicesAdapter().addDevice(activityWeakReference.get().getDeviceQueue().poll());
                    activityWeakReference.get().getDevicesAdapter().notifyDataSetChanged();
                }
            });
        }
    }
}
