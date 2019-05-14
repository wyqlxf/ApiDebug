package com.wyq.api.debug.interfaces

import com.wyq.api.debug.entity.Api

/**
 * 创建人： WangYongQi
 * 创建时间：2018/7/16.
 * 文件说明：列表点击监听
 */
interface OnListItemClickListener {

    // 列表点击
    fun onListItemClickListener(api: Api)

}