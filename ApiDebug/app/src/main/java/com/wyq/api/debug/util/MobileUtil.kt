package com.wyq.api.debug.util

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import com.wyq.api.debug.base.BaseApplication
import com.wyq.library.util.SPUtil
import java.util.*

/**
 * 项目名称：ApiDebug
 * 创建人：WangYongQi
 * 创建时间：2019/1/10 8:52
 * 类描述：设备号工具类
 * 修改人：
 * 修改时间：
 * 修改备注：
 *
 */
object MobileUtil {

    private val uuid: String
        get() {
            var serial = "unknown"
            if (Build.VERSION.SDK_INT >= 9) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (ContextCompat.checkSelfPermission(BaseApplication.context,
                                    Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                        serial = Build.getSerial()
                    } else {
                        try {
                            serial = Build::class.java.getField("SERIAL").get(null)
                                    .toString()
                        } catch (ex: Exception) {
                        }

                    }
                } else {
                    serial = Build.SERIAL
                }
            }
            var idShort = ""
            try {
                idShort = ("35" + Build.BOARD.length % 10
                        + Build.BRAND.length % 10 + Build.CPU_ABI.length % 10
                        + Build.DEVICE.length % 10 + Build.DISPLAY.length % 10
                        + Build.HOST.length % 10 + Build.ID.length % 10
                        + Build.MANUFACTURER.length % 10 + Build.MODEL.length % 10 + Build.PRODUCT.length % 10 + Build.TAGS.length % 10 + Build.TYPE.length % 10 + Build.USER.length % 10)
            } catch (ex: Exception) {
            }

            val uuid: String
            uuid = if (TextUtils.isEmpty(serial) || serial == "null" || serial == "unknown" ||
                    TextUtils.isEmpty(idShort)) {
                "1" + UUID.randomUUID().toString()
            } else {
                "2" + UUID(idShort.hashCode().toLong(), serial.hashCode().toLong()).toString()
            }
            return uuid
        }

    val soleId: String
        get() {
            var soleId = SPUtil.instance.getString("sole_id", "")
            if (!TextUtils.isEmpty(soleId) && soleId != "null") {
                return soleId
            }
            soleId = uuid
            if (!TextUtils.isEmpty(soleId) && soleId != "null") {
                SPUtil.instance.setString("sole_id", soleId)
                return soleId
            }
            return "android_unknown"
        }

}
