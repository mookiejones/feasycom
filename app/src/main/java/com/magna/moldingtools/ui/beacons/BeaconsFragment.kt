package com.magna.moldingtools.ui.beacons
import android.Manifest
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.feasycom.bean.BluetoothDeviceWrapper
import com.feasycom.bean.EddystoneBeacon
import com.feasycom.bean.FeasyBeacon
import com.feasycom.controler.*
import com.feasycom.util.FeasyBeaconUtil
import com.feasycom.util.FeasycomUtil
import com.feasycom.util.ToastUtil
import com.magna.moldingtools.Activity.LaunchActivity
import com.magna.moldingtools.Activity.MainActivity
import com.magna.moldingtools.Activity.SetActivity
import com.magna.moldingtools.Bean.CommandBean
import com.magna.moldingtools.Controler.BeaconListener
import com.magna.moldingtools.Controler.FscBeaconCallbacksImpMain
import com.magna.moldingtools.R
import com.magna.moldingtools.dialog.ChangeNameDialog
import com.magna.moldingtools.Constants
import com.magna.moldingtools.Constants.PERMISSIONS_LOCATION
import kotlinx.android.synthetic.main.change_name_layout.view.*
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList

private const val ENABLE_BT_REQUEST_ID = 1
private const val REQUEST_EXTERNAL_STORAGE = 1
private const val TAG="BeaconsFragment"
class BeaconsFragment: Fragment() {
    private var timerUI: Timer? = null
    private var timerTask: TimerTask? = null
    var deviceQueue: Queue<BluetoothDeviceWrapper> = LinkedList()
    //region Fields
    var beacons =ArrayList<BluetoothDeviceWrapper>()

    private lateinit var api: FscBeaconApi
    var adapter: BeaconListAdapter?=null
    //endregion

    private fun deviceChanged(device: BluetoothDeviceWrapper) {
        adapter?.addDevice(device)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        api = FscBeaconApiImp.getInstance(requireActivity())

    }








    private val listener = object: BeaconListener {
        override fun onDeviceFound(device: BluetoothDeviceWrapper?, rssi: Int, record: ByteArray?) {
            if(device==null)return
            addDevice(device)
        }
    }
    private val onBeaconSelectedListener=object:View.OnClickListener {
        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        override fun onClick(v: View?) {
            if(v==null)return
            val item = v.tag as BluetoothDeviceWrapper




            val encryptionWay = item?.feasyBeacon?.encryptionWay?.substring(1)


            if(encryptionWay==null)
                return

            val eddystoneBeacon:EddystoneBeacon? = item.getgBeacon()
            if(eddystoneBeacon!=null && eddystoneBeacon.frameTypeString=="URL"){
                var uri = Uri.parse(eddystoneBeacon.url)
                val intent = Intent(Intent.ACTION_VIEW,uri)
                startActivity(intent)

            }


            Log.e(TAG,api.isConnected.toString())


            // Need to connect to the beacon to

        requestNewName(item)



        }

    }


    private fun changeDeviceName(item:BluetoothDeviceWrapper,newName:String){



        api.stopScan()
        item.completeLocalName="Molding_Tools_$newName"
        val encryptWay = item.feasyBeacon.encryptionWay
        val moduleString = item.feasyBeacon.module
         feasyBeacon = item.feasyBeacon!!



        val connectable = feasyBeacon!!.isConnectable
        feasyBeacon?.isConnectable=true
        feasyBeacon?.deviceName="Molding_Tools_$newName"

        /**
         * We will be compatible with many modules by moduleString and versionString
         * firmware version
         */
        val versionString=feasyBeacon!!.version

        val connected = api.isConnected
        if(!connected){
            val updateDetermine = FeasyBeaconUtil.updateDetermine(feasyBeacon!!.version,feasyBeacon!!.module)
            Log.e(TAG,updateDetermine.toString())

        }

        val nameListener = object:BeaconCallback(api,feasyBeacon!!,feasyBeacon!!.module){
            override fun blePeripheralFound(device: BluetoothDeviceWrapper?, rssi: Int, record: ByteArray?) {
                if(device!=null &&  device.feasyBeacon!=null){
                    deviceQueue.offer(device)
                }

            }
        }


        api.setCallbacks(nameListener)
        api.setConnectable(true)
        item.feasyBeacon.isConnectable=true
        api.connect(item,Constants.PIN)

        Log.e(TAG,"Stop")
    }
    private fun requestNewName(item:BluetoothDeviceWrapper){
        val currentName = item.name.replace("Molding_Tools_","",true)
        var newName = item.name
        val editView = LayoutInflater.from(requireContext()).inflate(R.layout.change_name_layout,null)
        editView.txtNameChange.apply {
            setText(currentName)
            doAfterTextChanged { newName=it.toString() }
        }





        val dialog= AlertDialog.Builder(requireContext())
                .setTitle(R.string.change_name)
                .setView(editView)
                .setPositiveButton(android.R.string.ok){dialog,id->
                    dialog.dismiss()
                    changeDeviceName(item,newName)
                }
                .setNegativeButton(android.R.string.cancel){dialog,id->dialog.cancel()}
                .create()
        dialog.show()
    }

