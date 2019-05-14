package com.wyq.library.util

import android.content.Context
import android.content.SharedPreferences
import com.wyq.api.debug.base.BaseApplication

/**
 * 创建人： WangYongQi
 * 创建时间：2018/7/4.
 * 文件说明：轻量级数据缓存
 */
class SPUtil {

    private var sharedPref: SharedPreferences
    private var editor: SharedPreferences.Editor

    private constructor(context: Context) {
        sharedPref = context.getSharedPreferences("ApiDebug", Context.MODE_PRIVATE)
        editor = sharedPref.edit()
    }

    companion object {

        private var INSTANCE: SPUtil? = null

        val instance: SPUtil
            get() {
                if (INSTANCE == null) {
                    INSTANCE = SPUtil(BaseApplication.context)
                }
                return INSTANCE!!
            }
    }

    fun getString(key: String, defValue: String): String {
        return if (sharedPref.contains(key)) {
            sharedPref.getString(key, defValue)
        } else {
            defValue
        }
    }

    fun setString(key: String, value: String) {
        if (sharedPref.contains(key)) {
            editor.remove(key)
        }
        editor.putString(key, value)
        editor.commit()
    }

    fun getInt(key: String, defValue: Int): Int {
        return if (sharedPref.contains(key)) {
            sharedPref.getInt(key, defValue)
        } else {
            defValue
        }
    }

    fun setInt(key: String, value: Int) {
        if (sharedPref.contains(key)) {
            editor.remove(key)
        }
        editor.putInt(key, value)
        editor.commit()
    }

    fun getLong(key: String, defValue: Long): Long {
        return if (sharedPref.contains(key)) {
            sharedPref.getLong(key, defValue)
        } else {
            defValue
        }
    }

    fun setLong(key: String, value: Long) {
        if (sharedPref.contains(key)) {
            editor.remove(key)
        }
        editor.putLong(key, value)
        editor.commit()
    }

    fun getFloat(key: String, defValue: Float): Float {
        return if (sharedPref.contains(key)) {
            sharedPref.getFloat(key, defValue)
        } else {
            defValue
        }
    }

    fun setFloat(key: String, value: Float) {
        if (sharedPref.contains(key)) {
            editor.remove(key)
        }
        editor.putFloat(key, value)
        editor.commit()
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return if (sharedPref.contains(key)) {
            sharedPref.getBoolean(key, defValue)
        } else {
            defValue
        }
    }

    fun setBoolean(key: String, value: Boolean) {
        if (sharedPref.contains(key)) {
            editor.remove(key)
        }
        editor.putBoolean(key, value)
        editor.commit()
    }

    fun remove(key: String) {
        if (sharedPref.contains(key)) {
            editor.remove(key)
            editor.commit()
        }
    }

    fun getAll(): Map<String, *> {
        return sharedPref.all
    }

    fun clearAll() {
        editor.clear()
        editor.commit()
    }

}