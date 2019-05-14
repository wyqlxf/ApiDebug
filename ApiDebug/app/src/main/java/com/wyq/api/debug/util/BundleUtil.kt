package com.wyq.api.debug.util

import android.os.Bundle
import android.text.TextUtils

object BundleUtil {

    /**
     * @param bundle
     * @param key
     * @return
     */
    fun getString(bundle: Bundle?, key: String): String? {
        var value: String? = ""
        if (bundle != null && !TextUtils.isEmpty(key)) {
            value = if (bundle.containsKey(key)) bundle.getString(key) else ""
        }
        return value
    }

    /**
     * @param bundle
     * @param key
     * @return
     */
    fun getInt(bundle: Bundle?, key: String): Int {
        var value = -1
        if (bundle != null && !TextUtils.isEmpty(key)) {
            value = if (bundle.containsKey(key)) bundle.getInt(key) else -1
        }
        return value
    }

    /**
     * @param bundle
     * @param key
     * @return
     */
    fun getInt(bundle: Bundle?, key: String, defaultValue: Int): Int {
        var value = defaultValue
        if (bundle != null && !TextUtils.isEmpty(key)) {
            value = if (bundle.containsKey(key)) bundle.getInt(key) else defaultValue
        }
        return value
    }

    /**
     * @param bundle
     * @param key
     * @return
     */
    fun getLong(bundle: Bundle?, key: String): Long {
        var value: Long = -1
        if (bundle != null && !TextUtils.isEmpty(key)) {
            value = if (bundle.containsKey(key)) bundle.getLong(key) else -1
        }
        return value
    }

    /**
     * @param bundle
     * @param key
     * @param defaultValue
     * @return
     */
    fun getBoolean(bundle: Bundle?, key: String, defaultValue: Boolean): Boolean {
        var flag = defaultValue
        if (bundle != null && !TextUtils.isEmpty(key)) {
            flag = if (bundle.containsKey(key)) bundle.getBoolean(key) else defaultValue
        }
        return flag
    }

}
