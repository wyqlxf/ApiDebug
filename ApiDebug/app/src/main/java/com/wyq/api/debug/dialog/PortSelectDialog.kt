package com.wyq.api.debug.dialog

import android.app.Activity
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.wyq.api.debug.R
import com.wyq.api.debug.interfaces.OnPortItemClickListener

/**
 * 项目名称：ApiDebug
 * 创建人：WangYongQi
 * 创建时间：2019/1/14 15:16
 * 类描述：
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
class PortSelectDialog(activity: Activity?) : SupportPopupWindow(), View.OnClickListener {

    /**
     * 选项点击事件接口
     */
    var mOnPortItemClickListener: OnPortItemClickListener? = null

    init {
        val inflater = activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val contentView = inflater.inflate(R.layout.item_dialog_port, null)
        val dm = DisplayMetrics()
        activity?.windowManager.defaultDisplay.getMetrics(dm)
        val w = dm.widthPixels
        // 设置SelectPicPopupWindow的View
        this.contentView = contentView
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.width = w
        // 设置SelectPicPopupWindow弹出窗体的高
        this.height = ViewGroup.LayoutParams.MATCH_PARENT
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.isFocusable = false
        this.isOutsideTouchable = false
        // 刷新状态
        this.update()
        // 实例化一个ColorDrawable颜色为半透明
        val dw = ColorDrawable(0)
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismissListener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw)
        contentView.findViewById<View>(R.id.tv_web).setOnClickListener(this)
        contentView.findViewById<View>(R.id.tv_ios).setOnClickListener(this)
        contentView.findViewById<View>(R.id.tv_android).setOnClickListener(this)
        contentView.findViewById<View>(R.id.dialog_top_view).setOnClickListener(this)
        contentView.findViewById<View>(R.id.dialog_bottom_view).setOnClickListener(this)
    }

    override fun onClick(view: View) {
        mOnPortItemClickListener?.let {
            when (view.id) {
                R.id.tv_web -> it.onItemClicked(0)
                R.id.tv_ios -> it.onItemClicked(1)
                R.id.tv_android -> it.onItemClicked(2)
                else -> it.onItemClicked(-1)
            }
        }
    }

}
