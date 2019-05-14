package com.wyq.api.debug.activity

import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.wyq.api.debug.R
import com.wyq.api.debug.adapter.ApiListAdapter
import com.wyq.api.debug.base.BaseAppCompatActivity
import com.wyq.api.debug.base.BaseApplication
import com.wyq.api.debug.database.FilterDbHelper
import com.wyq.api.debug.entity.Api
import com.wyq.api.debug.interfaces.OnListItemClickListener
import kotlinx.android.synthetic.main.activity_api_filter.*

/**
 * 项目名称：ApiDebug
 * 创建人：WangYongQi
 * 创建时间：2019/1/8 13:39
 * 类描述：接口过滤界面
 * 修改人：
 * 修改时间：
 * 修改备注：
 *
 */
class ApiFilterActivity : BaseAppCompatActivity() {

    private var rootView: View? = null
    private var adapter: ApiListAdapter? = null
    private var mRefreshAsyncTask: RefreshAsyncTask? = null
    private var mRefreshLayout: SwipeRefreshLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_api_filter)

        ivBack.setOnClickListener {
            finish()
        }

        adapter = ApiListAdapter(this)
        adapter?.type = 3
        // 设置布局管理器
        val linearLayoutManager = LinearLayoutManager(this)
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
                intent.setClass(this@ApiFilterActivity, ApiDetailActivity::class.java)
                startActivity(intent)
            }
        })

        // 刷新控件
        mRefreshLayout = rootView?.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
        // 列表刷新
        mRefreshLayout?.isEnabled = true
        mRefreshLayout?.isRefreshing = false
        mRefreshLayout?.setColorSchemeColors(Color.parseColor("#1976d2"))
        mRefreshLayout?.setOnRefreshListener {
            loadData(false)
        }
        loadData(false)
    }

    /**
     * 创建人：WangYongQi
     * 创建时间：2019/1/9 15:35
     * 方法描述：加载数据
     **/
    private fun loadData(showToast: Boolean) {
        if (mRefreshAsyncTask == null || mRefreshAsyncTask?.status != AsyncTask.Status.RUNNING) {
            mRefreshAsyncTask = RefreshAsyncTask(adapter, mRefreshLayout, showToast)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                mRefreshAsyncTask?.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
            } else {
                mRefreshAsyncTask?.execute()
            }
        }
    }

    // 刷新，异步任务
    private class RefreshAsyncTask : AsyncTask<String, String, MutableList<Api>> {

        private var mAdapter: ApiListAdapter?
        private var mRefreshLayout: SwipeRefreshLayout?
        private var isShowToast: Boolean = false

        constructor(alAdapter: ApiListAdapter?, refreshLayout: SwipeRefreshLayout?, showToast: Boolean) {
            mAdapter = alAdapter
            mRefreshLayout = refreshLayout
            isShowToast = showToast
        }

        override fun doInBackground(vararg p0: String?): MutableList<Api> {
            return FilterDbHelper.getInstance().getList()
        }

        override fun onPostExecute(list: MutableList<Api>) {
            super.onPostExecute(list)
            mAdapter?.let {
                it.list = list
                it.notifyDataSetChanged()
                val count = it.list.size
                if (isShowToast) {
                    Toast.makeText(BaseApplication.context, "一共找到 $count 条过滤数据", Toast.LENGTH_SHORT).show()
                }
                mRefreshLayout?.let {
                    if (it.isRefreshing) {
                        it.isRefreshing = false
                    }
                }
            }
        }

    }

}