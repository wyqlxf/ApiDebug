package com.wyq.api.debug.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.wyq.api.debug.R
import com.wyq.api.debug.base.BaseAppCompatActivity
import com.wyq.api.debug.entity.Api
import kotlinx.android.synthetic.main.activity_field_detail.*

/**
 * 项目名称：ApiDebug
 * 创建人：WangYongQi
 * 创建时间：2019/1/15 14:02
 * 类描述：字段详情界面
 * 修改人：
 * 修改时间：
 * 修改备注：
 *
 */
class FieldDetailActivity : BaseAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_field_detail)
        // 获取Api对象
        val api = intent.extras.getSerializable("api") as Api
        // 设置标题
        tvTitle.text = api.name
        // 接口地址
        tvUrl.text = api.url
        tvUrl.setOnClickListener {
            val intent = Intent()
            intent.putExtra("name", api.name)
            intent.putExtra("type", api.type)
            intent.putExtra("url", api.url)
            intent.putExtra("parameter", api.parameter)
            intent.putExtra("time", api.time)
            intent.putExtra("description", api.description)
            intent.setClass(this@FieldDetailActivity, ApiDetailActivity::class.java)
            startActivity(intent)
        }
        // 接口类型
        tvType.text = api.type
        // 接口参数
        if (!TextUtils.isEmpty(api.parameter)) {
            tvParameter.text = api.parameter
            llParameter.visibility = View.VISIBLE
        } else {
            llParameter.visibility = View.GONE
        }
        // 接口描述
        tvDescription.text = api.description

        // 返回按钮
        ivBack.setOnClickListener {
            finish()
        }

        // 字段说明
        llFiledDetail.removeAllViews()
        for (i in 0 until api.filedDetail.size) {
            val value = api.filedDetail[i]
            if (!TextUtils.isEmpty(value)) {
                val textView = TextView(this)
                textView.text = value
                textView.textSize = 14f
                textView.setTextIsSelectable(true)
                textView.setTextColor(Color.parseColor("#222222"))
                textView.setPadding(0, 0, 0, resources.getDimension(R.dimen.width22px).toInt())
                llFiledDetail.addView(textView)
            }
        }
    }

}