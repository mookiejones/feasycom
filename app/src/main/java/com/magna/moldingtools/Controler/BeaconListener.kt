package com.magna.moldingtools.Controler


import com.feasycom.bean.BluetoothDeviceWrapper

interface BeaconListener {
    fun onDeviceFound(device: BluetoothDeviceWrapper?, rssi: Int, record: ByteArray?)
}