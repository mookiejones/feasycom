package com.magna.moldingtools.ui.beacons

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.util.Log
import com.feasycom.bean.FeasyBeacon
import com.feasycom.controler.FscBeaconApi
import com.feasycom.controler.FscBeaconCallbacksImp
import com.magna.moldingtools.Bean.CommandBean

private const val TAG="BeaconCallback"
open class BeaconCallback (private val feasyBeaconApi: FscBeaconApi,private val feasyBeacon: FeasyBeacon,private val moduleString:String): FscBeaconCallbacksImp()
{

    override fun blePeripheralConnected(gatt: BluetoothGatt?, device: BluetoothDevice?) {

        val name = feasyBeacon.deviceName
      Log.d(TAG,name)
    }

    override fun connectProgressUpdate(device: BluetoothDevice?, status: Int) {

      when(status){
          CommandBean.PASSWORD_CHECK-> Log.e(TAG,"PASSWORD_CHECK")
          CommandBean.PASSWORD_SUCCESSFULE->{
              Log.e(TAG,"PASSWORD_SUCCESSFULE")
              feasyBeaconApi.startGetDeviceInfo(moduleString,feasyBeacon)

          }
          CommandBean.PASSWORD_FAILED-> Log.e(TAG,"PASSWORD_FAILED")
          CommandBean.PASSWORD_TIME_OUT-> Log.e(TAG,"PASSWORD_TIME_OUT")
          else->{
              Log.e(TAG,"Stop")
          }
      }
    }



    override fun readResponse(gatt: BluetoothGatt?, device: BluetoothDevice?, service: BluetoothGattService?, ch: BluetoothGattCharacteristic?, strValue: String?, hexString: String?, rawValue: ByteArray?, timestamp: String?) {
        super.readResponse(gatt, device, service, ch, strValue, hexString, rawValue, timestamp)
    }
    override fun blePeripheralDisonnected(gatt: BluetoothGatt?, device: BluetoothDevice?) {
        super.blePeripheralDisonnected(gatt, device)
    }

    override fun atCommandCallBack(command: String?, param: String?, status: String?) {
        super.atCommandCallBack(command, param, status)
    }

    override fun deviceInfo(parameterName: String?, parameter: Any?) {
        super.deviceInfo(parameterName, parameter)
    }

    override fun packetReceived(gatt: BluetoothGatt?, device: BluetoothDevice?, service: BluetoothGattService?, ch: BluetoothGattCharacteristic?, strValue: String?, hexString: String?, rawValue: ByteArray?, timestamp: String?) {
        super.packetReceived(gatt, device, service, ch, strValue, hexString, rawValue, timestamp)
    }


}