package com.feasycom.fsybecon.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.feasycom.fsybecon.Adapter.SettingBeaconParameterListAdapter;
import com.feasycom.fsybecon.BeaconView.GsensorSpinnerView;
import com.feasycom.fsybecon.BeaconView.IntervalSpinnerView;
import com.feasycom.fsybecon.BeaconView.KeycfgSpinnerView;
import com.feasycom.fsybecon.BeaconView.LableButtonView;
import com.feasycom.fsybecon.BeaconView.LableEditView;
import com.feasycom.fsybecon.BeaconView.LableSpinnerView;
import com.feasycom.fsybecon.Bean.BaseEvent;
import com.feasycom.fsybecon.Controler.FscBeaconCallbacksImpParameter;
import com.feasycom.fsybecon.R;
import com.feasycom.fsybecon.Utils.ViewUtil;
import com.feasycom.fsybecon.Widget.InfoDialog;
import com.feasycom.fsybecon.Widget.OTADetermineDialog;
import com.feasycom.bean.BeaconBean;
import com.feasycom.bean.BluetoothDeviceWrapper;
import com.feasycom.bean.FeasyBeacon;
import com.feasycom.controler.FscBeaconApi;
import com.feasycom.controler.FscBeaconApiImp;
import com.feasycom.util.FeasyBeaconUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.feasycom.fsybecon.Activity.SetActivity.OPEN_TEST_MODE;

/**
 * Copyright 2017 Shenzhen Feasycom Technology co.,Ltd
 */

public class ParameterSettingActivity extends BaseActivity {

    private static final String PIN_VALUE="000000";
    public static final int REQUEST_BEACON_ADD = 1;
    private static final String TAG = "ParameterSettingActivit";
    public static boolean firstEnter = true;
    public static int TOTAL_COUNT = 0;
    public static int SUCESSFUL_COUNT = 0;
    public static boolean isModule_BP109 = false;
    public static boolean isModule_BP101 = false;
    public static boolean isModule_BP671 = false;
    public static boolean back = false;
    // do not  injected here otherwise listViewHeader can not use annotation injection
    public ListView parameterlistview;
    View listViewHeader;
    View listViewFooter;
    @BindView(R.id.TxPower)
    LableSpinnerView TxPower;
    @BindView(R.id.TxPowerDivider)
    View TxPowerDivider;
    @BindView(R.id.adv_interval)
    IntervalSpinnerView adv_interval;
    @BindView(R.id.adv_gsensor)
    GsensorSpinnerView adv_gsensor;
    @BindView(R.id.adv_keycfg)
    KeycfgSpinnerView adv_keycfg;
    // @BindView(R.id.AdvIntervalDivider)
    //View AdvIntervalDivider;
    @BindView(R.id.ConnectableDivider)
    View ConnectableDivider;
    @BindView(R.id.PIN)
    LableEditView PIN;
    @BindView(R.id.PIN_divider)
    View PINDivider;
    @BindView(R.id.add_LL)
    LinearLayout addLL;
    @BindView(R.id.header_left)
    TextView headerLeft;
    @BindView(R.id.header_title)
    TextView headerTitle;
    @BindView(R.id.header_right)
    TextView headerRight;
    /*
    @BindView(R.id.Search_Button)
    ImageView SearchButton;
    @BindView(R.id.Set_Button)
    ImageView SetButton;
    @BindView(R.id.About_Button)
    ImageView AboutButton;
    */
    @BindView(R.id.Module)
    LableEditView Module;
    @BindView(R.id.Version)
    LableEditView Version;
    @BindView(R.id.Name)
    LableEditView Name;
    @BindView(R.id.Interval)
    LableEditView Interval;
    @BindView(R.id.Gsensor)
    LableEditView Gsensor;
    @BindView(R.id.Keycfg)
    LableEditView Keycfg;
    @BindView(R.id.Connectable)
    LableButtonView Connectable;
    @BindView(R.id.ExtEnd)
    LableEditView ExtEnd;
    InfoDialog connectDialog;
    OTADetermineDialog otaDetermineDialog;
    //for BLE password
    String pin2Connect;
    private FscBeaconApi fscBeaconApi;
    private BluetoothDeviceWrapper device;
    private Handler handler = new Handler();
    private SettingBeaconParameterListAdapter adapter;
    private Activity activity;
    Runnable checkConnect = new Runnable() {
        @Override
        public void run() {
            Log("run");

            if (!fscBeaconApi.isConnected() && (("connecting...".equals(connectDialog.getInfo()))
                    || ("check password...".equals(connectDialog.getInfo())))) {
                fscBeaconApi.setCallbacks(null);
                connectDialog.setInfo("timeout");
                connectFailedHandler();
            }
        }
    };
    private String moduleString;
    private String encryptWay;

