package com.wyq.api.debug.activity

import android.os.Bundle
import com.wyq.api.debug.R
import com.wyq.api.debug.base.BaseAppCompatActivity
import kotlinx.android.synthetic.main.activity_about_we.*

/**
 * 项目名称：ApiDebug
 * 创建人：WangYongQi
 * 创建时间：2019/1/4 14:28
 * 类描述：关于我们
 * 修改人：
 * 修改时间：
 * 修改备注：
 *
 */
class AboutWeActivity : BaseAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_we)

        ivBack.setOnClickListener {
            finish()
        }

        webView.loadUrl("file:///android_asset/about.html")
    }

}