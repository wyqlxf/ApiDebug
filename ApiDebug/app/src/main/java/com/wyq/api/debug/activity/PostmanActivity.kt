package com.wyq.api.debug.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.wyq.api.debug.R
import com.wyq.api.debug.base.BaseAppCompatActivity
import com.wyq.api.debug.base.BaseApplication

/**
 * 创建人： WangYongQi
 * 创建时间：2018/7/26.
 * 文件说明：Postman接口请求类
 */
class PostmanActivity : BaseAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_postman)
        val name = intent.getStringExtra("name") + ""
        var type = intent.getStringExtra("type") + ""
        val url = intent.getStringExtra("url") + ""
        var parameter = intent.getStringExtra("parameter") + ""

        // 返回按钮
        val ivBack = findViewById<ImageView>(R.id.iv_back)
        ivBack.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(ivBack.applicationWindowToken, 0)
            finish()
        }

        // Url控件
        val etUrl = findViewById<EditText>(R.id.et_url)
        if (url != null && url != "null") {
            etUrl.setText(url)
        }
        etUrl.requestFocus()

        // 参数大布局
        val layoutParameter = findViewById<LinearLayout>(R.id.layout_parameter)

        // 类型选择控件
        val rbGet = findViewById<RadioButton>(R.id.rb_get)
        val rbPost = findViewById<RadioButton>(R.id.rb_post)
        if (type.equals("POST", ignoreCase = true)) {
            rbGet.isChecked = false
            rbPost.isChecked = true
            layoutParameter.visibility = View.VISIBLE
            parameter = parameter.replace("{", "")
                    .replace("}", "")
                    .replace(" ", "")
            val textArray = parameter.split(",")
            for (str in textArray) {
                if (str.contains("=")) {
                    val key = str.subSequence(0, str.indexOf("="))
                    val value = str.subSequence(str.indexOf("=") + 1, str.length)
                    addLayoutParameter(layoutParameter, key.toString(), value.toString(), true)
                }
            }
            addLayoutParameter(layoutParameter, "", "", false)
        } else {
            rbGet.isChecked = true
            rbPost.isChecked = false
            layoutParameter.visibility = View.GONE
            addLayoutParameter(layoutParameter, "", "", false)
        }
        rbGet.setOnClickListener {
            rbGet.isChecked = true
            rbPost.isChecked = false
            layoutParameter.visibility = View.GONE
            type = "Get"
        }
        rbPost.setOnClickListener {
            rbGet.isChecked = false
            rbPost.isChecked = true
            layoutParameter.visibility = View.VISIBLE
            type = "Post"
        }

        // 开始请求按钮
        val btnRequest = findViewById<Button>(R.id.btn_request)
        btnRequest.visibility = View.VISIBLE
        btnRequest.setOnClickListener {
            val url = etUrl.text.toString()
            if (!TextUtils.isEmpty(url)) {
                val intent = Intent()
                intent.putExtra("name", name)
                intent.putExtra("type", type)
                intent.putExtra("url", url)
                intent.putExtra("parameter", getPostParameter(layoutParameter))
                intent.putExtra("page", "postman")
                intent.setClass(this@PostmanActivity, ApiDetailActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(BaseApplication.context, "请先输入接口参数!", Toast.LENGTH_SHORT).show()
            }
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(ivBack.applicationWindowToken, 0)
        }
    }

    /**
     * 添加布局参数
     */
    private fun addLayoutParameter(layoutParameter: LinearLayout, key: String, value: String, flag: Boolean) {
        val view = LayoutInflater.from(this).inflate(R.layout.item_postman_parameter, null)
        val etLeft = view.findViewById<EditText>(R.id.et_left)
        etLeft.setText(key)
        val etRight = view.findViewById<EditText>(R.id.et_right)
        etRight.setText(value)
        val ivRight = view.findViewById<ImageView>(R.id.iv_right)
        if (flag) {
            ivRight.tag = "cancel"
            ivRight.setImageResource(R.drawable.ic_cancel_black_24dp)
        } else {
            ivRight.tag = "add"
            ivRight.setImageResource(R.drawable.ic_add_circle_black_24dp)
        }
        ivRight.setOnClickListener {
            if (ivRight.tag == "add") {
                ivRight.tag = "cancel"
                ivRight.setImageResource(R.drawable.ic_cancel_black_24dp)
                addLayoutParameter(layoutParameter, "", "", false)
            } else {
                layoutParameter.removeView(view)
            }
        }
        layoutParameter.addView(view)
    }

    /**
     * 获取Post参数
     */
    private fun getPostParameter(layoutParameter: LinearLayout): String {
        var buffer = StringBuffer()
        val count = layoutParameter.childCount
        for (i: Int in 0 until count) {
            val view = layoutParameter.getChildAt(i)
            if (view is LinearLayout) {
                val childCount = view.childCount
                var key = ""
                var value = ""
                for (j: Int in 0 until childCount) {
                    var childView = view.getChildAt(j)
                    if (childView is EditText) {
                        if (childView.id == R.id.et_left) {
                            key = childView.text.toString()
                        } else if (childView.id == R.id.et_right) {
                            value = childView.text.toString()
                        }
                    }
                }
                if (!TextUtils.isEmpty(key)) {
                    buffer.append("$key=$value")
                    buffer.append("#")
                }
            }
        }
        return buffer.toString()
    }

}