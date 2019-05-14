package com.wyq.api.debug.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.wyq.api.debug.R
import com.wyq.api.debug.activity.ApiDetailActivity
import com.wyq.api.debug.adapter.ApiListAdapter
import com.wyq.api.debug.adapter.SearchListAdapter
import com.wyq.api.debug.config.Config
import com.wyq.api.debug.dialog.PortSelectDialog
import com.wyq.api.debug.entity.Api
import com.wyq.api.debug.interfaces.EndLessOnScrollListener
import com.wyq.api.debug.interfaces.OnListItemClickListener
import com.wyq.api.debug.interfaces.OnPortItemClickListener
import com.wyq.api.debug.request.HttpClientHelper
import com.wyq.api.debug.util.JSONAnalyze

/**
 * 项目名称：ApiDebug
 * 创建人：WangYongQi
 * 创建时间：2019/1/4 9:05
 * 类描述：默认界面
 * 修改人：
 * 修改时间：
 * 修改备注：
 *
 */
class DefaultFragment : Fragment() {

    companion object {

        private var instance: DefaultFragment? = null

        fun getInstance(): DefaultFragment {
            if (instance == null) {
                instance = DefaultFragment()
            }
            return instance!!
        }

    }

    private var rootView: View? = null
    private var adapter: ApiListAdapter? = null
    private lateinit var searchDialog: PopupWindow
    private lateinit var searchAdapter: SearchListAdapter
    private var llLoading: LinearLayout? = null
    private var portSelectDialog: PortSelectDialog? = null
    private var refreshLayout: SwipeRefreshLayout? = null
    private var mLoadAsyncTask: LoadAsyncTask? = null
    private var endLessOnScrollListener: EndLessOnScrollListener? = null

