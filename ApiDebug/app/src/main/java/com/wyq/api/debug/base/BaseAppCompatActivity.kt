package com.wyq.api.debug.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.wyq.api.debug.request.HttpClientHelper

/**
 * 项目名称：ApiDebug
 * 创建人：WangYongQi
 * 创建时间：2018/12/27 11:16
 * 类描述：AppCompatActivity基类
 * 修改人：
 * 修改时间：
 * 修改备注：
 *
 */
open class BaseAppCompatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HttpClientHelper.initHttpClient(this)
    }

}