package com.wyq.api.debug.entity

import java.io.File
import java.io.Serializable

/**
 * 创建人： WangYongQi
 * 创建时间：2018/7/16.
 * 文件说明：Api实体类
 */

class Api : Serializable {

    // 名称
    var name: String = ""
    // 类型
    var type: String = ""
    // 链接
    var url: String = ""
    // 参数
    var parameter: String = ""
    // 链接描述
    var description: String = ""
    // 接口详情
    var filedDetail: MutableList<String> = ArrayList()

    // 文件
    var file: File? = null
    // 日期
    var date: String = ""
    // 时间戳
    var time: Long = 0
    // 位置
    var position: Int = 0

    override fun toString(): String {
        return "接口名称：" + name +
                "\n 接口类型：" + type +
                "\n 接口地址：" + url +
                "\n 接口参数：" + parameter +
                "\n 接口描述：" + description
    }

}