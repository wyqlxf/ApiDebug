package com.wyq.api.debug.base

import android.app.Application
import com.wyq.api.debug.request.HttpClientHelper

/**
 * 项目名称：ApiDebug
 * 创建人：WangYongQi
 * 创建时间：2018/12/27 14:49
 * 类描述：应用程序入口
 * 修改人：
 * 修改时间：
 * 修改备注：
 *
 */
class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        application = this
        HttpClientHelper.initHttpClient(this)
    }

    companion object {

        private lateinit var application: Application

        internal val context: Application
            get() {
                return application
            }
    }

}