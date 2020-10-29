package com.magna.moldingtools.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.KeyEvent
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.magna.moldingtools.R
import com.magna.moldingtools.Widget.AboutUsDialog
import com.magna.moldingtools.Widget.QRDialog

/**
 * Copyright 2017 Shenzhen Feasycom Technology co.,Ltd
 */
private const val TAG="AboutActivity"
class AboutActivity : BaseActivity() {
    @JvmField
    @BindView(R.id.more)
    var more: TextView? = null

    @JvmField
    @BindView(R.id.qr)
    var qr: LinearLayout? = null

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
    @BindView(R.id.aboutUsTV)
    var aboutUsTV: TextView? = null

    @JvmField
    @BindView(R.id.Sensor_Button)
    var SensorButton: ImageView? = null
    override val tag: String?=TAG

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        ButterKnife.bind(this)
        aboutUsTV!!.text = Html.fromHtml("""
    <p><b>Feasycom</b> focus on the researching and developing of IoT (internet of things) products, including Bluetooth Modules ,WiFi and LoRa Modules,Bluetooth Beacon,etc. With more than 10-year experiences in the wireless connectivity, which ensure us have the capability for providing low-risk product development, reducing system integration cost and shortening product customization cycle to thousands of diverse customer worldwide.</p>
    
    <p>&nbsp</p>
    <p>Feasycom’s engineering and design services include:</p>
    
    <p>&nbsp SDK</p>
    <p>&nbsp APP Support</p>
    <p>&nbsp PCB Design</p>
    <p>&nbsp Development Board</p>
    <p>&nbsp Firmware Development</p>
    <p>&nbsp Depth Customization</p>
    <p>&nbsp Certification Request</p>
    <p>&nbsp Turn-Key Production Testing & Manufacturing</p>
    <p>&nbsp</p>
    <p>Our products and services mainly apply to Automotive, Point of Sale, Home Automation, Healthcare and Engineering, Banking, Computing, Vending Business, Location, Lighting and more.</p>
    <p>&nbsp</p>
    <p>Aiming at <b><i>&quot Make Communication Easy and Freely &quot</b></i>, Feasycom is dedicated to design and develop high-quality products, efficient services to customers, for today, and all days to come.</p>
    
    """.trimIndent()
        )
    }

    override fun onResume() {
        super.onResume()
    }

    override fun initView() {}
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.repeatCount == 0) {
            android.util.Log.e(TAG, "onKeyDown: ")
            SetActivity.actionStart(this)
            finishActivity()
        }
        return true
    }

    @OnClick(R.id.qr)
    fun qrClick() {
        QRDialog(this).show()
    }

    @OnClick(R.id.Set_Button)
    override fun setClick() {
        SetActivity.actionStart(this)
        this!!.finish()
    }

    @OnClick(R.id.About_Button)
    fun aboutClick() {
    }

    @OnClick(R.id.Search_Button)
    fun searchClick() {
        MainActivity.actionStart(this)
        this.finish()
    }

    @OnClick(R.id.Sensor_Button)
    fun sensorClick() {
        SensorActivity.actionStart(this)
        this.finish()
    }

    @OnClick(R.id.more)
    fun onViewClicked() {
        AboutUsDialog(this).show()
    }

    companion object {


        @JvmStatic
        fun actionStart(context: Context) {
            val intent = Intent(context, AboutActivity::class.java)
            context.startActivity(intent)
        }
    }
}