    fun addDevice(device:BluetoothDeviceWrapper?){
        if(device==null)return


        //todo Need to check client mac and not name
        val index = beacons.indexOfFirst { it.address==device.address }
        if(index==-1){
            beacons.add(device)
        }else{
            beacons[index]=device
        }

        deviceChanged(device)
    }


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View{
        val view = inflater.inflate(R.layout.fragment_beacons,container,false)

        val itemList = view.findViewById<RecyclerView>(R.id.item_list)

        adapter= BeaconListAdapter(requireContext(),onBeaconSelectedListener)
        itemList.layoutManager=LinearLayoutManager(requireContext())
        itemList.adapter=adapter
        return view
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
//        api.stopScan()
    }
    private var feasyBeacon:FeasyBeacon?=null
    private val beaconListener = object: FscBeaconCallbacksImp() {
        override fun blePeripheralFound(device: BluetoothDeviceWrapper?, rssi: Int, record: ByteArray?) {
            if(device!=null &&  device.feasyBeacon!=null){
                deviceQueue.offer(device)
            }

        }

        override fun blePeripheralConnected(gatt: BluetoothGatt?, device: BluetoothDevice?) {
//            if(feasyBeacon!=null)
//            api.startGetDeviceInfo(feasyBeacon!!.module,feasyBeacon)
        }

        override fun connectProgressUpdate(device: BluetoothDevice?, status: Int) {
//             super.connectProgressUpdate(device, status)
            when(status){
                CommandBean.PASSWORD_CHECK->writeText("Password Check")
                CommandBean.PASSWORD_SUCCESSFULE->{
                    writeText("Password Successful")

                    api.startGetDeviceInfo(feasyBeacon!!.module,feasyBeacon!! )
                }
                CommandBean.PASSWORD_FAILED->writeText("Password Failed")
                CommandBean.PASSWORD_TIME_OUT->writeText("Password Time Out")
                else->{
                    Log.d(TAG,"Stop")
                }
            }

        }

        override fun blePeripheralDisonnected(gatt: BluetoothGatt?, device: BluetoothDevice?) {
            super.blePeripheralDisonnected(gatt, device)
            writeText("Disconnected")
        }

        override fun atCommandCallBack(command: String?, param: String?, status: String?) {
            super.atCommandCallBack(command, param, status)
            when(status){
                CommandBean.COMMAND_FINISH->writeText("CommandFinish")
                CommandBean.COMMAND_TIME_OUT->writeText("COMMAND_TIME_OUT")
                CommandBean.COMMAND_SUCCESSFUL->writeText("COMMAND_SUCCESSFUL")
                CommandBean.COMMAND_NO_NEED->writeText("COMMAND_NO_NEED")
            }
        }

        override fun deviceInfo(parameterName: String?, parameter: Any?) {
            super.deviceInfo(parameterName, parameter)
        }
    }
    private fun writeText(value:String){
        activity?.runOnUiThread {
            Toast.makeText(requireActivity(),value,Toast.LENGTH_SHORT).show()
        }
//                ToastUtil.show(requireContext(),value)


    }
    override fun onResume() {
        super.onResume()
       api = FscBeaconApiImp.getInstance()
        api.initialize()
        if(!api.checkBleHardwareAvailable()) {
            bleMissing()
            return
        }
        /**
         * on every Resume check if BT is enabled (user could turn it off while app was in background etc.)
         */
        if(!api.isBtEnabled){
            /**
             * BT is not turned on - ask user to make it enabled
             */
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent,ENABLE_BT_REQUEST_ID)
        }
        /**
         * Check if we have write permission
         */
        val permission = ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
        if(permission!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(),
            PERMISSIONS_LOCATION,
            REQUEST_EXTERNAL_STORAGE)
        }else{

            api.setCallbacks(beaconListener)
            if(SetActivity.SCAN_FIXED_TIME){
                api.startScan(60000)
            }else{
                api.startScan(0)
            }
        }

        timerUI = Timer()
        timerTask = UITimerTask(WeakReference<BeaconsFragment>(this ))
        timerUI!!.schedule(timerTask, 100, 100)
    }


    internal inner class UITimerTask(private val weakReference: WeakReference<BeaconsFragment>):TimerTask(){
        override fun run() {

             weakReference.get()?.activity?.runOnUiThread {
                 weakReference.get()?.adapter?.addDevice(weakReference.get()?.deviceQueue?.poll())
             }
        }
    }
    /**
     * bluetooth is not turned on
     */
    private fun btDisabled() {
        Toast.makeText(requireContext(), "Sorry, BT has to be turned ON for us to work!", Toast.LENGTH_LONG).show()
        val launch = activity as LaunchActivity
        launch.finishActivity()
    }

    /**
     * does not support BLE
     */
    private fun bleMissing() {
        Toast.makeText(requireContext(), "BLE Hardware is required but not available!", Toast.LENGTH_LONG).show()
        val launch = activity as LaunchActivity
        launch.finishActivity()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == ENABLE_BT_REQUEST_ID){
            btDisabled()
        }
    }
}