    // device设备号
    private var device = "android"
    //记录目标项位置
    private var mToPosition: Int = 0
    //目标项是否在最后一个可见项之后
    private var mShouldScroll: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_default, container, false)
            val recyclerView = rootView?.findViewById<RecyclerView>(R.id.recyclerView)
            adapter = ApiListAdapter(context)
            adapter?.type = 0
            // 设置布局管理器
            val linearLayoutManager = LinearLayoutManager(context)
            recyclerView?.layoutManager = linearLayoutManager
            // 设置Item增加、移除动画
            recyclerView?.itemAnimator = DefaultItemAnimator()
            // 设置adapter
            recyclerView?.adapter = adapter
            // 设置点击监听
            adapter?.setOnItemClickListener(object : OnListItemClickListener {
                override fun onListItemClickListener(api: Api) {
                    val intent = Intent()
                    intent.putExtra("name", api.name)
                    intent.putExtra("type", api.type)
                    intent.putExtra("url", api.url)
                    intent.putExtra("parameter", api.parameter)
                    intent.putExtra("time", api.time)
                    intent.putExtra("description", api.description)
                    intent.setClass(context, ApiDetailActivity::class.java)
                    startActivity(intent)
                }
            })
            endLessOnScrollListener = object : EndLessOnScrollListener(linearLayoutManager) {
                override fun onLoadMore(currentPage: Int) {
                    loadData(adapter, refreshLayout, llLoading, device, currentPage)
                }
            }
            recyclerView?.addOnScrollListener(endLessOnScrollListener)

            // 正在加载
            llLoading = rootView?.findViewById<LinearLayout>(R.id.llLoading)

            // 刷新控件
            refreshLayout = rootView?.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
            // 列表刷新
            refreshLayout?.isEnabled = true
            refreshLayout?.isRefreshing = false
            refreshLayout?.setColorSchemeColors(Color.parseColor("#1976d2"))
            refreshLayout?.setOnRefreshListener {
                loadData(adapter, refreshLayout, llLoading, device, 1)
            }

            // 初始化对话框
            val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.dialog_search, null)
            val listView = view.findViewById<ListView>(R.id.lv_search)
            searchAdapter = SearchListAdapter(context!!)
            listView.adapter = searchAdapter
            searchAdapter.setOnItemClickListener(object : OnListItemClickListener {
                override fun onListItemClickListener(api: Api) {
                    closePopupWindow()
                    if (api.position >= 0) {
                        smoothMoveToPosition(recyclerView, api.position)
                    }
                }
            })
            searchDialog = PopupWindow(view)
            searchDialog.width = ViewGroup.LayoutParams.MATCH_PARENT
            searchDialog.height = ViewGroup.LayoutParams.MATCH_PARENT
            searchDialog.isFocusable = true
            searchDialog.isTouchable = true
            searchDialog.isOutsideTouchable = false
            searchDialog.setBackgroundDrawable(BitmapDrawable(resources, Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)))

            // 头部布局
            val llHead = rootView?.findViewById<LinearLayout>(R.id.llHead)

            // 关键字搜索框
            val etSearch = rootView?.findViewById<EditText>(R.id.etSearch)
            // 关键字搜索框监听
            etSearch?.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // 进行搜索
                    val text = etSearch.text
                    if (!TextUtils.isEmpty(text)) {
                        val list = arrayListOf<Api>()
                        adapter?.let {
                            for (api in it.list) {
                                if (text in api.name || text in api.url) {
                                    list.add(api)
                                }
                            }
                        }
                        if (list.isNotEmpty()) {
                            closePopupWindow()
                            showPopupWindow(llHead, list)
                        } else {
                            Toast.makeText(context, "匹配失败，请换个关键字再试试！", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                false
            }

            // 箭头
            val ivArrow = rootView?.findViewById<ImageView>(R.id.ivArrow)
            ivArrow?.setImageResource(R.drawable.ic_arrow_drop_down_orange_a700_24dp)
            // 各端的图标
            val ivPortIcon = rootView?.findViewById<ImageView>(R.id.ivPortIcon)
            ivPortIcon?.setImageResource(R.mipmap.ic_android)
            // 初始化弹窗
            portSelectDialog = PortSelectDialog(activity)
            portSelectDialog?.mOnPortItemClickListener = object : OnPortItemClickListener {
                override fun onItemClicked(position: Int) {
                    when (position) {
                        0 -> {
                            ivPortIcon?.setImageResource(R.mipmap.ic_web)
                            // 刷新数据
                            device = "touch"
                            loadData(adapter, refreshLayout, llLoading, device, 1)
                        }
                        1 -> {
                            ivPortIcon?.setImageResource(R.mipmap.ic_ios)
                            // 刷新数据
                            device = "ios"
                            loadData(adapter, refreshLayout, llLoading, device, 1)
                        }
                        2 -> {
                            ivPortIcon?.setImageResource(R.mipmap.ic_android)
                            // 刷新数据
                            device = "android"
                            loadData(adapter, refreshLayout, llLoading, device, 1)
                        }
                    }
                    portSelectDialog?.let {
                        if (it.isShowing) {
                            it.dismiss()
                        }
                    }
                    ivArrow?.setImageResource(R.drawable.ic_arrow_drop_down_orange_a700_24dp)
                }
            }

            // 各端弹窗选择
            val llPortSelect = rootView?.findViewById<LinearLayout>(R.id.llPortSelect)
            llPortSelect?.setOnClickListener {
                portSelectDialog?.let {
                    if (!it.isShowing) {
                        it.showAsDropDown(llPortSelect, llPortSelect.layoutParams.width, 0)
                        ivArrow?.setImageResource(R.drawable.ic_arrow_drop_up_orange_a700_24dp)
                    } else {
                        it.dismiss()
                        ivArrow?.setImageResource(R.drawable.ic_arrow_drop_down_orange_a700_24dp)
                    }
                }
            }
        }
        // 刷新数据
        loadData(adapter, refreshLayout, llLoading, device, 1)
        return rootView
    }

    /**
     * 创建人：WangYongQi
     * 创建时间：2019/1/14 17:55
     * 方法描述：刷新数据
     **/
    private fun loadData(adapter: ApiListAdapter?, refreshLayout: SwipeRefreshLayout?, llLoading: LinearLayout?, device: String, page: Int) {
        if (page > 1) {
            llLoading?.visibility = View.GONE
        } else {
            llLoading?.visibility = View.VISIBLE
            endLessOnScrollListener?.reset()
        }
        adapter?.let {
            if (page <= 1) {
                it.list.clear()
                it.totalRows = 10000
            }
            if (it.list.size < it.totalRows) {
                if (mLoadAsyncTask == null || mLoadAsyncTask?.status != AsyncTask.Status.RUNNING) {
                    mLoadAsyncTask = LoadAsyncTask(adapter, refreshLayout, llLoading, device, page)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        mLoadAsyncTask?.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                    } else {
                        mLoadAsyncTask?.execute()
                    }
                }
            }
        }
    }

    // 刷新，异步任务
    private class LoadAsyncTask : AsyncTask<String, String, MutableList<Api>> {

        private var mAdapter: ApiListAdapter?
        private var llLoading: LinearLayout? = null
        private var mRefreshLayout: SwipeRefreshLayout? = null
        private var page = 1
        private var device: String = "android"

        constructor(alAdapter: ApiListAdapter?, refreshLayout: SwipeRefreshLayout?, llLoading: LinearLayout?, device: String, page: Int) {
            this.mAdapter = alAdapter
            this.mRefreshLayout = refreshLayout
            this.llLoading = llLoading
            this.device = device
            this.page = page
        }

        override fun doInBackground(vararg p0: String?): MutableList<Api> {
            // device代表端，page代表分页，list_rows代表每一页返回的数据量
            val map = HttpClientHelper.doGet(Config.apiListUrl + "device=$device&page=$page&list_rows=10")
            var result = ""
            if (map.containsKey("result") && map["result"] is String) {
                result = map["result"] as String
            }
            var list: MutableList<Api> = ArrayList()
            if (!TextUtils.isEmpty(result)) {
                val json = JSONAnalyze.getJSONObject(result)
                val status = JSONAnalyze.getJSONValue(json, "status")
                if (status == "1") {
                    val data = JSONAnalyze.getJSONObject(json, "data")
                    val total = JSONAnalyze.getJSONValue(data, "total_rows")
                    if (!TextUtils.isEmpty(total)) {
                        try {
                            val totalRows = total.toInt()
                            if (totalRows > 0) {
                                mAdapter?.totalRows = totalRows
                            }
                        } catch (ex: Exception) {
                        }
                    }
                    val apiList = JSONAnalyze.getJSONArray(data, "api_list")
                    apiList?.let {
                        for (i in 0 until it.length()) {
                            val obj = JSONAnalyze.getJSONObject(it, i)
                            val apiName = JSONAnalyze.getJSONValue(obj, "api_name")
                            val apiType = JSONAnalyze.getJSONValue(obj, "api_type")
                            val apiAddress = JSONAnalyze.getJSONValue(obj, "api_address")
                            val apiParameter = JSONAnalyze.getJSONValue(obj, "api_parameter")
                            val apiDescription = JSONAnalyze.getJSONValue(obj, "api_description")
                            val filedDetail = JSONAnalyze.getJSONArray(obj, "api_detail")
                            val api = Api()
                            api.name = apiName
                            api.type = apiType
                            api.url = apiAddress
                            api.parameter = apiParameter
                            api.description = apiDescription
                            var tempList: MutableList<String> = ArrayList()
                            filedDetail?.let {
                                for (i in 0 until it.length()) {
                                    val value = JSONAnalyze.getJSONValue(it, i)
                                    if (!TextUtils.isEmpty(value)) {
                                        tempList.add(value)
                                    }
                                }
                            }
                            api.filedDetail = tempList
                            list.add(api)
                        }
                    }
                }
            }
            return list
        }

        override fun onPostExecute(list: MutableList<Api>) {
            super.onPostExecute(list)
            mAdapter?.let {
                it.list.addAll(list)
                for (i in 0 until it.list.size) {
                    it.list[i].position = i
                }
                it.notifyDataSetChanged()
//                if (page == 1) {
//                    val count = it.list.size
//                    Toast.makeText(BaseApplication.context, "一共找到 $count 条日志数据", Toast.LENGTH_SHORT).show()
//                }
            }
            mRefreshLayout?.isRefreshing = false
            llLoading?.visibility = View.GONE
        }

    }

    /**
     * 滑动到指定位置
     */
    private fun smoothMoveToPosition(mRecyclerView: RecyclerView?, position: Int) {
        mRecyclerView?.let {
            // 第一个可见位置
            val firstItem = it.getChildLayoutPosition(it.getChildAt(0))
            // 最后一个可见位置
            val lastItem = it.getChildLayoutPosition(it.getChildAt(it.childCount - 1))
            if (position < firstItem) {
                // 第一种可能:跳转位置在第一个可见位置之前
                it.smoothScrollToPosition(position)
            } else if (position <= lastItem) {
                // 第二种可能:跳转位置在第一个可见位置之后
                val movePosition = position - firstItem
                if (movePosition >= 0 && movePosition < it.childCount) {
                    val top = it.getChildAt(movePosition).top
                    it.smoothScrollBy(0, top)
                }
            } else {
                // 第三种可能:跳转位置在最后可见项之后
                it.smoothScrollToPosition(position)
                mToPosition = position
                mShouldScroll = true
            }
        }
    }

    /**
     * 显示关键字弹窗
     */
    private fun showPopupWindow(anchor: LinearLayout?, list: List<Api>) {
        searchDialog?.let {
            searchAdapter.list = list
            searchAdapter.notifyDataSetChanged()
            if (Build.VERSION.SDK_INT >= 24) {
                val location = IntArray(2)
                anchor?.getLocationOnScreen(location)
                // 7.1 版本处理
                if (Build.VERSION.SDK_INT == 25) {
                    val windowManager = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                    if (windowManager != null) {
                        val manager = activity?.windowManager
                        val outMetrics = DisplayMetrics()
                        manager?.defaultDisplay?.getMetrics(outMetrics)
                        val screenHeight = outMetrics.heightPixels
                        it.height = screenHeight - location[1] - anchor!!.height
                    }
                }
                it.showAtLocation(anchor, Gravity.NO_GRAVITY, 0, location[1] + anchor!!.height)
            } else {
                it.showAsDropDown(anchor, 0, 0)
            }
        }
    }

    /**
     * 关闭关键字弹窗
     */
    private fun closePopupWindow() {
        if (searchDialog != null && searchDialog.isShowing) {
            searchDialog.dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        adapter?.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }

}