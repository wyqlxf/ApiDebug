package com.wyq.api.debug.dialog

import android.graphics.Rect
import android.os.Build
import android.view.View
import android.widget.PopupWindow

/**
 * 项目名称：ApiDebug
 * 创建人：WangYongQi
 * 创建时间：2019/1/14 15:14
 * 类描述：解决PopupWindow在android7.0弹出位置错误问题
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
abstract class SupportPopupWindow : PopupWindow {

    constructor() : super()

    override fun showAsDropDown(anchor: View) {
        if (Build.VERSION.SDK_INT >= 24) {
            val rect = Rect()
            anchor.getGlobalVisibleRect(rect)
            val h = anchor.resources.displayMetrics.heightPixels - rect.bottom
            height = h
        }
        super.showAsDropDown(anchor)
    }

    override fun showAsDropDown(anchor: View, xoff: Int, yoff: Int) {
        if (Build.VERSION.SDK_INT >= 24) {
            val rect = Rect()
            anchor.getGlobalVisibleRect(rect)
            val h = anchor.resources.displayMetrics.heightPixels - rect.bottom
            height = h
        }
        super.showAsDropDown(anchor, xoff, yoff)
    }

}
