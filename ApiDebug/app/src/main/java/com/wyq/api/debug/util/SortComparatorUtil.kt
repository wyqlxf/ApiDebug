package com.wyq.api.debug.util

import android.text.TextUtils
import com.wyq.api.debug.entity.Api

/**
 * 创建人： WangYongQi
 * 创建时间：2018/7/19.
 * 文件说明：排序
 */

class SortComparatorUtil : Comparator<Api> {

    var mSort: String

    constructor(sort: String) {
        mSort = sort
    }

    override fun compare(l: Api, r: Api): Int {
        return compare(l.time, r.time)
    }

    private fun compare(left: Long, right: Long): Int {
        var state = 0
        if (!TextUtils.isEmpty(mSort)) {
            if (mSort == "asc") {
                // asc
                if (left > right) {
                    state = 1
                } else if (left < right) {
                    state = -1
                }
            } else {
                // desc
                if (left > right) {
                    state = -1
                } else if (left < right) {
                    state = 1
                }
            }
        }
        return state
    }

}