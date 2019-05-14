package com.wyq.api.debug.activity

import android.content.Intent
import android.os.Bundle
import com.wyq.api.debug.R
import com.wyq.api.debug.base.BaseAppCompatActivity
import com.wyq.api.debug.config.Config
import kotlinx.android.synthetic.main.activity_web_setting.*

class WebSettingActivity : BaseAppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_setting)

        et_url.setText(Config.webDevUrl)
        et_url.setSelection(Config.webDevUrl.length)

        iv_back.setOnClickListener {
            finish()
        }

        btn_request.setOnClickListener {
            val url = et_url.text.toString()
            val intent = Intent()
            val bundle = Bundle()
            bundle.putString("url", url)
            bundle.putString("title", "")
            bundle.putString("filter_keyword", "")
            bundle.putString("java_script_interface_name", "webkit")
            bundle.putBoolean("is_show_head", false)
            bundle.putInt("mode", 1)
            intent.putExtras(bundle)
            intent.setClass(this@WebSettingActivity, CommonBrowserActivity::class.java)
            startActivity(intent)
        }
    }

}