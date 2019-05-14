package com.wyq.api.debug.interfaces

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

/**
 * 创建人： WangYongQi
 * 创建时间：2017/12/6.
 * 文件说明：加载更多滚动监听
 */

abstract class EndLessOnScrollListener(private val mLinearLayoutManager: LinearLayoutManager) : RecyclerView.OnScrollListener() {

    //当前页，从1开始
    private var currentPage = 1
    //已经加载出来的Item的数量
    private var totalItemCount: Int = 0
    //主要用来存储上一个totalItemCount
    private var previousTotal = 0
    //在屏幕上可见的item数量
    private var visibleItemCount: Int = 0
    //在屏幕可见的Item中的第一个
    private var firstVisibleItem: Int = 0

    //是否正在上拉数据
    private var loading = true

    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        visibleItemCount = recyclerView!!.childCount
        totalItemCount = mLinearLayoutManager.itemCount
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition()
        if (loading) {
            if (totalItemCount > previousTotal) {
                //说明数据已经加载结束
                loading = false
                previousTotal = totalItemCount
            }
        }
        //这里需要好好理解
        if (!loading && totalItemCount - visibleItemCount <= firstVisibleItem) {
            currentPage++
            onLoadMore(currentPage)
            loading = true
        }
    }

    fun reset() {
        //当前页，从1开始
        currentPage = 1
        //已经加载出来的Item的数量
        totalItemCount = 0
        //主要用来存储上一个totalItemCount
        previousTotal = 0
        //在屏幕上可见的item数量
        visibleItemCount = 0
        //在屏幕可见的Item中的第一个
        firstVisibleItem = 0
        //是否正在上拉数据
        loading = true
    }

    /**
     * 提供一个抽象方法，在Activity中监听到这个EndLessOnScrollListener
     * 并且实现这个方法
     */
    abstract fun onLoadMore(currentPage: Int)

}