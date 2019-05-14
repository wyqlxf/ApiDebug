package com.wyq.api.debug.activity

import android.os.Bundle
import com.wyq.api.debug.R
import com.wyq.api.debug.base.BaseAppCompatActivity
import com.wyq.library.util.SPUtil
import kotlinx.android.synthetic.main.activity_validity_period.*

/**
 * 项目名称：ApiDebug
 * 创建人：WangYongQi
 * 创建时间：2019/1/6 11:21
 * 类描述：实时期效界面
 * 修改人：
 * 修改时间：
 * 修改备注：
 *
 */
class ValidityPeriodActivity : BaseAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_validity_period)
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
        val time = SPUtil.instance.getLong("validity_period_time", 1000 * 60 * 60 * 2)
        when (time) {
            1000 * 60 * 3L -> {
                checkBox1.isChecked = true
            }
            1000 * 60 * 30L -> {
                checkBox2.isChecked = true
            }
            1000 * 60 * 60L -> {
                checkBox3.isChecked = true
            }
            1000 * 60 * 60 * 2L -> {
                checkBox4.isChecked = true
            }
            1000 * 60 * 60 * 12L -> {
                checkBox5.isChecked = true
            }
            1000 * 60 * 60 * 24 * 7L -> {
                checkBox6.isChecked = true
            }
            0L -> {
                checkBox7.isChecked = true
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
            setValidityPeriod(1000 * 60 * 3)
        }

        checkBox2.setOnClickListener {
            restore()
            checkBox2.isChecked = true
            setValidityPeriod(1000 * 60 * 30)
        }

        checkBox3.setOnClickListener {
            restore()
            checkBox3.isChecked = true
            setValidityPeriod(1000 * 60 * 60)
        }

        checkBox4.setOnClickListener {
            restore()
            checkBox4.isChecked = true
            setValidityPeriod(1000 * 60 * 60 * 2)
        }

        checkBox5.setOnClickListener {
            restore()
            checkBox5.isChecked = true
            setValidityPeriod(1000 * 60 * 60 * 12)
        }

        checkBox6.setOnClickListener {
            restore()
            checkBox6.isChecked = true
            setValidityPeriod(1000 * 60 * 60 * 24 * 7)
        }

        checkBox7.setOnClickListener {
            restore()
            checkBox7.isChecked = true
            setValidityPeriod(0)
        }
    }

    /**
     * 创建人：WangYongQi
     * 创建时间：2019/1/6 14:40
     * 方法描述：设置有效期
     **/
    private fun setValidityPeriod(time: Long) {
        SPUtil.instance.setLong("validity_period_time", time)
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
        checkBox5.isChecked = false
        checkBox6.isChecked = false
        checkBox7.isChecked = false
    }

}