package com.wyq.api.debug.entity

import android.annotation.SuppressLint
import android.os.Environment
import android.text.TextUtils
import com.addcn.android.hk591new.util.AesEncryptUtil
import com.wyq.api.debug.config.Config
import com.wyq.api.debug.database.FilterDbHelper
import com.wyq.api.debug.util.SortComparatorUtil
import com.wyq.library.util.SPUtil
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * 创建人： WangYongQi
 * 创建时间：2018/7/16.
 * 文件说明：Api数据集合
 */

class ApiLogList {

    private constructor() {
        // 加载日志数据
        Thread(Runnable {
            loadLogData()
        }).start()
    }

    companion object {

        private var mInstance: ApiLogList? = null

        val instance: ApiLogList
            get() {
                if (this.mInstance == null) {
                    this.mInstance = ApiLogList()
                }
                return mInstance!!
            }
    }

    // 日志API数据集合
    private lateinit var logList: MutableList<Api>

    fun getLogList(): MutableList<Api> {
        return logList
    }

    // 加载日志数据
    @SuppressLint("SimpleDateFormat")
    fun loadLogData() {
        synchronized(this) {
            logList = ArrayList()
            val fileDir = File(Environment.getExternalStorageDirectory().canonicalPath + Config.filePath)
            if (fileDir.exists()) {
                val subFile = fileDir.listFiles()
                for (iFileLength in subFile.indices) {
                    val curFile = subFile[iFileLength]
                    // 判断是否为文件夹
                    if (!curFile.isDirectory) {
                        val fileName = subFile[iFileLength].name
                        val text = AesEncryptUtil.aesDecodeText(Config.encryptionKey, Config.filePath, fileName)
                        val textArray = text.split(";")
                        val api = Api()
                        for (str in textArray) {
                            if (str.contains("=")) {
                                val prefix = str.subSequence(0, str.indexOf("="))
                                val content = str.subSequence(str.indexOf("=") + 1, str.length)
                                when (prefix) {
                                    "type" -> {
                                        api.type = content.toString()
                                    }
                                    "url" -> {
                                        api.url = content.toString()
                                    }
                                    "parameter" -> {
                                        api.parameter = content.toString()
                                    }
                                    "time" -> {
                                        val time = content.toString().toLong()
                                        val sdf = SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss")
                                        api.time = time
                                        api.date = sdf.format(time)
                                    }
                                }
                            }
                        }
                        if (!TextUtils.isEmpty(api.url)) {
                            var identifier = ""
                            val url = api.url
                            if (url.contains("?")) {
                                identifier = url.subSequence(0, url.indexOf("?")).toString()
                            }
                            if (FilterDbHelper.getInstance().isAlreadyFilter(identifier)) {
                                // 已过滤
                                api.file?.delete()
                            } else {
                                api.file = curFile
                                val time = SPUtil.instance.getLong("validity_period_time", 1000 * 60 * 60 * 12)
                                if (System.currentTimeMillis() - api.time < time || time <= 0) {
                                    logList.add(api)
                                } else {
                                    api.file?.delete()
                                }
                            }
                        }
                    }
                }
            }
            if (logList.isNotEmpty()) {
                Collections.sort(logList, SortComparatorUtil("desc"))
            }
        }
    }

}