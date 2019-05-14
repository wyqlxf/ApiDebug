package com.wyq.api.debug.util

import android.net.Uri
import android.text.TextUtils

/**
 * 项目名称：ApiDebug
 * 创建人：WangYongQi
 * 创建时间：2019/1/15 9:49
 * 类描述：
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
object HostUtil {

    /**
     * 根据传入的URL获取一级域名
     *
     * @param url
     * @return
     */
    fun getDomain(url: String?): String {
        var domain = ""
        url?.let {
            if (it.startsWith("http")) {
                try {
                    val host = Uri.parse(it).host
                    if (!TextUtils.isEmpty(host) && host!!.contains(".")) {
                        domain = host.substring(host.indexOf("."), host.length)
                    }
                } catch (ex: Exception) {
                }
            }
        }
        return domain
    }

}
