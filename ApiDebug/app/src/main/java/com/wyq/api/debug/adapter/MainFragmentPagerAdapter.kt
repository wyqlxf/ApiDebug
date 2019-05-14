package com.wyq.api.debug.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * 项目名称：ApiDebug
 * 创建人：WangYongQi
 * 创建时间：2019/1/3 18:09
 * 类描述：页面滑动适配器
 * 修改人：
 * 修改时间：
 * 修改备注：
 *
 */
class MainFragmentPagerAdapter(fm: FragmentManager, fragmentList: List<Fragment>) : FragmentPagerAdapter(fm) {

    private var mFragmentList: List<Fragment> = fragmentList

    init {
        mFragmentList = fragmentList
    }

    override fun getItem(position: Int): Fragment? {
        return if (mFragmentList.size > position) {
            mFragmentList[position]
        } else {
            null
        }
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

}