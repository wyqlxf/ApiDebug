package com.wyq.api.debug.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.wyq.api.debug.R
import com.wyq.api.debug.entity.Api
import com.wyq.api.debug.interfaces.OnListItemClickListener

/**
 * 创建人： WangYongQi
 * 创建时间：2018/7/17.
 * 文件说明：
 */
class SearchListAdapter : BaseAdapter {

    var list: List<Api>
    private var mContext: Context
    private var mInflater: LayoutInflater
    private lateinit var mItemClickListener: OnListItemClickListener

    constructor(context: Context) {
        mContext = context
        mInflater = LayoutInflater.from(context)
        list = arrayListOf()
    }

    fun setOnItemClickListener(itemClickListener: OnListItemClickListener) {
        this.mItemClickListener = itemClickListener
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return if (list.size > position) {
            list[position]
        } else {
            Api()
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var viewHolder: ViewHolder
        var view: View
        if (convertView == null) {
            view = mInflater.inflate(R.layout.item_api_search, null)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }
        if (list.size > position) {
            val api = list[position]
            viewHolder.tvName.text = api.name

            viewHolder.tvName.setOnClickListener {
                mItemClickListener.onListItemClickListener(api)
            }
        }
        return view
    }

    private class ViewHolder {

        var tvName: TextView

        constructor(view: View) {
            tvName = view.findViewById(R.id.tv_name)
        }

    }

}