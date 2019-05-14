package com.wyq.api.debug.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.net.Uri
import android.widget.Toast
import com.wyq.api.debug.base.BaseApplication
import com.wyq.api.debug.config.Config
import java.util.*

/**
 * 项目名称：ApiDebug
 * 创建人：WangYongQi
 * 创建时间：2019/1/14 18:08
 * 类描述：
 * 修改人：
 * 修改时间：
 * 修改备注：
 *
 */
object AppUtil {

    /**
     * 根据包名，判断app是否已安装
     * @param context
     * @param packageName
     * @return
     */
    private fun isAppInstalled(context: Context, packageName: String): Boolean {
        return try {
            val list = ArrayList<String>()
            val infoList = context.packageManager.getInstalledPackages(0)
            infoList?.let {
                for (i in it.indices) {
                    val pn = it[i].packageName
                    list.add(pn)
                }
            }
            list.contains(packageName)
        } catch (ex: Exception) {
            false
        }
    }

    /**
     * 跳转到主app
     * @param context
     * @return
     */
    fun jumpMainApp(context: Context?) {
        context?.let {
            // 先判断用户是否安裝了主app
            if (isAppInstalled(it, Config.mainAppPackageName)) {
                try {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setClassName(Config.mainAppPackageName, Config.mainAppClassName)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    it.startActivity(intent)
                } catch (ex: Exception) {
                }
            } else {
                val intent = Intent()
                intent.action = Intent.ACTION_VIEW
                intent.data = Uri.parse(Config.mainAppDownloadUrl)
                it.startActivity(intent)
                Toast.makeText(BaseApplication.context, "检测到未安装应用程式，请先下载并安装应用程式", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 根据包名，判断app是否有效
     * @param context
     * @param packageName
     * @return
     */
    fun isAppAvailable(context: Context?, packageName: String): Boolean {
        var packageInfo: PackageInfo?
        try {
            packageInfo = context?.packageManager?.getPackageInfo(packageName, 0)
        } catch (e: Exception) {
            packageInfo = null
            e.printStackTrace()
        }
        return packageInfo != null
    }

}