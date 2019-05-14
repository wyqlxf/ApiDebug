package com.wyq.api.debug.util

import android.text.TextUtils

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * 项目名称：<br></br>
 * 类名称：JSONAnalyze<br></br>
 * 类描述：JSON解析<br></br>
 * 创建人：WangYongQi<br></br>
 * 创建时间：2016年9月7日下午2:47:52<br></br>
 * 修改人： <br></br>
 * 修改时间： <br></br>
 * 修改备注：
 *
 * @version V1.0
 */

object JSONAnalyze {

    fun getJSONObject(json: String): JSONObject? {
        return try {
            if (!TextUtils.isEmpty(json)) {
                JSONObject(json)
            } else {
                null
            }
        } catch (e: JSONException) {
            null
        }
    }

    fun getJSONArray(json: String?): JSONArray? {
        return try {
            if (!TextUtils.isEmpty(json)) {
                JSONArray(json)
            } else {
                null
            }
        } catch (e: JSONException) {
            null
        }
    }

    /**
     * 创建人：WangYongQi<br></br>
     * 创建时间：2016年9月7日 下午2:50:08<br></br>
     * 方法描述：获取JSON值<br></br>
     *
     * @param json
     * @return
     */
    fun getJSONValue(json: JSONObject?, key: String): String {
        var text = ""
        try {
            if (json != null && !TextUtils.isEmpty(key)) {
                text = if (json.isNull(key)) "" else json.getString(key)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return text
    }

    /**
     * 创建人：WangYongQi<br></br>
     * 创建时间：2016年9月7日 下午5:43:35<br></br>
     * 方法描述：获取JSON值<br></br>
     *
     * @param array
     * @param index
     * @return
     */
    fun getJSONValue(array: JSONArray?, index: Int): String {
        var text = ""
        try {
            if (array != null && index >= 0 && index < array.length()) {
                text = if (array.isNull(index)) "" else array.getString(index)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return text
    }

    /**
     * 创建人：WangYongQi<br></br>
     * 创建时间：2017年11月22日 下午6:20:12<br></br>
     * 方法描述：获取JSON值<br></br>
     *
     * @param array
     * @param index
     * @return
     */
    fun getJSONArray(array: JSONArray?, index: Int): JSONArray? {
        var jsonArray: JSONArray? = null
        try {
            if (array != null && index >= 0 && index < array.length()) {
                jsonArray = if (array.isNull(index)) null else array.getJSONArray(index)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return jsonArray
    }

    /**
     * 创建人：WangYongQi
     * 创建时间：2016年9月7日 下午6:04:17
     * 方法描述：获取一个JSONObject
     *
     * @param json
     * @param key
     * @return
     */
    fun getJSONObject(json: JSONObject?, key: String): JSONObject? {
        try {
            if (json != null && !TextUtils.isEmpty(key)) {
                return if (json.isNull(key)) null else json.getJSONObject(key)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return null
    }

    /**
     * 创建人：WangYongQi
     * 创建时间：2016年9月7日 下午5:54:45
     * 方法描述：获取一个JSONObject
     *
     * @param array
     * @param index
     * @return
     */
    fun getJSONObject(array: JSONArray?, index: Int): JSONObject? {
        var json: JSONObject? = null
        try {
            if (array != null && index >= 0 && index < array.length()) {
                json = if (array.isNull(index)) null else array.getJSONObject(index)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return json
    }

    /**
     * 创建人：WangYongQi
     * 创建时间：2016年9月7日 下午3:27:25
     * 方法描述：获取JSON数组
     *
     * @param json
     * @param key
     * @return
     */
    fun getJSONArray(json: JSONObject?, key: String): JSONArray? {
        var array: JSONArray? = null
        try {
            if (json != null && !TextUtils.isEmpty(key)) {
                array = if (json.isNull(key)) null else json.getJSONArray(key)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return array
    }

}
