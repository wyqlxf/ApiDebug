package com.wyq.api.debug.interfaces

/**
 * 创建人：WangYongQi
 * 创建时间：2016年11月11日下午3:34:43
 * 修改人：
 * 修改时间：
 * 修改备注：API请求结果回调
 *
 * @version V1.0
 */
interface OnApiResultListener {

    fun onResult(result: Map<String, Any>)

}
