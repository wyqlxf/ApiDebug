package com.wyq.api.debug.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button

import com.wyq.api.debug.R

/**
 * 项目名称：ApiDebug
 * 创建人：WangYongQi
 * 创建时间：2019/1/9 16:08
 * 类描述：圆角对话框
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
class RealTimeDialog(context: Context) : Dialog(context, R.style.roundDialog) {

    var btnDeleteData: Button? = null
    var btnFilterData: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.item_dialog_real)
        btnDeleteData = findViewById(R.id.btnDeleteData)
        btnFilterData = findViewById(R.id.btnFilterData)
    }

}