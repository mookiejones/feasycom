package com.magna.moldingtools.ui.beacons

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.feasycom.bean.BluetoothDeviceWrapper
import com.magna.moldingtools.R
import com.magna.moldingtools.inflate
import com.magna.moldingtools.ui.ViewHolder
import kotlinx.android.synthetic.main.sensor_info.view.*
import kotlin.math.roundToLong

class BeaconListAdapter(private val context: Context,
                        private val listener:View.OnClickListener) :RecyclerView.Adapter<ViewHolder>()
{
    private val beacons=ArrayList<BluetoothDeviceWrapper> ()

    /**
     * addDevice
     * @param device [Device] device to add
     */
    fun addDevice(device:BluetoothDeviceWrapper?){
        if(device==null) return
        val index = beacons.indexOfFirst { device.address==it.address }
        if(index != -1){
            beacons[index]=device
            notifyItemChanged(index)
        }else{
            beacons.add(device)
            notifyItemInserted(beacons.size)
        }
    }

    private fun getImage(id:Int):Drawable?=
            ContextCompat.getDrawable(context,id)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val inflatedView = parent.inflate(R.layout.sensor_info,false)
        return ViewHolder(inflatedView)
    }

    private fun getChargeImage(device:BluetoothDeviceWrapper?):Int{
        if(device==null)return -1
        val battery=device.feasyBeacon.battery.toInt()
        val round = (battery/10).toFloat().roundToLong()

        val batteryValue = (round * 10).toInt()

        return when(batteryValue){
            0->R.drawable.electric_quantity0
            10->R.drawable.electric_quantity10
            20->R.drawable.electric_quantity20
            30->R.drawable.electric_quantity30
            40->R.drawable.electric_quantity40
            50->R.drawable.electric_quantity50
            60->R.drawable.electric_quantity60
            70->R.drawable.electric_quantity70
            80->R.drawable.electric_quantity80
            90->R.drawable.electric_quantity90
            100->R.drawable.electric_quantity100
            else->throw Exception("Argument out of range")
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = beacons[position]
        holder.itemView.setOnClickListener(listener)
        with(holder.itemView){
            tv_name.text=item.name
            tv_mac.text=item.address
            tv_rssi.text=item.rssi.toString()
            if(item!=null) {
                charge_value.visibility= View.VISIBLE
                charge_pic.visibility=View.VISIBLE

                charge_value.text = item.feasyBeacon?.battery
                charge_pic.setImageDrawable(getImage(getChargeImage(item)))
            }else{
                charge_value.visibility= View.GONE
                charge_pic.visibility=View.GONE
            }
            tag=item

        }


    }



    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount(): Int=beacons.size




}