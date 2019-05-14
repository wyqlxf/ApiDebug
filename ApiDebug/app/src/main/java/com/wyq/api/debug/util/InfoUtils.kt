package com.wyq.api.debug.util

import android.content.pm.PackageInfo
import android.os.Build
import android.util.DisplayMetrics
import com.wyq.api.debug.base.BaseApplication
import java.io.Serializable

/**
 * 项目名称：ApiDebug
 * 创建人：WangYongQi
 * 创建时间：2019/1/10 8:52
 * 类描述：
 * 修改人：
 * 修改时间：
 * 修改备注：
 *
 */
class InfoUtils : Serializable {

    private var mPackageInfo: PackageInfo? = null
    private var mDisplayMetrics: DisplayMetrics? = null

    companion object {

        private var instance: InfoUtils? = null

        @Synchronized
        fun getInstance(): InfoUtils {
            if (instance == null) {
                instance = InfoUtils()
            }
            return instance!!
        }
    }

    constructor() {
        mDisplayMetrics = BaseApplication.context.resources.displayMetrics
        try {
            mPackageInfo = BaseApplication.context.packageManager.getPackageInfo(BaseApplication.context.packageName, 0)
        } catch (e: Exception) {
        }
    }

    private fun getSize(): String {
        return if (mDisplayMetrics != null) {
            (this.mDisplayMetrics!!.widthPixels.toString() + "x"
                    + this.mDisplayMetrics!!.heightPixels)
        } else {
            "0x0"
        }
    }

    fun getUserAgent(): String {
        val builder = StringBuilder()
        if (mPackageInfo != null) {
            builder.append('/'.toString() + mPackageInfo!!.versionName + "."
                    + mPackageInfo!!.versionCode)
        } else {
            builder.append('/' + "2.20.6.93")
        }
        builder.append("/" + getSize())
        builder.append("/Android")
        builder.append("/" + Build.VERSION.RELEASE)
        builder.append("/" + Build.MODEL)
        builder.append("/" + MobileUtil.soleId)
        return builder.toString()
    }

}