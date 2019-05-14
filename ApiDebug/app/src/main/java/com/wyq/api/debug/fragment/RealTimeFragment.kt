package com.wyq.api.debug.fragment

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.wyq.api.debug.R
import com.wyq.api.debug.activity.ApiDetailActivity
import com.wyq.api.debug.adapter.ApiListAdapter
import com.wyq.api.debug.base.BaseApplication
import com.wyq.api.debug.entity.Api
import com.wyq.api.debug.entity.ApiLogList
import com.wyq.api.debug.interfaces.OnListItemClickListener
import com.wyq.api.debug.util.AppUtil

/**
 * 项目名称：ApiDebug
 * 创建人：WangYongQi
 * 创建时间：2019/1/4 11:40
 * 类描述：实时界面
 * 修改人：
 * 修改时间：
 * 修改备注：
 *
 */
class RealTimeFragment : Fragment() {

    companion object {

        private var instance: RealTimeFragment? = null

        fun getInstance(): RealTimeFragment {
            if (instance == null) {
                instance = RealTimeFragment()
            }
            return instance!!
        }

    }

    private var rootView: View? = null
    private var adapter: ApiListAdapter? = null
    private var refreshLayout: SwipeRefreshLayout? = null
    private var fabDelete: FloatingActionButton? = null
    private var llNoDataDefault: LinearLayout? = null
    private var mRefreshAsyncTask: RefreshAsyncTask? = null
    private var mDeleteAsyncTask: DeleteAsyncTask? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_real_time, container, false)
            val recyclerView = rootView?.findViewById<RecyclerView>(R.id.recyclerView)
            adapter = ApiListAdapter(context)
            adapter?.type = 1
            // 设置布局管理器
            val linearLayoutManager = LinearLayoutManager(context)
            recyclerView?.layoutManager = linearLayoutManager
            // 设置Item增加、移除动画
            recyclerView?.itemAnimator = DefaultItemAnimator()
            // 设置adapter
            recyclerView?.adapter = adapter
            // 设置数据源集合
            adapter?.list = ApiLogList.instance.getLogList()
            // 更新适配器
            adapter?.notifyDataSetChanged()
            // 设置点击监听
            adapter?.setOnItemClickListener(object : OnListItemClickListener {
                override fun onListItemClickListener(api: Api) {
                    val intent = Intent()
                    intent.putExtra("name", api.name)
                    intent.putExtra("type", api.type)
                    intent.putExtra("url", api.url)
                    intent.putExtra("parameter", api.parameter)
                    intent.putExtra("time", api.time)
                    intent.setClass(context, ApiDetailActivity::class.java)
                    startActivity(intent)
                }
            })

            // 刷新控件
            refreshLayout = rootView?.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
            // 悬浮删除按钮
            fabDelete = rootView?.findViewById<FloatingActionButton>(R.id.fabDelete)

            // 列表刷新
            refreshLayout?.isEnabled = true
            refreshLayout?.isRefreshing = false
            refreshLayout?.setColorSchemeColors(Color.parseColor("#1976d2"))
            refreshLayout?.setOnRefreshListener {
                refreshData(adapter, refreshLayout, fabDelete, true)
            }

            // 删除按钮
            fabDelete?.setOnClickListener {
                AlertDialog.Builder(context)
                        .setMessage("是否删除所有数据？")
                        .setPositiveButton("确定") { _, _ ->
                            if (mDeleteAsyncTask == null || mDeleteAsyncTask?.status != AsyncTask.Status.RUNNING) {
                                mDeleteAsyncTask = DeleteAsyncTask(context, adapter, refreshLayout, fabDelete, llNoDataDefault)
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                    mDeleteAsyncTask?.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                                } else {
                                    mDeleteAsyncTask?.execute()
                                }
                            }
                        }
                        .setNegativeButton("取消", null)
                        .create()
                        .show()
            }

            // 没有数据的缺省页
            llNoDataDefault = rootView?.findViewById<LinearLayout>(R.id.llNoDataDefault)
            adapter?.let {
                if (it.list.size > 0) {
                    llNoDataDefault?.visibility = View.GONE
                    refreshLayout?.visibility = View.VISIBLE
                    fabDelete?.visibility = View.VISIBLE
                } else {
                    llNoDataDefault?.visibility = View.VISIBLE
                    refreshLayout?.visibility = View.GONE
                    fabDelete?.visibility = View.GONE
                }
            }

            // 跳转主App
            val llJumpApp = rootView?.findViewById<LinearLayout>(R.id.llJumpApp)
            llJumpApp?.setOnClickListener {
                AppUtil.jumpMainApp(context)
            }
        }
        return rootView
    }

    /**
     * 创建人：WangYongQi
     * 创建时间：2019/1/14 17:55
     * 方法描述：刷新数据
     **/
    private fun refreshData(adapter: ApiListAdapter?, refreshLayout: SwipeRefreshLayout?, fabDelete: FloatingActionButton?, isToast: Boolean) {
        if (mRefreshAsyncTask == null || mRefreshAsyncTask?.status != AsyncTask.Status.RUNNING) {
            mRefreshAsyncTask = RefreshAsyncTask(adapter, refreshLayout, fabDelete, llNoDataDefault, isToast)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                mRefreshAsyncTask?.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
            } else {
                mRefreshAsyncTask?.execute()
            }
        }
    }

    // 删除，异步任务
    private class DeleteAsyncTask : AsyncTask<String, String, String> {

        private var mAdapter: ApiListAdapter?
        private var llNoDataDefault: LinearLayout? = null
        private var progressDialog: ProgressDialog? = null
        private var mRefreshLayout: SwipeRefreshLayout? = null
        private var mFabDelete: FloatingActionButton? = null

        constructor(context: Context?, alAdapter: ApiListAdapter?, refreshLayout: SwipeRefreshLayout?, fabDelete: FloatingActionButton?, layout: LinearLayout?) {
            mAdapter = alAdapter
            mRefreshLayout = refreshLayout
            mFabDelete = fabDelete
            if (context != null) {
                progressDialog = ProgressDialog.show(context, "", "正在删除中...", true)
                progressDialog?.setCancelable(false)
            }
            llNoDataDefault = layout
        }

        override fun doInBackground(vararg p0: String?): String {
            // 重新加载
            ApiLogList.instance.loadLogData()
            val list = ApiLogList.instance.getLogList()
            for (api in list) {
                // 删除文件
                try {
                    api.file?.delete()
                } catch (ex: Exception) {
                    Log.e("TAG", ex.message)
                }
            }
            // 重新加载
            ApiLogList.instance.loadLogData()
            return ""
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            // 重新赋值
            mAdapter?.list = ApiLogList.instance.getLogList()
            mAdapter?.notifyDataSetChanged()
            progressDialog?.let {
                if (it.isShowing) {
                    it.dismiss()
                }
            }
            mFabDelete?.visibility = View.GONE
            mRefreshLayout?.visibility = View.GONE
            llNoDataDefault?.visibility = View.VISIBLE
            Toast.makeText(BaseApplication.context, "删除成功！", Toast.LENGTH_LONG).show()
        }

    }

    // 刷新，异步任务
    private class RefreshAsyncTask : AsyncTask<String, String, String> {

        private var mAdapter: ApiListAdapter?
        private var llNoDataDefault: LinearLayout? = null
        private var mRefreshLayout: SwipeRefreshLayout? = null
        private var mFabDelete: FloatingActionButton? = null
        private var isToast = false

        constructor(alAdapter: ApiListAdapter?, refreshLayout: SwipeRefreshLayout?, fabDelete: FloatingActionButton?, layout: LinearLayout?, toast: Boolean) {
            mAdapter = alAdapter
            mRefreshLayout = refreshLayout
            mFabDelete = fabDelete
            llNoDataDefault = layout
            isToast = toast
        }

        override fun doInBackground(vararg p0: String?): String {
            ApiLogList.instance.loadLogData()
            return ""
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            mAdapter?.let {
                it.list = ApiLogList.instance.getLogList()
                it.notifyDataSetChanged()
                val count = it.list.size
                if (count > 0) {
                    llNoDataDefault?.visibility = View.GONE
                    mRefreshLayout?.visibility = View.VISIBLE
                    mFabDelete?.visibility = View.VISIBLE
                } else {
                    llNoDataDefault?.visibility = View.VISIBLE
                    mRefreshLayout?.visibility = View.GONE
                    mFabDelete?.visibility = View.GONE
                }
                if (isToast) {
                    Toast.makeText(BaseApplication.context, "一共找到 $count 条日志数据", Toast.LENGTH_SHORT).show()
                }
            }
            mRefreshLayout?.isRefreshing = false
        }

    }

    override fun onResume() {
        super.onResume()
        refreshData(adapter, refreshLayout, fabDelete, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }

}