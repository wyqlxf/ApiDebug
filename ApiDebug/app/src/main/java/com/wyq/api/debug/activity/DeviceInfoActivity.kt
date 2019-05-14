package com.wyq.api.debug.activity

import android.content.Context
import android.os.Bundle
import android.telephony.TelephonyManager
import com.wyq.api.debug.R
import com.wyq.api.debug.base.BaseAppCompatActivity
import com.wyq.api.debug.util.MobileUtil
import com.wyq.api.debug.util.ScreenSize
import kotlinx.android.synthetic.main.activity_device_info.*

/**
 * 项目名称：ApiDebug
 * 创建人：WangYongQi
 * 创建时间：2019/4/2 11:01
 * 类描述：设备信息
 * 修改人：
 * 修改时间：
 * 修改备注：
 *
 */
class DeviceInfoActivity : BaseAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_info)

        ivBack.setOnClickListener {
            finish()
        }

        tvIdCode.text = MobileUtil.soleId
        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        tvImei.text = telephonyManager.deviceId
        tvVersion.text = android.os.Build.VERSION.RELEASE
        tvMode.text = android.os.Build.MODEL
        val width = ScreenSize.getScreenWidth(this)
        val height = ScreenSize.getScreenHeight(this)
        tvScreen.text = "$width x $height"

    }

}