    public static void actionStart(Context context, BluetoothDeviceWrapper device, String pin) {
        Log.e(TAG, "Starting ParameterSettingActivity");
        Intent intent = new Intent(context, ParameterSettingActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable("device", device);
        mBundle.putSerializable("pin", pin);
        intent.putExtras(mBundle);
        context.startActivity(intent);
        firstEnter = true;
    }

    public static void checkLength(String str, FeasyBeacon fb) {

        fb.setKeycfg(false);
        fb.setGsensor(false);
        fb.setBuzzer(false);
        fb.setLed(false);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");


        back = false;
        setContentView(R.layout.activity_parameter_configuration);
        activity = this;
        EventBus.getDefault().register(this);
        device = (BluetoothDeviceWrapper) getIntent().getSerializableExtra("device");
        pin2Connect = (String) getIntent().getSerializableExtra("pin");

        connectDialog = new InfoDialog(activity, "connecting...");
        otaDetermineDialog = new OTADetermineDialog(activity);
        parameterlistview = findViewById(R.id.parameter_listview);
        adapter = new SettingBeaconParameterListAdapter(activity, getLayoutInflater(), device.getFeasyBeacon());
        adapterInit(adapter);
        listViewHeader =
                getLayoutInflater().inflate(R.layout.setting_parameter_header, null, false);
        listViewFooter = getLayoutInflater().inflate(R.layout.setting_parameter_footer, null, false);
        parameterlistview.addHeaderView(listViewHeader);
        parameterlistview.addFooterView(listViewFooter, null, false);

        /**
         * inject here or listViewHeader can not use annotation injection
         */
        ButterKnife.bind(this);



/*
        txPowerlist = Arrays.asList(getResources().getStringArray(R.array.txpower_table));
        spinnerAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, txPowerlist);
        TxPower.spinnerInit(spinnerAdapter, txPowerlist);
*/
        if (null != device.getFeasyBeacon()) {
            encryptWay = device.getFeasyBeacon().getEncryptionWay();

            moduleString = device.getFeasyBeacon().getmodule();
            FeasyBeacon fb = device.getFeasyBeacon();
            fb.setKeycfg(false);
            fb.setGsensor(false);
            fb.setBuzzer(false);
            fb.setLed(false);
            List<String> advin = Arrays.asList(getResources().getStringArray(R.array.advin));
            List<String> duration = Arrays.asList(getResources().getStringArray(R.array.duration));


            List<String> advIntervalList;
            if (device.getFeasyBeacon().getGsensor() || device.getFeasyBeacon().getKeycfg()) {
                advIntervalList = Arrays.asList(getResources().getStringArray(R.array.interval_table_1));
            } else {
                advIntervalList = Arrays.asList(getResources().getStringArray(R.array.interval_table));
            }

            ArrayAdapter<String> intervalSpinnerAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, advIntervalList);
            adv_interval.spinnerInit(intervalSpinnerAdapter, advIntervalList, device.getFeasyBeacon());
            if (device.getFeasyBeacon().getKeycfg()) {
                adv_keycfg.setVisibility(View.VISIBLE);
                ArrayAdapter<String> keycfgAdvinSpinnerAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, advin);
                ArrayAdapter<String> keycfgDurationSpinnerAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, duration);
                adv_keycfg.spinnerAdvin(keycfgAdvinSpinnerAdapter, advin);
                adv_keycfg.spinnerDuration(keycfgDurationSpinnerAdapter, duration);
            }
            if (device.getFeasyBeacon().getGsensor()) {
                adv_gsensor.setVisibility(View.VISIBLE);
                ArrayAdapter<String> gsensorAdvinSpinnerAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, advin);
                ArrayAdapter<String> gsensorDurationSpinnerAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, duration);
                adv_gsensor.spinnerAdvin(gsensorAdvinSpinnerAdapter, advin);
                adv_gsensor.spinnerDuration(gsensorDurationSpinnerAdapter, duration);
            }


            /**
             * We will be compatible with many modules by moduleString and versionString
             * firmware version
             */
            String versionString = device.getFeasyBeacon().getVersion();
            /**
             * TxPower bind with BP103 BP104 BP106 BP101 BP671
             */
            List<String> txPowerlist;
            switch (moduleString){
                case "26":
                case "27":
                case "28":
                case "29":
                    isModule_BP109 = false;
                    isModule_BP101 = false;
                    isModule_BP671 = false;
                    TxPower.setVisibility(View.VISIBLE);
                    TxPowerDivider.setVisibility(View.VISIBLE);
                    txPowerlist = Arrays.asList(getResources().getStringArray(R.array.txpower_table));
                    intervalSpinnerAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, txPowerlist);
                    TxPower.spinnerInit(intervalSpinnerAdapter, txPowerlist);
                    break;
                case "25":
                    isModule_BP109 = true;
                    isModule_BP101 = false;
                    isModule_BP671 = false;
                    TxPower.setVisibility(View.GONE);
                    TxPowerDivider.setVisibility(View.GONE);
                    break;
                case "30":
                    isModule_BP109 = false;
                    isModule_BP671 = false;
                    isModule_BP101 = true;
                    TxPower.setVisibility(View.VISIBLE);
                    TxPowerDivider.setVisibility(View.VISIBLE);
                    txPowerlist = Arrays.asList(getResources().getStringArray(R.array.BP101_txpower_table));
                    intervalSpinnerAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, txPowerlist);
                    TxPower.spinnerInit(intervalSpinnerAdapter, txPowerlist);
                    break;
                case "31":
                    isModule_BP109 = false;
                    isModule_BP671 = true;
                    isModule_BP101 = false;
                    TxPower.setVisibility(View.VISIBLE);
                    TxPowerDivider.setVisibility(View.VISIBLE);
                    txPowerlist = Arrays.asList(getResources().getStringArray(R.array.BP671_txpower_table));
                    intervalSpinnerAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, txPowerlist);
                    TxPower.spinnerInit(intervalSpinnerAdapter, txPowerlist);
                    break;
                case "unknow":
                    Connectable.setCheck(true);
                    break;
                default:
                    if(versionString.length()!=3){
                        Connectable.setCheck(true);
                    }else {
                        /**
                         * connectable button does not appear if BLE password is used
                         */
                        if (FeasyBeacon.BLE_KEY_WAY.equals(encryptWay.substring(1))) {
                            Connectable.setVisibility(View.GONE);
                            ConnectableDivider.setVisibility(View.GONE);
                        } else {
                            Connectable.setVisibility(View.VISIBLE);
                            ConnectableDivider.setVisibility(View.VISIBLE);
                        }
                        Connectable.setCheck(device.getFeasyBeacon().isConnectable());
                    }
                    break;
            }


            if (FeasyBeacon.BLE_KEY_WAY.equals(encryptWay.substring(1))) {
                PIN.setVisibility(View.VISIBLE);
                PINDivider.setVisibility(View.VISIBLE);
            } else {
                PIN.setVisibility(View.GONE);
                PINDivider.setVisibility(View.GONE);
            }
        } else {
            // Log.e("ParameterSetting", "为null");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        fscBeaconApi = FscBeaconApiImp.getInstance(activity);
        fscBeaconApi.initialize();

        Name.setTextWacher(new ViewUtil.NameTextWatcher(Name, fscBeaconApi));
        Interval.setTextWacher(new ViewUtil.IntervalTextWatcher(Interval, fscBeaconApi));
        PIN.setTextWacher(new ViewUtil.PinTextWatcher(PIN, fscBeaconApi));
        ExtEnd.setTextWacher(new ViewUtil.ExtendTextWatcher(ExtEnd, fscBeaconApi));
        Gsensor.setTextWacher(new ViewUtil.GsensorTextWatcher(Gsensor, fscBeaconApi));
        Keycfg.setTextWacher(new ViewUtil.KeyTextWatcher(Keycfg, fscBeaconApi));
        initView();
        if (OPEN_TEST_MODE) {
            connectAndGetInformation();
        } else {
            if (!fscBeaconApi.isConnected()) {
                if (null == device.getFeasyBeacon()
                        || FeasyBeaconUtil.updateDetermine(device.getFeasyBeacon().getVersion(), device.getFeasyBeacon().getmodule())) {
                    otaDetermineDialog.show();
                } else {
                    connectAndGetInformation();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");

        EventBus.getDefault().unregister(this);
        fscBeaconApi.disconnect();
        handler.removeCallbacks(checkConnect);
        super.onDestroy();
    }

    @Override
    public void initView() {
        Log.e(TAG, "initView");
        parameterlistview.setAdapter(adapter);
        Connectable.setOnToggleChanged(on -> {

            Log.e(TAG, "Setting Connectable to " + on);

            fscBeaconApi.setConnectable(on);
        });
        fscBeaconApi.setCallbacks(new FscBeaconCallbacksImpParameter(
                new WeakReference<>((ParameterSettingActivity) activity), fscBeaconApi, moduleString, device.getFeasyBeacon()));
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log("onKeyDown");
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            fscBeaconApi.disconnect();
            SetActivity.actionStart(activity);
            finishActivity();
        }
        return true;
    }

    @Override
    public void refreshHeader() {
        Log.e(TAG, "refreshHeader");
        headerTitle.setText(getResources().getString(R.string.parameter_setting_title));
        headerLeft.setText(getResources().getString(R.string.back));
        headerRight.setText(getResources().getString(R.string.save));
    }

    @Override
    public void refreshFooter() {
        /**
         * footer image src init
         */
        /*
        SetButton.setImageResource(R.drawable.setting_on);
        AboutButton.setImageResource(R.drawable.about_off);
        SearchButton.setImageResource(R.drawable.search_off);
        */
        Log("refreshFooter");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log("onActivityResult");
        if (resultCode == AddBeaconActivity.REQUEST_BEACON_ADD_OK) {
            BeaconBean beaconBean = (BeaconBean) data.getSerializableExtra("beaconBean");
            fscBeaconApi.addBeaconInfo(beaconBean);
            if (fscBeaconApi.isBeaconInfoFull()) {
                addBeaconEnable(false);
            } else {
                addBeaconEnable(true);
            }
            runOnUiThread(() -> adapter.notifyDataSetChanged());
        }
    }

    @Subscribe
    public void onEventMainThread(BaseEvent event) {
        Log.e(TAG, "onEventMainThread");
        switch (event.getEventId()) {
            case BaseEvent.DELE_BEACON_EVENT:
                fscBeaconApi.deleteBeaconInfo(event.getIndex());
                adapter.notifyDataSetChanged();
                addBeaconEnable(true);
                break;
            case BaseEvent.OTA_EVENT_YES:
                UpgradeActivity.actionStart(activity, device, pin2Connect);
                finishActivity();
                break;
            case BaseEvent.OTA_EVENT_NO:
                connectAndGetInformation();
                break;
        }
    }

    @OnClick(R.id.add_IV)
    public void addBeacon() {
        Log("addBeacon");
        Intent intent = new Intent(activity, AddBeaconActivity.class);
        startActivityForResult(intent, REQUEST_BEACON_ADD);
    }

    //@OnClick(R.id.Set_Button)
    public void setClick() {
        Log("setClick");
    }

    //@OnClick(R.id.About_Button)
    public void aboutClick() {
        Log("aboutClick");
        fscBeaconApi.disconnect();
        AboutActivity.actionStart(activity);
        finishActivity();
    }

    //@OnClick(R.id.Search_Button)
    public void searchClick() {
        Log("searchClick");
        fscBeaconApi.disconnect();
        MainActivity.actionStart(activity);
        finishActivity();
    }

    @Override
    public void sensorClick() {
        Log("sensorClick");
        fscBeaconApi.disconnect();
        SensorActivity.actionStart(activity);
        finishActivity();
    }

    @OnClick(R.id.header_left)
    public void goBack() {
        Log("goBack");

        back = true;
        fscBeaconApi.disconnect();
       /* Log.e(TAG, "goBack: " );
        SetActivity.actionStart(activity);
        finishActivity();*/
    }

    @OnClick(R.id.header_right)
    public void save() {
        Log("save");

        if (!IntervalSpinnerView.verify) {
            //添加取消
            AlertDialog alertDialog2 = new AlertDialog.Builder(this)
                    .setTitle("ERROR")
                    .setMessage("Interval,Gsonser and Key cannot be \"Zero\" at the same time")
                    .setIcon(R.mipmap.ic_launcher)
                    .setNegativeButton(" cancel", (dialog, which) -> {

                    })
                    .create();
            alertDialog2.show();
        } else if (!(KeycfgSpinnerView.keycfgSend || GsensorSpinnerView.gsensorSend) && IntervalSpinnerView.position > 11) {
            //添加取消
            AlertDialog alertDialog2 = new AlertDialog.Builder(this)
                    .setTitle("ERROR")
                    .setMessage("Interval ,G-Sensor and Key , at least one value :0<X≤2\n")
                    .setIcon(R.mipmap.ic_launcher)
                    .setNegativeButton(" cancel", (dialog, which) -> {
                    })
                    .create();
            alertDialog2.show();
        } else {
            if (fscBeaconApi.isConnected()) {
                connectDialog.setInfo("save...");
                connectDialog.show();
                fscBeaconApi.saveBeaconInfo();
            } else {
                return;
            }
        }

    }

    public void connectFailedHandler() {
        Log("connectFailedHandler");

        if (back) {
            SetActivity.actionStart(activity);
            finishActivity();
        } else {
            handler.postDelayed(() -> {
                connectDialog.dismiss();
                fscBeaconApi.disconnect();
                Log.e(TAG, "保存后的页面跳转 ");
                SetActivity.actionStart(activity);
                finishActivity();
            }, InfoDialog.INFO_DIAOLOG_SHOW_TIME);
        }
    }

    private void connectAndGetInformation() {
        Log("connectAndGetInformation");

        connectDialog.show();
        int CHECK_CONNECT_TIME = 50000;
        handler.postDelayed(checkConnect, CHECK_CONNECT_TIME);
        fscBeaconApi.connect(device, pin2Connect);
        TOTAL_COUNT++;
    }



    // enable to add beacon button
    public void addBeaconEnable(boolean enable) {
        Log("addBeaconEnable");

        if (enable) {
            runOnUiThread(() -> addLL.setVisibility(View.VISIBLE));
        } else {
            runOnUiThread(() -> addLL.setVisibility(View.GONE));
        }
    }

    /**
     * initialize 10 broadcast messages
     *
     * @param adapter
     */
    private void adapterInit(Adapter adapter) {
        Log("adapterInit");

        SettingBeaconParameterListAdapter mAdapter = (SettingBeaconParameterListAdapter) adapter;
        for (int i = 0; i < fscBeaconApi.BEACON_AMOUNT; i++) {
            mAdapter.addBeacon(new BeaconBean(Integer.valueOf(i + 1).toString(), FeasyBeacon.BEACON_TYPE_NULL));
        }
    }

    public InfoDialog getConnectDialog() {

        return connectDialog;
    }

    public LableEditView getInterval() {
        return Interval;
    }

    public LableButtonView getConnectable() {
        return Connectable;
    }

    public LableEditView getExtEnd() {
        return ExtEnd;
    }

    public String getEncryptWay() {
        return encryptWay;
    }

    public LableEditView getPIN() {
        return PIN;
    }

    public IntervalSpinnerView getAdvInterval() {
        return adv_interval;
    }


    public LableSpinnerView getTxPower() {
        return TxPower;
    }

    public LableEditView getName() {
        return Name;
    }

    public LableEditView getVersion() {
        return Version;
    }

    public GsensorSpinnerView getGsensor() {
        return adv_gsensor;
    }

    public KeycfgSpinnerView getKeycfg() {
        return adv_keycfg;
    }

    public LableEditView getModule() {
        return Module;
    }

    public SettingBeaconParameterListAdapter getAdapter() {
        return adapter;
    }

    public Handler getHandler() {
        return handler;
    }

    public Runnable getCheckConnect() {
        return checkConnect;
    }

    public Activity getActivity() {
        return activity;
    }

    public String getPin2Connect() {
        return pin2Connect;
    }

    public ListView getParameterlistview() {
        return parameterlistview;
    }

    public FscBeaconApi getFscBeaconApi() {
        return fscBeaconApi;
    }

}
