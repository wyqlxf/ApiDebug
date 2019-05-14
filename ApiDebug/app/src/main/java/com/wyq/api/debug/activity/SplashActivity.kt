package com.wyq.api.debug.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.KeyEvent
import android.view.Window
import android.view.WindowManager
import com.wyq.api.debug.R
import com.wyq.api.debug.entity.ApiLogList
import kotlinx.android.synthetic.main.activity_splash.*

/**
 * 项目名称：ApiDebug
 * 创建人：WangYongQi
 * 创建时间：2018/12/27 11:14
 * 类描述：启动页
 * 修改人：
 * 修改时间：
 * 修改备注：
 *
 */
class SplashActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash)
        // 加载数据
        Thread(Runnable {
            ApiLogList.instance.loadLogData()
        }).start()
        mCountDownTimer?.start()

        btnJump.setOnClickListener {
            mCountDownTimer?.cancel()
            mCountDownTimer = null
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }
    }

    private var mCountDownTimer: CountDownTimer? = object : CountDownTimer(2000, 100) {
        override fun onTick(l: Long) {
            // 定期间隔触发回调
            when {
                l > 1800 -> tvContent.text = "让              "
                l > 1600 -> tvContent.text = "让 开           "
                l > 1400 -> tvContent.text = "让 开 发        "
                l > 1200 -> tvContent.text = "让 开 发 更     "
                l > 1000 -> tvContent.text = "让 开 发 更 便  "
                else -> tvContent.text = "让 开 发 更 便 捷"
            }
        }

        override fun onFinish() {
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK) {
            if (event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0) {
                // 取消倒计时
                mCountDownTimer?.cancel()
                mCountDownTimer = null
                finish()
            }
            return true
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onDestroy() {
        super.onDestroy()
        // 取消倒计时
        mCountDownTimer?.cancel()
        mCountDownTimer = null
    }

}