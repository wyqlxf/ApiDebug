package com.wyq.api.debug.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.wyq.api.debug.R
import com.wyq.api.debug.activity.FieldDetailActivity
import com.wyq.api.debug.activity.PostmanActivity
import com.wyq.api.debug.base.BaseApplication
import com.wyq.api.debug.config.Config
import com.wyq.api.debug.database.FavDbHelper
import com.wyq.api.debug.database.FilterDbHelper
import com.wyq.api.debug.dialog.RealTimeDialog
import com.wyq.api.debug.entity.Api
import com.wyq.api.debug.interfaces.OnListItemClickListener
import com.wyq.library.util.SPUtil
import java.util.*

/**
 * 创建人： WangYongQi
 * 创建时间：2018/7/16.
 * 文件说明：Api列表适配器
 */

class ApiListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // 0 默认/1 实时/ 2收藏 / 3过滤
    var type = 0
    // 总量
    var totalRows = 10000
    var list: MutableList<Api>
    private var mContext: Context?
    private var mInflater: LayoutInflater
    private lateinit var mItemClickListener: OnListItemClickListener

    constructor(context: Context?) {
        mContext = context
        mInflater = LayoutInflater.from(context)
        list = ArrayList()
    }

    fun setOnItemClickListener(itemClickListener: OnListItemClickListener) {
        this.mItemClickListener = itemClickListener
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = mInflater.inflate(R.layout.item_api_list, null) as View
        var listHolder = ListHolder(view)
        listHolder.tvName = view.findViewById(R.id.tv_name)
        listHolder.layoutName = view.findViewById(R.id.layout_name)
        listHolder.tvType = view.findViewById(R.id.tv_type)
        listHolder.tvUrl = view.findViewById(R.id.tv_url)
        listHolder.tvParameter = view.findViewById(R.id.tv_parameter)
        listHolder.layoutParameter = view.findViewById(R.id.layout_parameter)
        listHolder.tvTime = view.findViewById(R.id.tv_time)
        listHolder.layoutTime = view.findViewById(R.id.layout_time)
        listHolder.tvDescription = view.findViewById(R.id.tv_description)
        listHolder.layoutDescription = view.findViewById(R.id.layout_description)
        listHolder.tvPostMan = view.findViewById(R.id.tv_postMan)
        listHolder.tvFieldDetail = view.findViewById(R.id.tv_field_detail)
        listHolder.viewLine = view.findViewById(R.id.view_line)
        listHolder.layoutItem = view.findViewById(R.id.layout_item)
        if (type == 0) {
            listHolder.tvFieldDetail.visibility = View.VISIBLE
        } else {
            listHolder.tvFieldDetail.visibility = View.GONE
        }
        return listHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ListHolder) {
            if (list.size > position) {
                var api = list[position]
                api.position = position
                if (TextUtils.isEmpty(api.name)) {
                    holder.layoutName.visibility = View.GONE
                } else {
                    holder.tvName.text = api.name
                    holder.layoutName.visibility = View.VISIBLE
                }

                holder.tvType.text = api.type

                // 域名转换
                var url = api.url
                val flag = SPUtil.instance.getString("host_flag", "0")
                when (flag) {
                    "1" -> {
                        // 一键切换成线上环境
                        url = url.replace(Config.debugHost, Config.host)
                        url = url.replace(Config.devHost, Config.host)
                    }
                    "2" -> {
                        // 一键切换成debug环境
                        url = url.replace(Config.host, Config.debugHost)
                        url = url.replace(Config.devHost, Config.debugHost)
                    }
                    "3" -> {
                        // 一键切换成dev环境
                        url = url.replace(Config.host, Config.devHost)
                        url = url.replace(Config.debugHost, Config.devHost)
                    }
                }
                holder.tvUrl.text = url

                if (TextUtils.isEmpty(api.parameter) || api.description == "null") {
                    holder.layoutParameter.visibility = View.GONE
                } else {
                    holder.tvParameter.text = api.parameter
                    holder.layoutParameter.visibility = View.VISIBLE
                }

                if (TextUtils.isEmpty(api.date)) {
                    holder.layoutTime.visibility = View.GONE
                } else {
                    holder.tvTime.text = api.date
                    holder.layoutTime.visibility = View.VISIBLE
                }

                if (TextUtils.isEmpty(api.description) || api.description == "null") {
                    holder.layoutDescription.visibility = View.GONE
                } else {
                    holder.tvDescription.text = api.description
                    holder.layoutDescription.visibility = View.VISIBLE
                }

                // 忽略大小写比较，如果是GET或者POST请求
                if (api.type.equals("GET", ignoreCase = true) || api.type.equals("POST", ignoreCase = true)) {
                    holder.tvPostMan.visibility = View.VISIBLE
                    holder.tvPostMan.setOnClickListener {
                        //获取剪贴板管理器
                        // val cm = mContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        // 创建普通字符型ClipData
                        // val mClipData = ClipData.newPlainText("Label", "" + api.toString())
                        // 将ClipData内容放到系统剪贴板里。
                        // cm.primaryClip = mClipData
                        val intent = Intent()
                        intent.putExtra("name", api.name)
                        intent.putExtra("type", api.type)
                        intent.putExtra("url", api.url)
                        intent.putExtra("parameter", api.parameter)
                        intent.setClass(mContext, PostmanActivity::class.java)
                        mContext?.startActivity(intent)
                    }
                } else {
                    holder.tvPostMan.visibility = View.GONE
                }
                // 参数详情
                holder.tvFieldDetail.setOnClickListener {
                    val intent = Intent()
                    val bundle = Bundle()
                    bundle.putSerializable("api", api)
                    intent.putExtras(bundle)
                    intent.setClass(mContext, FieldDetailActivity::class.java)
                    mContext?.startActivity(intent)
                }

                if (list.size == position + 1) {
                    holder.viewLine.visibility = View.GONE
                } else {
                    holder.viewLine.visibility = View.VISIBLE
                }

                holder.layoutItem.setOnClickListener {
                    if (mItemClickListener != null) {
                        mItemClickListener.onListItemClickListener(api)
                    }
                }
                holder.layoutItem.setOnLongClickListener {
                    when (type) {
                        1 -> {
                            // 实时
                            if (mContext != null) {
                                val dialog = RealTimeDialog(mContext!!)
                                dialog.setCancelable(true)
                                dialog.show()
                                dialog.btnDeleteData?.setOnClickListener {
                                    // 删除
                                    api.file?.delete()
                                    list.remove(api)
                                    notifyDataSetChanged()
                                    Toast.makeText(BaseApplication.context, "删除成功！", Toast.LENGTH_SHORT).show()
                                    if (dialog.isShowing) {
                                        dialog.dismiss()
                                    }
                                }
                                dialog.btnFilterData?.setOnClickListener {
                                    // 过滤
                                    var identifier = ""
                                    val url = api.url
                                    if (url.contains("?")) {
                                        identifier = url.subSequence(0, url.indexOf("?")).toString()
                                    }
                                    // 添加过滤
                                    FilterDbHelper.getInstance().addFilter(api.url, identifier, api.name, api.type, api.time.toString(), api.parameter, api.description)
                                    list.remove(api)
                                    notifyDataSetChanged()
                                    Toast.makeText(BaseApplication.context, "过滤成功！", Toast.LENGTH_SHORT).show()
                                    if (dialog.isShowing) {
                                        dialog.dismiss()
                                    }
                                }
                            }
                        }
                        2 -> {
                            // 收藏
                            AlertDialog.Builder(mContext)
                                    .setMessage("是否确定取消收藏？")
                                    .setPositiveButton("确定") { _, _ ->
                                        var identifier = ""
                                        val url = api.url
                                        if (url.contains("?")) {
                                            identifier = url.subSequence(0, url.indexOf("?")).toString()
                                        }
                                        FavDbHelper.getInstance().removeFavorite(identifier)
                                        list.remove(api)
                                        notifyDataSetChanged()
                                        Toast.makeText(BaseApplication.context, "取消收藏成功！", Toast.LENGTH_SHORT).show()
                                    }
                                    .setNegativeButton("取消", null)
                                    .create()
                                    .show()
                        }
                        3 -> {
                            // 解除过滤
                            AlertDialog.Builder(mContext)
                                    .setMessage("是否解除过滤？")
                                    .setPositiveButton("确定") { _, _ ->
                                        var identifier = ""
                                        val url = api.url
                                        if (url.contains("?")) {
                                            identifier = url.subSequence(0, url.indexOf("?")).toString()
                                        }
                                        FilterDbHelper.getInstance().removeFilter(identifier)
                                        list.remove(api)
                                        notifyDataSetChanged()
                                        Toast.makeText(BaseApplication.context, "解除过滤成功！", Toast.LENGTH_SHORT).show()
                                    }
                                    .setNegativeButton("取消", null)
                                    .create()
                                    .show()
                        }
                    }
                    true
                }
            }
        }
    }

    private class ListHolder : RecyclerView.ViewHolder {

        constructor(view: View) : super(view)

        lateinit var tvName: TextView
        lateinit var layoutName: LinearLayout

        lateinit var tvType: TextView

        lateinit var tvUrl: TextView

        lateinit var tvParameter: TextView
        lateinit var layoutParameter: LinearLayout

        lateinit var tvTime: TextView
        lateinit var layoutTime: LinearLayout

        lateinit var tvDescription: TextView
        lateinit var layoutDescription: LinearLayout

        lateinit var tvPostMan: TextView
        lateinit var tvFieldDetail: TextView

        lateinit var viewLine: View
        lateinit var layoutItem: LinearLayout

    }

}