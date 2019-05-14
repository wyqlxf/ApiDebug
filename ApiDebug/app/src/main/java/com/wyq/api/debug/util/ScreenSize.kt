package com.wyq.api.debug.util

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics

/**
 * 创建人：WangYongQi
 * 创建时间：2016年3月21日下午5:16:52
 * 修改人：
 * 修改时间：
 * 修改备注：
 *
 * @version V1.0
 */
object ScreenSize {

    /**
     * 创建人：WangYongQi
     * 创建时间：2016年3月21日 下午5:17:04
     * 方法描述：获取屏幕宽度
     *
     * @param activity
     */
    fun getScreenWidth(activity: Activity): Int {
        val wm = activity.windowManager
        val dm = DisplayMetrics()
        wm.defaultDisplay.getMetrics(dm)
        return dm.widthPixels
    }

    /**
     * 创建人：WangYongQi
     * 创建时间：2016年3月21日 下午5:17:04
     * 方法描述：获取屏幕高度
     *
     * @param activity
     */
    fun getScreenHeight(activity: Activity): Int {
        val wm = activity.windowManager
        val dm = DisplayMetrics()
        wm.defaultDisplay.getMetrics(dm)
        return dm.heightPixels
    }

    /**
     * 创建人：WangYongQi
     * 创建时间：2016年3月21日 下午5:17:04
     * 方法描述：获取屏幕大小
     *
     * @param activity
     */
    fun getScreenSize(activity: Activity): IntArray {
        val size = IntArray(2)
        val wm = activity.windowManager
        val dm = DisplayMetrics()
        wm.defaultDisplay.getMetrics(dm)
        size[0] = dm.widthPixels
        size[1] = dm.heightPixels
        return size
    }

    /**
     * 创建人：WangYongQi
     * 创建时间：2016年3月21日 下午5:17:18
     * 方法描述：DP转PX
     *
     * @param context
     * @param dpValue
     * @return
     */
    fun dipToPx(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * px转dp
     * @param context
     * @param pxValue
     * @return
     */
    fun pxToDip(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * 创建人：WangYongQi
     * 创建时间：2016年3月21日 下午5:17:28
     * 方法描述：将SP值转换为PX值
     *
     * @param context
     * @param spValue
     * @return
     */
    fun spToPx(context: Context, spValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    fun getStatusBarHeight(context: Context): Int {
        var height = dipToPx(context, 24f)
        try {
            val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                height = context.resources.getDimensionPixelSize(resourceId)
            }
        } catch (ex: Exception) {
        }
        return height
    }

}
