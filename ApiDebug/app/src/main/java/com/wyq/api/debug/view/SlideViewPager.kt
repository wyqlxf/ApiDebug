package com.wyq.api.debug.view

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * 项目名称：ApiDebug
 * 创建人：WangYongQi
 * 创建时间：2018/12/27 13:49
 * 类描述：自定义ViewPager
 * 修改人：
 * 修改时间：
 * 修改备注：
 *
 */
class SlideViewPager : ViewPager {

    var isScroll = true

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return if (isScroll) super.onTouchEvent(ev) else false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return if (isScroll) super.onInterceptTouchEvent(ev) else false
    }

}