package com.feasycom.fsybecon.Adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.feasycom.bean.BluetoothDeviceWrapper;
import com.feasycom.fsybecon.BeaconView.AltBeaconItem;
import com.feasycom.fsybecon.BeaconView.EddystoneBeaconItem;
import com.feasycom.fsybecon.BeaconView.IBeaconItem;
import com.feasycom.fsybecon.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.feasycom.fsybecon.Activity.FilterDeviceActivity.filterValue;
import static com.feasycom.fsybecon.Activity.FilterDeviceActivity.isFilterEnable;
/**
 * Copyright 2017 Shenzhen Feasycom Technology co.,Ltd
 */

public class SearchDeviceListAdapter extends BaseAdapter {
    private LayoutInflater mInflator;
    private Context mContext;
    private ArrayList<BluetoothDeviceWrapper> mDevices = new ArrayList<BluetoothDeviceWrapper>();

    public SearchDeviceListAdapter(Context context, LayoutInflater Inflator) {
        super();
        mContext = context;
        mInflator = Inflator;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mDevices.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mDevices.get(position);
    }

    synchronized public boolean addDevice(BluetoothDeviceWrapper deviceDetail) {
        if (deviceDetail == null) {
            return false;
        }
        int i = 0;
        for (; i < mDevices.size(); i++) {
            if (deviceDetail.getAddress().equals(mDevices.get(i).getAddress())) {
                if (null != deviceDetail.getiBeacon()) {
                    if (deviceDetail.getiBeacon().equals(mDevices.get(i).getiBeacon())) {
                        return false;
                    }
                }
                if (null != deviceDetail.getgBeacon()) {
                    if (deviceDetail.getgBeacon().equals(mDevices.get(i).getgBeacon())) {
                        return false;
                    }
                }
                if (null != deviceDetail.getAltBeacon()) {
                    if (deviceDetail.getAltBeacon().equals(mDevices.get(i).getAltBeacon())) {
                        return false;
                    }
                }
            }
        }
        if (i >= mDevices.size()) {
            mDevices.add(deviceDetail);
        }

        //process the filter event.
        if(isFilterEnable == true)
        {
            Log.i("filterValue", ": "+String.valueOf(filterValue+100));
            if (mDevices.get(i).getRssi() < filterValue) {
                mDevices.remove(i);
            }
        }
        return false;
    }

    public void sort() {
        for (int i=0; i < mDevices.size() - 1; i++) {
            for (int j = 0; j < mDevices.size() - 1 - i; j++) {
                if(mDevices.get(j).getRssi() < mDevices.get(j + 1).getRssi()) {
                    BluetoothDeviceWrapper bd = mDevices.get(j);
                    mDevices.set(j, mDevices.get(j + 1));
                    mDevices.set(j + 1, bd);
                }
            }
        }
    }

    public void clearList() {
        mDevices.clear();
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        // TODO Auto-generated method stub

        ViewHolder viewHolder;
        // General ListView optimization code.
        if (view == null) {
            view = mInflator.inflate(R.layout.search_beacon_info, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        BluetoothDeviceWrapper deviceDetail = mDevices.get(position);
        String deviceName = deviceDetail.getName();
        String completeName = deviceDetail.getCompleteLocalName();
        String deviceAdd = deviceDetail.getAddress();
        String deviceModel = deviceDetail.getModel();
        int deviceRssi = deviceDetail.getRssi().intValue();
        if((completeName != null) && completeName.length()>0){
            //设备名长度限制，最大10
            if (completeName.length() > 10) {
                completeName = completeName.substring(0, 10);
            }
            viewHolder.tvName.setText(completeName);
        }else if (deviceName != null && deviceName.length() > 0) {
            //设备名长度限制，最大10
            if (deviceName.length() > 10) {
                deviceName = deviceName.substring(0, 10);
            }
            viewHolder.tvName.setText(deviceName);
        } else {
            viewHolder.tvName.setText("unknow");
        }
        if (deviceAdd != null && deviceAdd.length() > 0) {
            viewHolder.tvAddr.setText(" (" + deviceAdd + ")");
        } else {
            viewHolder.tvAddr.setText(" (unknow)");
        }

        //iBeacon
        if (null != deviceDetail.getiBeacon()) {
            viewHolder.iBeaconItemView.setIBeaconValue(deviceDetail.getiBeacon());
            viewHolder.iBeaconItemView.setVisibility(View.VISIBLE);
            viewHolder.beaconPic.setImageResource(R.drawable.ibeacon);
        } else {
            viewHolder.iBeaconItemView.setIBeaconValue(null);
            viewHolder.iBeaconItemView.setVisibility(View.GONE);
        }

        //gBeacon
        if (null != deviceDetail.getgBeacon()) {
            viewHolder.gBeaconItemView.setGBeaconValue(deviceDetail.getgBeacon());
            viewHolder.gBeaconItemView.setVisibility(View.VISIBLE);
            if ("URL".equals(deviceDetail.getgBeacon().getFrameTypeString())) {
                viewHolder.beaconPic.setImageResource(R.drawable.url);
            } else if ("UID".equals(deviceDetail.getgBeacon().getFrameTypeString())) {
                viewHolder.beaconPic.setImageResource(R.drawable.uid);
            }
        } else {
            viewHolder.gBeaconItemView.setGBeaconValue(null);
            viewHolder.gBeaconItemView.setVisibility(View.GONE);
        }

        //AltBeacon
        if (null != deviceDetail.getAltBeacon()) {
            viewHolder.altBeaconItemView.setAltBeaconValue(deviceDetail.getAltBeacon());
            viewHolder.altBeaconItemView.setVisibility(View.VISIBLE);
            viewHolder.beaconPic.setImageResource(R.drawable.altbeacon);
        } else {
            viewHolder.altBeaconItemView.setAltBeaconValue(null);
            viewHolder.altBeaconItemView.setVisibility(View.GONE);
        }
        if (deviceRssi <= -100) {
            deviceRssi = -100;
        } else if (deviceRssi > 0) {
            deviceRssi = 0;
        }
        String str_rssi = "(" + deviceRssi + ")";
        if (str_rssi.equals("(-100)")) {
            str_rssi = "null";
        }
        viewHolder.pbRssi.setProgress(100 + deviceRssi);
        viewHolder.tvRssi.setText("RSSI:" + deviceDetail.getRssi().toString());
        return view;
    }


    static class ViewHolder {
        @BindView(R.id.beacon_pic)
        ImageView beaconPic;
        @BindView(R.id.tv_rssi)
        TextView tvRssi;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_addr)
        TextView tvAddr;
        @BindView(R.id.iBeacon_item_view)
        IBeaconItem iBeaconItemView;
        @BindView(R.id.gBeacon_item_view)
        EddystoneBeaconItem gBeaconItemView;
        @BindView(R.id.altBeacon_item_view)
        AltBeaconItem altBeaconItemView;
        @BindView(R.id.pb_rssi)
        ProgressBar pbRssi;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}