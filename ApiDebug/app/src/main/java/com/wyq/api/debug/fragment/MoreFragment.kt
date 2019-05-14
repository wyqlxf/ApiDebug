package com.wyq.api.debug.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.wyq.api.debug.R
import com.wyq.api.debug.activity.*
import com.wyq.api.debug.config.Config

/**
 * 项目名称：ApiDebug
 * 创建人：WangYongQi
 * 创建时间：2019/1/3 14:40
 * 类描述：更多界面
 * 修改人：
 * 修改时间：
 * 修改备注：
 *
 */
class MoreFragment : Fragment() {

    companion object {

        private var instance: MoreFragment? = null

        fun getInstance(): MoreFragment {
            if (instance == null) {
                instance = MoreFragment()
            }
            return instance!!
        }

    }

    private var rootView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_more, container, false)
            // 模拟请求
            val llSimulationRequest = rootView?.findViewById<LinearLayout>(R.id.llSimulationRequest)
            llSimulationRequest?.setOnClickListener {
                val intent = Intent()
                intent.putExtra("name", "模拟请求")
                intent.putExtra("type", "Get")
                intent.putExtra("url", Config.defaultAddressUrl)
                intent.putExtra("parameter", "")
                intent.setClass(context, PostmanActivity::class.java)
                startActivity(intent)
            }
            // 接口过滤
            val llApiFilter = rootView?.findViewById<LinearLayout>(R.id.llApiFilter)
            llApiFilter?.setOnClickListener {
                startActivity(Intent(context, ApiFilterActivity::class.java))
            }
            // 实时期效
            val llValidityPeriod = rootView?.findViewById<LinearLayout>(R.id.llValidityPeriod)
            llValidityPeriod?.setOnClickListener {
                startActivity(Intent(context, ValidityPeriodActivity::class.java))
            }
            // 环境切换
            val llLinkSwitch = rootView?.findViewById<LinearLayout>(R.id.llLinkSwitch)
            llLinkSwitch?.setOnClickListener {
                startActivity(Intent(context, LinkSwitchActivity::class.java))
            }
            // 触屏dev调试
            val llWebDebug = rootView?.findViewById<LinearLayout>(R.id.llWebDebug)
            llWebDebug?.setOnClickListener {
                startActivity(Intent(context, WebSettingActivity::class.java))
            }
            // 设备信息
            val llDeviceInfo = rootView?.findViewById<LinearLayout>(R.id.llDeviceInfo)
            llDeviceInfo?.setOnClickListener {
                startActivity(Intent(context, DeviceInfoActivity::class.java))
            }
            // 应用下载
            val llDownload = rootView?.findViewById<LinearLayout>(R.id.llDownload)
            llDownload?.setOnClickListener {
                val intent = Intent()
                intent.action = Intent.ACTION_VIEW
                intent.data = Uri.parse(Config.mainAppDownloadUrl)
                startActivity(intent)
            }
            // 关于我们
            val llBoutWe = rootView?.findViewById<LinearLayout>(R.id.llBoutWe)
            llBoutWe?.setOnClickListener {
                startActivity(Intent(context, AboutWeActivity::class.java))
            }
        }
        return rootView
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }

}