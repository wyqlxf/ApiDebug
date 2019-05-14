package com.wyq.api.debug.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.Spanned
import android.text.TextUtils
import android.view.View
import android.webkit.WebSettings
import com.wyq.api.debug.R
import com.wyq.api.debug.base.BaseAppCompatActivity
import com.wyq.api.debug.config.Config
import com.wyq.api.debug.database.FavDbHelper
import com.wyq.api.debug.interfaces.OnApiResultListener
import com.wyq.api.debug.request.ApiRequestHelper
import com.wyq.api.debug.util.HostUtil
import com.wyq.library.util.SPUtil
import kotlinx.android.synthetic.main.activity_api_detail.*
import okhttp3.Headers
import thereisnospon.codeview.CodeViewTheme
import java.math.BigDecimal

/**
 * 创建人： WangYongQi
 * 创建时间：2018/7/16.
 * 文件说明：api详情页展示界面
 */

class ApiDetailActivity : BaseAppCompatActivity() {

    private val mHandler = Handler()
    private var progressDialog: ProgressDialog? = null

    @SuppressLint("SetJavaScriptEnabled", "Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_api_detail)

        val name = intent.getStringExtra("name")
        val type = intent.getStringExtra("type")
        var url = intent.getStringExtra("url")
        var parameter = intent.getStringExtra("parameter")
        val page = intent.getStringExtra("page")
        val time = intent.getStringExtra("time") + ""
        val description = intent.getStringExtra("description") + ""

        // 域名转换
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

        // 环境名称
        tvDevelopment.visibility = View.GONE
        when {
            HostUtil.getDomain(url).contains(".debug") -> {
                // debug环境
                tvDevelopment.text = "当前为debug环境"
            }
            HostUtil.getDomain(url).contains(".dev") -> {
                // dev环境
                tvDevelopment.text = "当前为dev环境"
            }
            else -> {
                // 线上环境
                tvDevelopment.text = "当前为线上正式环境"
            }
        }

        // 标题
        if (!TextUtils.isEmpty(name)) {
            tvTitle.text = name
        }

        // 返回按钮
        ivBack.setOnClickListener {
            finish()
        }

        // 标识符
        var identifier = ""
        if (url.contains("?")) {
            identifier = url.subSequence(0, url.indexOf("?")).toString()
        }
        if (FavDbHelper.getInstance().isAlreadyFav(identifier)) {
            ivFav.setImageResource(R.drawable.ic_favorite_white_24dp)
        } else {
            ivFav.setImageResource(R.drawable.ic_favorite_border_white_24dp)
        }

        // 收藏按钮
        ivFav.setOnClickListener {
            if (FavDbHelper.getInstance().isAlreadyFav(identifier)) {
                ivFav.setImageResource(R.drawable.ic_favorite_border_white_24dp)
                FavDbHelper.getInstance().removeFavorite(identifier)
            } else {
                ivFav.setImageResource(R.drawable.ic_favorite_white_24dp)
                FavDbHelper.getInstance().addFavorite(url, identifier, name, type, time, parameter, description)
            }
        }

        // 展开控件
        llExpand.setOnClickListener {
            if (tvExpand.text == "展开更多信息") {
                llOtherInfo.visibility = View.VISIBLE
                tvExpand.text = "收起更多信息"
                ivExpand.setImageResource(R.drawable.ic_expand_less_black_24dp)
                // 滚动到指定位置
                mHandler.post {
                    scrollView.smoothScrollTo(0, tvResult.bottom)
                }
            } else {
                llOtherInfo.visibility = View.GONE
                tvExpand.text = "展开更多信息"
                ivExpand.setImageResource(R.drawable.ic_expand_more_black_24dp)
                // 滚动到底部
                mHandler.post {
                    scrollView.smoothScrollTo(0, llResult.measuredHeight - scrollView.height)
                }
            }
        }

        // 结果展示控件
        codeView.setTheme(CodeViewTheme.ANDROIDSTUDIO).fillColor()
        codeView.setEncode("UTF-8")
        codeView.isVerticalScrollBarEnabled = true
        codeView.isHorizontalScrollBarEnabled = false

        // 设置 WebView中启用文件访问
        codeView.settings.allowFileAccess = true
        // 设置 WebView启用JavaScript执行
        codeView.settings.javaScriptEnabled = true
        // 设置WebView是否应启用对“视口”HTML元标记的支持或应使用宽视口
        codeView.settings.useWideViewPort = true
        // 设置WebView是否以概览模式加载页面，即缩小宽度以适合屏幕的内容
        codeView.settings.loadWithOverviewMode = true
        //设置 WebView 编码格式
        codeView.settings.defaultTextEncodingName = "UTF-8"
        // 设置 WebView 字体的大小，默认大小为 16
        codeView.settings.defaultFontSize = 20
        // 设置 WebView 支持的最小字体大小，默认为 8
        codeView.settings.minimumFontSize = 14
        // 设置 WebView 通过JS打开新窗口
        codeView.settings.javaScriptCanOpenWindowsAutomatically = true
        // 禁止横向滚动
        codeView.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN

        // 接口请求的开始时间
        val startTime = System.currentTimeMillis()

        when (type) {
            "POST", "Post" -> {
                progressDialog = ProgressDialog.show(this, "", "正在请求数据...", true)
                progressDialog?.setCancelable(true)
                parameter = parameter.replace("{", "")
                        .replace("}", "")
                        .replace(" ", "")
                val map = HashMap<String, String>()
                val textArray = if (page == "postman") (parameter.split("#")) else parameter.split(",")

                for (str in textArray) {
                    if (str.contains("=")) {
                        val key = str.subSequence(0, str.indexOf("="))
                        val value = str.subSequence(str.indexOf("=") + 1, str.length)
                        map[key.toString()] = value.toString()
                    }
                }
                codeView.visibility = View.GONE
                llResult.visibility = View.VISIBLE
                ApiRequestHelper.getInstance().doPost(url, map, object : OnApiResultListener {
                    override fun onResult(map: Map<String, Any>) {
                        progressDialog?.let {
                            if (it.isShowing) {
                                it.dismiss()
                            }
                        }
                        llExpand.visibility = View.VISIBLE
                        tvDevelopment.visibility = View.VISIBLE
                        val time = System.currentTimeMillis() - startTime
                        val text = (BigDecimal(time) * BigDecimal(0.001)).setScale(3, BigDecimal.ROUND_HALF_UP)
                        tvTime.text = "数据加载耗时： ${text}秒"
                        when {
                            time > 3000 -> {
                                // 红色
                                tvTime.setTextColor(Color.parseColor("#ff0000"))
                            }
                            time > 2000 -> {
                                // 紫色
                                tvTime.setTextColor(Color.parseColor("#aa00ff"))
                            }
                            time > 1000 -> {
                                // 黄色
                                tvTime.setTextColor(Color.parseColor("#ffd600"))
                            }
                            time > 500 -> {
                                // 蓝色
                                tvTime.setTextColor(Color.parseColor("#00b0ff"))
                            }
                            else -> {
                                // 绿色
                                tvTime.setTextColor(Color.parseColor("#00c853"))
                            }
                        }
                        var cookie = ""
                        var spanned: Spanned? = null
                        var requestHeaders: Headers? = null
                        var responseHeaders: Headers? = null
                        if (map != null) {
                            if (map.containsKey("spanned") && map["spanned"] is Spanned) {
                                spanned = map["spanned"] as Spanned
                            }
                            if (map.containsKey("cookie") && map["cookie"] is String) {
                                cookie = map["cookie"] as String
                            }
                            if (map.containsKey("request_headers") && map["request_headers"] is Headers) {
                                requestHeaders = map["request_headers"] as Headers
                            }
                            if (map.containsKey("response_headers") && map["response_headers"] is Headers) {
                                responseHeaders = map["response_headers"] as Headers
                            }
                        }
                        if (spanned != null) {
                            tvResult.text = spanned
                        } else {
                            tvResult.text = "没有返回数据"
                        }
                        tvCookie.text = cookie.replace(";", ";\n")
                        requestHeaders?.let {
                            for (i in 0 until it.size()) {
                                tvRequestHeaders.text = it.toString()
                            }
                        }
                        responseHeaders?.let {
                            for (i in 0 until it.size()) {
                                tvResponseHeaders.text = it.toString()
                            }
                        }
                    }
                })
            }
            "GET", "Get" -> {
                progressDialog = ProgressDialog.show(this, "", "正在请求数据...", true)
                progressDialog?.setCancelable(true)
                codeView.visibility = View.GONE
                llResult.visibility = View.VISIBLE
                ApiRequestHelper.getInstance().doGet(url, object : OnApiResultListener {
                    override fun onResult(map: Map<String, Any>) {
                        progressDialog?.let {
                            if (it.isShowing) {
                                it.dismiss()
                            }
                        }
                        llExpand.visibility = View.VISIBLE
                        tvDevelopment.visibility = View.VISIBLE
                        val time = System.currentTimeMillis() - startTime
                        val text = (BigDecimal(time) * BigDecimal(0.001)).setScale(3, BigDecimal.ROUND_HALF_UP)
                        tvTime.text = "数据加载耗时： ${text}秒"
                        when {
                            time > 3000 -> {
                                // 红色
                                tvTime.setTextColor(Color.parseColor("#ff0000"))
                            }
                            time > 2000 -> {
                                // 紫色
                                tvTime.setTextColor(Color.parseColor("#aa00ff"))
                            }
                            time > 1000 -> {
                                // 黄色
                                tvTime.setTextColor(Color.parseColor("#ffd600"))
                            }
                            time > 500 -> {
                                // 蓝色
                                tvTime.setTextColor(Color.parseColor("#00b0ff"))
                            }
                            else -> {
                                // 绿色
                                tvTime.setTextColor(Color.parseColor("#00c853"))
                            }
                        }
                        var cookie = ""
                        var spanned: Spanned? = null
                        var requestHeaders: Headers? = null
                        var responseHeaders: Headers? = null
                        if (map != null) {
                            if (map.containsKey("spanned") && map["spanned"] is Spanned) {
                                spanned = map["spanned"] as Spanned
                            }
                            if (map.containsKey("cookie") && map["cookie"] is String) {
                                cookie = map["cookie"] as String
                            }
                            if (map.containsKey("request_headers") && map["request_headers"] is Headers) {
                                requestHeaders = map["request_headers"] as Headers
                            }
                            if (map.containsKey("response_headers") && map["response_headers"] is Headers) {
                                responseHeaders = map["response_headers"] as Headers
                            }
                        }
                        if (spanned != null) {
                            tvResult.text = spanned
                        } else {
                            tvResult.text = "没有返回数据"
                        }
                        tvCookie.text = cookie.replace(";", ";\n")
                        requestHeaders?.let {
                            for (i in 0 until it.size()) {
                                tvRequestHeaders.text = it.toString()
                            }
                        }
                        responseHeaders?.let {
                            for (i in 0 until it.size()) {
                                tvResponseHeaders.text = it.toString()
                            }
                        }
                    }

                })
            }
            else -> {
                // 网页展示
                codeView.loadUrl(url)
                llResult.visibility = View.GONE
                codeView.visibility = View.VISIBLE
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler?.removeCallbacksAndMessages(null)
    }

}