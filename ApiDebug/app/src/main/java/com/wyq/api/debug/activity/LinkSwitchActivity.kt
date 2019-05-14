package com.wyq.api.debug.activity

import android.os.Bundle
import com.wyq.api.debug.R
import com.wyq.api.debug.base.BaseAppCompatActivity
import com.wyq.library.util.SPUtil
import kotlinx.android.synthetic.main.activity_link_switch.*

/**
 * 项目名称：ApiDebug
 * 创建人：WangYongQi
 * 创建时间：2019/1/6 15:25
 * 类描述：环境切换界面
 * 修改人：
 * 修改时间：
 * 修改备注：
 *
 */
class LinkSwitchActivity : BaseAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_link_switch)
        init()
        click()
    }


    /**
     * 创建人：WangYongQi
     * 创建时间：2019/1/6 14:45
     * 方法描述：初始化
     **/
    private fun init() {
        restore()
        val flag = SPUtil.instance.getString("host_flag", "0")
        when (flag) {
            "0" -> {
                checkBox1.isChecked = true
            }
            "1" -> {
                checkBox2.isChecked = true
            }
            "2" -> {
                checkBox3.isChecked = true
            }
            "3" -> {
                checkBox4.isChecked = true
            }
        }
    }

    /**
     * 创建人：WangYongQi
     * 创建时间：2019/1/6 14:49
     * 方法描述：按钮点击事件
     **/
    private fun click() {
        ivBack.setOnClickListener {
            finish()
        }

        checkBox1.setOnClickListener {
            restore()
            checkBox1.isChecked = true
            setValidityPeriod("0")
        }

        checkBox2.setOnClickListener {
            restore()
            checkBox2.isChecked = true
            setValidityPeriod("1")
        }

        checkBox3.setOnClickListener {
            restore()
            checkBox3.isChecked = true
            setValidityPeriod("2")
        }

        checkBox4.setOnClickListener {
            restore()
            checkBox4.isChecked = true
            setValidityPeriod("3")
        }
    }

    /**
     * 创建人：WangYongQi
     * 创建时间：2019/1/6 14:40
     * 方法描述：设置标志
     **/
    private fun setValidityPeriod(flag: String) {
        SPUtil.instance.setString("host_flag", flag)
    }

    /**
     * 创建人：WangYongQi
     * 创建时间：2019/1/6 14:16
     * 方法描述：还原恢复
     **/
    private fun restore() {
        checkBox1.isChecked = false
        checkBox2.isChecked = false
        checkBox3.isChecked = false
        checkBox4.isChecked = false
    }

}