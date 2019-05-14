package com.wyq.api.debug.activity

import android.annotation.TargetApi
import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.wyq.api.debug.R
import com.wyq.api.debug.base.BaseAppCompatActivity
import com.wyq.api.debug.util.*
import java.lang.ref.WeakReference
import java.util.*

/**
 * 内嵌页（通用版）
 * WebView内存泄露的情况还是很常见的，尤其是当你加载的页面比较庞大的时候，
 * 因此我们是可以在加载WebView页面的activity中单独开启一个新的进程，这样就能和我们app的主进程分开了，即使WebView产生了崩溃等问题也不会影响到主程序;
 * android:process=".web"
 * 但是，如果单独开启一个新的进程，每次进入内嵌页都比较慢，所以我们这里选择另外一种方式，通过java反射机制去解决WebView内存泄露的问题。
 * 新增时间：2019年 3月份
 * 最低支持版本名：>=3.30.4
 * 最低支持版本号：>=118
 */

class CommonBrowserActivity : BaseAppCompatActivity() {

    // 模式，0代表正常模式；1代表沉浸模式；2代表全屏模式
    private var mode = 0
    // 是否显示头部，默认显示
    private var isShowHead = true
    // 内嵌页链接
    private var mUrl: String = ""
    // 内嵌页标题
    private var mTitle: String = ""
    // app版本
    private var version: String = ""
    // 与JS互调的接口名
    private var javaScriptInterfaceName: String = ""

    // 加载网页的控件
    private var mWebView: WebView? = null
    // 网页控件的布局容器
    private var mLayoutBody: LinearLayout? = null

    // 标题
    private var tvTitle: TextView? = null
    // 返回按钮
    private var ivLeftBack: ImageView? = null

    // 声明handler
    private var mHandler: CommonHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 初始化数据
        initData()
        // 隐藏标题
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        when (mode) {
            1 ->
                // 沉浸模式
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val window = window
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    window.statusBarColor = Color.TRANSPARENT
                }
            2 ->
                // 设置全屏
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        // 设置界面
        setContentView(R.layout.activity_common_browser)
        // 初始化组件
        initView()
        // 加载链接
        loadUrl(mUrl)
    }

    /**
     * 初始化数据
     */
    private fun initData() {
        mHandler = CommonHandler(this)
        val intent = intent
        val bundle = intent.extras
        isShowHead = BundleUtil.getBoolean(bundle, "is_show_head", true)
        mode = BundleUtil.getInt(bundle, "mode", 0)
        javaScriptInterfaceName = BundleUtil.getString(bundle, "java_script_interface_name") + ""
        version = BundleUtil.getString(bundle, "version") + ""
        // 搜索关键字
        val filterKeyword = BundleUtil.getString(bundle, "filter_keyword")
        mTitle = BundleUtil.getString(bundle, "title") + ""
        mUrl = BundleUtil.getString(bundle, "url") + ""
        if (!TextUtils.isEmpty(mUrl) && mUrl!!.startsWith("http") && !TextUtils.isEmpty(filterKeyword)) {
            mUrl = "$mUrl&=$filterKeyword"
        }
    }

    /**
     * 初始化组件
     */
    private fun initView() {
        /**
         * 动态添加WebView，解决内存占回收用无效的问题，
         * WebView构建时如果传入Activity的Context的话，对内存的引用会一直被保持着；
         * WebView构建时如果传入Activity的ApplicationContext的话，可以防止内存溢出，但是有个问题：
         * 如果你需要在WebView中打开链接或者你打开的页面带有flash，或者你的WebView想弹出一个dialog，
         * 都会导致从ApplicationContext到ActivityContext的强制类型转换错误，从而导致你应用崩溃，
         * 这是因为在加载flash的时候，系统会首先把你的WebView作为父控件，然后在该控件上绘制flash，
         * 它想找一个Activity的Context来绘制他，但是你传入的是ApplicationContext。
         */
        mWebView = WebView(this)
        val webSettings = mWebView?.settings
        // 默认是false 设置true允许和js交互
        webSettings?.javaScriptEnabled = true
        // 设置WebView是否使用viewport
        webSettings?.useWideViewPort = true
        // 设置WebView是否使用预览模式加载界面
        webSettings?.loadWithOverviewMode = true
        // 设置在WebView内部是否允许访问文件，默认允许访问
        webSettings?.allowFileAccess = true
        // 是否允许在WebView中访问内容URL（Content Url），默认允许
        webSettings?.allowContentAccess = true
        // 设置脚本是否允许自动打开弹窗，默认（false）不允许，适用于JavaScript方法window.open()
        webSettings?.javaScriptCanOpenWindowsAutomatically = true
        // 设置WebView是否不应从网络加载图像资源（通过http和https URI方案访问的资源）,解决图像不显示问题
        webSettings?.blockNetworkImage = false
        // 设置字体百分比，适配内嵌页布局
        webSettings?.textZoom = 100
        // 设置WebView是否应支持使用其屏幕缩放控件和手势进行缩放，默认值true
        webSettings?.setSupportZoom(true)
        // 设置WebView是否应使用其内置缩放机制，默认值true
        webSettings?.builtInZoomControls = false
        // 设置使用内置缩放机制时WebView是否应显示屏幕缩放控件，默认值为false
        webSettings?.displayZoomControls = false
        // 设置默认的字符编码集，默认"UTF-8"
        webSettings?.defaultTextEncodingName = "UTF-8"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // 设置是否应允许在文件方案URL上下文中运行的JavaScript访问其他文件方案URL中的内容
            webSettings?.allowFileAccessFromFileURLs = true
            // 设置是否应允许在文件方案URL的上下文中运行的JavaScript访问来自任何源的内容
            webSettings?.allowUniversalAccessFromFileURLs = true
        }
        /**
         * 解决5.0 以后的WebView加载的链接为Https开头，但是链接里面的内容，比如图片链接为Http就会加载不出来
         * MIXED_CONTENT_ALWAYS_ALLOW：允许从任何来源加载内容，即使起源是不安全的；
         * MIXED_CONTENT_NEVER_ALLOW：不允许Https加载Http的内容，即不允许从安全的起源去加载一个不安全的资源；
         * MIXED_CONTENT_COMPATIBILITY_MODE：当涉及到混合式内容时，WebView 会尝试去兼容最新Web浏览器的风格。
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings?.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        /**
         * Android WebView自带的缓存机制：
         * 浏览器缓存机制
         * Application Cache 缓存机制
         * Dom Storage 缓存机制
         * Web SQL Database 缓存机制（不再推荐使用，不再维护，取而代之的是Indexed Database 缓存机制）
         * Android 在4.4开始加入对 IndexedDB 的支持，只需打开允许 JS 执行的开关就好了
         */
        // 开启 DOM storage API 功能 较大存储空间（5MB），Dom Storage 机制类似于 Android 的 SharedPreference机制
        webSettings?.domStorageEnabled = true
        // 开启 Application Caches 功能 方便构建离线APP
        webSettings?.setAppCacheEnabled(true)
        /**
         * 设置缓存模式
         * LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
         * LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
         * LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
         * LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
         * 如果有网络，则走浏览器缓存机制。
         * 根据 HTTP 协议头里的 Cache-Control（或 Expires）和 Last-Modified（或 ETag）等字段来控制文件缓存的机制
         * Cache-Control：用于控制文件在本地缓存有效时长，如服务器回包：Cache-Control:max-age=600，则表示文件在本地应该缓存，且有效时长是600秒（从发出请求算起）；
         * 在接下来600秒内，如果有请求这个资源，浏览器不会发出 HTTP 请求，而是直接使用本地缓存的文件。
         * Expires：与Cache-Control功能相同，即控制缓存的有效时间，Expires是 HTTP1.0 标准中的字段，
         * Cache-Control 是 HTTP1.1 标准中新加的字段，当这两个字段同时出现时，Cache-Control 优先级较高。
         * Last-Modified：标识文件在服务器上的最新更新时间，下次请求时，如果文件缓存过期，浏览器通过 If-Modified-Since 字段带上这个时间，发送给服务器，
         * 由服务器比较时间戳来判断文件是否有修改。如果没有修改，服务器返回304告诉浏览器继续使用缓存；如果有修改，则返回200，同时返回最新的文件。
         * ETag：功能同Last-Modified ，即标识文件在服务器上的最新更新时间，不同的是，ETag 的取值是一个对文件进行标识的特征字串。
         * ETag 和 Last-Modified 可根据需求使用一个或两个同时使用。两个同时使用时，只要满足基中一个条件，就认为文件没有更新。
         * 常见用法是：Cache-Control与 Last-Modified 一起使用；Expires与 ETag一起使用。
         * 浏览器缓存机制 是 浏览器内核的机制，一般都是标准的实现。
         */
        webSettings?.cacheMode = WebSettings.LOAD_DEFAULT

        /**
         * 1. 接口互调引起远程代码执行漏洞
         * 漏洞产生原因是：当JS拿到Android这个对象后，就可以调用这个Android对象中所有的方法，包括系统类（java.lang.Runtime 类），从而进行任意代码执行。
         * 在Android 4.2版本之前，采用拦截prompt（）进行漏洞修复
         * 在Android 4.2版本之后Google规定对被调用的函数以 @JavascriptInterface进行注解从而避免漏洞攻击;
         *
         * 2.searchBoxJavaBridge_接口引起远程代码执行漏洞
         * 漏洞产生原因：在Android 3.0以下，Android系统会默认通过searchBoxJavaBridge_的Js接口给 WebView 添加一个JS映射对象：searchBoxJavaBridge_对象，
         * 该接口可能被利用，实现远程任意代码，删除searchBoxJavaBridge_接口即可。
         * 当系统辅助功能服务被开启时，在 Android 4.4 以下的系统中，由系统提供的 WebView 组件都默认导出 accessibility 和 accessibilityTraversal 这两个接口，
         * 它们同样存在远程任意代码执行的威胁，同样的需要通过 removeJavascriptInterface 方法将这两个对象删除
         */
        try {
            mWebView?.removeJavascriptInterface("searchBoxJavaBridge_")
            mWebView?.removeJavascriptInterface("accessibility")
            mWebView?.removeJavascriptInterface("accessibilityTraversal")
        } catch (ex: Exception) {
        }

        // 如果Android与JS互调的接口名不为空
        if (!TextUtils.isEmpty(javaScriptInterfaceName)) {
            mWebView?.addJavascriptInterface(JavaScriptInterfaceClass(), javaScriptInterfaceName)
        }

        // 设置WebViewClient，处理各种通知和请求事件
        mWebView?.webViewClient = object : WebViewClient() {

            /**
             * url重定向会执行此方法以及点击页面某些链接也会执行此方法
             * @param view
             * @param url
             * @return true:表示当前url已经加载完成，即使url还会重定向都不会再进行加载 false 表示此url默认由系统处理，该重定向还是重定向，直到加载完成
             */
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                return isShouldOverrideUrlLoading(url + "")
            }

            /**
             * url重定向会执行此方法以及点击页面某些链接也会执行此方法
             * @param view
             * @param request
             * @return true:表示当前url已经加载完成，即使url还会重定向都不会再进行加载 false 表示此url默认由系统处理，该重定向还是重定向，直到加载完成
             */
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url.toString() + ""
                return isShouldOverrideUrlLoading(url)
            }

            // 开始载入页面调用的
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                setBlockNetworkImage(true)
            }

            // 在页面加载结束时调用
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                setBlockNetworkImage(false)
            }
        }

        // 设置WebChromeClient，辅助 WebView 处理 Javascript 的对话框,网站图标,网站标题等等
        mWebView?.webChromeClient = object : WebChromeClient() {

            // 获取网页的加载进度
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress >= 100) {
                    setBlockNetworkImage(false)
                }
            }

            // 获取网页中的标题
            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                // 显示规则，优先显示传过来的标题，如果没有传标题则取网页标题显示
                if (!TextUtils.isEmpty(mTitle)) {
                    tvTitle!!.text = mTitle
                } else if (!TextUtils.isEmpty(title)) {
                    tvTitle!!.text = title
                } else {
                    tvTitle!!.text = ""
                }
            }
        }

        // 找到布局，并且添加WebView
        mLayoutBody = findViewById(R.id.llBody)
        // 添加WebView
        mLayoutBody?.addView(mWebView)
        try {
            // 设置WebView宽高，必须是在布局添加WebView完成之后执行
            val dm = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(dm)
            val width = dm.widthPixels
            val height = dm.heightPixels
            val linearParams = mWebView!!.layoutParams
            linearParams.width = width
            linearParams.height = height
            mWebView!!.layoutParams = linearParams
        } catch (ex: Exception) {
        }

        // 头部布局
        val rlHead = findViewById<RelativeLayout>(R.id.rlHead)
        rlHead.visibility = if (isShowHead) View.VISIBLE else View.GONE
        // 标题
        tvTitle = findViewById(R.id.tvTitle)
        if (!TextUtils.isEmpty(mTitle)) {
            tvTitle?.text = mTitle
        }
        // 返回按钮
        ivLeftBack = findViewById(R.id.ivLeftBack)
        ivLeftBack?.setOnClickListener { onBack() }
    }

    /**
     * 加载URL
     */
    private fun loadUrl(url: String?) {
        if (mWebView != null) {
            // 最后拼接完成的URL
            val lastUrl = getBuildLinkUrl(url)
            try {
                // 加载URL
                mWebView!!.loadUrl(lastUrl)
            } catch (ex: Exception) {
            }

        }
    }

    /**
     * 是否应该覆盖URL加载及逻辑处理
     *
     * @param url
     * @return
     */
    private fun isShouldOverrideUrlLoading(url: String): Boolean {
        val whatsAppMark = "whatsapp://send?"
        val fbAppMark = "https://www.facebook.com/sharer/sharer.php?u="
        if (url.contains(whatsAppMark)) {
            try {
                if (AppUtil.isAppAvailable(applicationContext, "com.whatsapp")) {
                    // whatsApp分享
                    val intent = Intent()
                    intent.action = "android.intent.action.VIEW"
                    val contentUrl = Uri.parse(url)
                    intent.data = contentUrl
                    startActivity(intent)
                    return true
                }
            } catch (ex: Exception) {
            }

        } else if (url.contains(fbAppMark)) {
            try {
                if (AppUtil.isAppAvailable(applicationContext, "com.facebook.katana")) {
                    // Facebook分享
                    val fbText = url.substring(url.indexOf(fbAppMark) + fbAppMark.length)
                    if (!TextUtils.isEmpty(fbText)) {
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.component = ComponentName("com.facebook.katana",
                                "com.facebook.composer.shareintent.ImplicitShareIntentHandlerDefaultAlias")
                        //这里就是组织内容了
                        shareIntent.type = "text/plain"
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "分享資訊")
                        shareIntent.putExtra(Intent.EXTRA_TEXT, fbText)
                        shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(shareIntent)
                        return true
                    }
                }
            } catch (ex: Exception) {
            }

        } else if (url.startsWith("tel:")) {
            try {
                // 拨打电话
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            } catch (ex: Exception) {
            }

            return true
        } else if (url.startsWith("mailto:")) {
            try {
                // 发送短信
                val i = Intent(Intent.ACTION_SENDTO, Uri.parse(url))
                startActivity(i)
            } catch (ex: Exception) {
            }

            return true
        }
        loadUrl(url)
        return true
    }

    /**
     * 设置图像加载是否受阻
     *
     * @param isBlock
     */
    private fun setBlockNetworkImage(isBlock: Boolean) {
        mWebView?.let {
            if (isBlock) {
                // 阻止图像加载
                it.settings.blockNetworkImage = true
            } else {
                // 允许图像加载
                it.settings.blockNetworkImage = false
                if (!it.settings.loadsImagesAutomatically) {
                    // 设置wenView加载图片资源
                    it.settings.loadsImagesAutomatically = true
                }
            }
        }
    }

    /**
     * 在Java 中，非静态的内部类和匿名内部类都会隐式地持有其外部类的引用，静态的内部类不会持有外部类的引用
     */
    private class CommonHandler(activity: CommonBrowserActivity) : Handler() {

        private val mWeakReference: WeakReference<CommonBrowserActivity> = WeakReference(activity)

        override fun handleMessage(msg: Message) {
            val activity = mWeakReference.get()
            if (activity != null && !activity.isFinishing) {
                when (msg.what) {
                    WHAT_GO_BACK -> activity.onBack()
                    WHAT_APP_SHARE -> {
                        val objShare = msg.obj
                        if (objShare != null && objShare is String) {
                            activity.onShare(objShare)
                        }
                    }
                    WHAT_OPEN_BROWSER -> {
                        val objBrowser = msg.obj
                        if (objBrowser != null && objBrowser is String) {
                            activity.onBrowser(objBrowser)
                        }
                    }
                    WHAT_JUMP_ACTIVITY -> try {
                        val objJump = msg.obj
                        if (objJump != null && objJump is HashMap<*, *>) {
                            val map = objJump as HashMap<String, String>
                            if (map != null) {
                                // 获取跳转的页面
                                val className = if (map.containsKey("class_name")) map["class_name"] else ""
                                // 获取传值的参数
                                val jsonExtras = if (map.containsKey("json_extras")) map["json_extras"] else ""
                                if (!TextUtils.isEmpty(className) && className != "undefined") {
                                    val intent = Intent()
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                    intent.action = Intent.ACTION_VIEW
                                    if (!TextUtils.isEmpty(jsonExtras) && jsonExtras != "undefined") {
                                        val extras = Bundle()
                                        val items = JSONAnalyze.getJSONArray(jsonExtras)
                                        if (items != null && items.length() > 0) {
                                            for (i in 0 until items.length()) {
                                                val itemsObject = JSONAnalyze.getJSONObject(items, i)
                                                if (itemsObject != null) {
                                                    val key = JSONAnalyze.getJSONValue(itemsObject, "key")
                                                    val value = JSONAnalyze.getJSONValue(itemsObject, "value")
                                                    if (!TextUtils.isEmpty(key)) {
                                                        extras.putString(key, value)
                                                    }
                                                }
                                            }
                                        }
                                        intent.putExtras(extras)
                                    }
                                    // 类反射机制
                                    val clazz = Class.forName(className)
                                    intent.setClass(activity, clazz)
                                    activity.startActivity(intent)
                                }
                            }
                        }
                    } catch (e: Exception) {
                    }

                }
            }
        }
    }

    private inner class JavaScriptInterfaceClass {

        /**
         * app回退
         */
        @JavascriptInterface
        fun backPage() {
            if (mHandler != null) {
                mHandler!!.sendEmptyMessage(WHAT_GO_BACK)
            }
        }

        /**
         * app分享
         *
         * @param text
         */
        @JavascriptInterface
        fun appShare(text: String) {
            if (TextUtils.isEmpty(text) || text == "undefined") {
                return
            }
            if (mHandler != null) {
                // 避免重复创建Message对象
                val msg = Message.obtain()
                msg.obj = text
                msg.what = WHAT_APP_SHARE
                mHandler!!.sendMessage(msg)
            }
        }

        /**
         * 调用第三方浏览器
         *
         * @param url
         */
        @JavascriptInterface
        fun openBrowser(url: String) {
            if (TextUtils.isEmpty(url) || url == "undefined") {
                return
            }
            if (mHandler != null) {
                // 避免重复创建Message对象
                val msg = Message.obtain()
                msg.obj = url
                msg.what = WHAT_OPEN_BROWSER
                mHandler!!.sendMessage(msg)
            }
        }

        /**
         * 跳转指定界面
         *
         * @param className
         * @param jsonExtras
         */
        @JavascriptInterface
        fun jumpActivity(className: String, jsonExtras: String, supportMinVersion: String) {
            if (TextUtils.isEmpty(className) || className == "undefined") {
                return
            }
            try {
                // 最低支持的版本，（默认可支持任意版本）
                var minVersion = 0
                if (!TextUtils.isEmpty(supportMinVersion) && supportMinVersion != "undefined") {
                    minVersion = Integer.parseInt(supportMinVersion)
                }
                val packageInfo = packageManager.getPackageInfo(packageName, 0)
                val curVersionCode = packageInfo.versionCode.toLong()
                if (curVersionCode < minVersion) {
                    return
                }
            } catch (ex: Exception) {
            }

            if (mHandler != null) {
                val map = HashMap<String, String>()
                map["class_name"] = "" + className
                map["json_extras"] = "" + jsonExtras
                // 避免重复创建Message对象
                val msg = Message.obtain()
                msg.obj = map
                msg.what = WHAT_JUMP_ACTIVITY
                mHandler!!.sendMessage(msg)
            }
        }
    }

    /**
     * 链接拼接
     *
     * @param url
     * @return
     */
    private fun getBuildLinkUrl(url: String?): String? {
        if (!TextUtils.isEmpty(url) && url!!.startsWith("http")) {
            val buffer = StringBuffer()
            if (!url.contains("device")) {
                buffer.append("&device=android")
            }
            if (!url.contains("idcode")) {
                buffer.append("&idcode=" + MobileUtil.soleId)
            }
            if (!url.contains("status_bar_height") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                buffer.append("&status_bar_height=" + ScreenSize.pxToDip(this, ScreenSize.getStatusBarHeight(this).toFloat()))
            }
            if (!TextUtils.isEmpty(buffer.toString())) {
                if (url.contains("?")) {
                    if (url.endsWith("?") || url.endsWith("&")) {
                        var sub = buffer.toString()
                        try {
                            sub = sub.substring(sub.indexOf("&") + 1)
                        } catch (ex: Exception) {
                        }

                        return url + sub
                    } else {
                        return url + buffer.toString()
                    }
                } else {
                    var sub = buffer.toString()
                    try {
                        sub = sub.substring(sub.indexOf("&") + 1)
                    } catch (ex: Exception) {
                    }

                    return "$url?$sub"
                }
            } else {
                return url
            }
        } else {
            return url
        }
    }

    /**
     * 调用系统浏览器
     */
    private fun onBrowser(url: String) {
        try {
            if (url.startsWith("http")) {
                val uri = Uri.parse(url)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
        } catch (ex: Exception) {
        }

    }

    /**
     * 分享
     *
     * @param text
     */
    private fun onShare(text: String) {
        try {
            val jsonObject = JSONAnalyze.getJSONObject(text)
            val title = JSONAnalyze.getJSONValue(jsonObject, "title")
            val url = JSONAnalyze.getJSONValue(jsonObject, "url")
            val pageType = JSONAnalyze.getJSONValue(jsonObject, "pageType")
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, pageType)
            intent.putExtra(Intent.EXTRA_TEXT, "" + title + "\n" + url)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(Intent.createChooser(intent, "分享給好友"))
        } catch (ex: Exception) {
        }

    }

    /**
     * 返回
     */
    private fun onBack() {
        if (mWebView != null && mWebView!!.canGoBack()) {
            mWebView!!.goBack()
        } else {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        if (mWebView != null) {
            /**
             * 执行自己的生命周期
             */
            mWebView!!.onResume()
            // 恢复所有WebView的所有布局，解析和JavaScript计时器，将恢复调度所有计时器
            mWebView!!.resumeTimers()
            // 恢复与JS交互
            mWebView!!.settings.javaScriptEnabled = true
        }
    }

    override fun onPause() {
        super.onPause()
        if (mWebView != null) {
            /**
             * 执行自己的生命周期
             * 通知内核尝试停止所有处理，如动画和地理位置，但是不能停止Js，如果想全局停止Js，可以调用pauseTimers()全局停止Js，调用onResume()恢复。
             */
            mWebView!!.onPause()
            // 暂停所有WebView的布局，解析和JavaScript定时器。 这个是一个全局请求，不仅限于这个WebView
            mWebView!!.pauseTimers()
            // 禁用与JS交互，防止后台无法释放js 导致耗电
            mWebView!!.settings.javaScriptEnabled = false
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK) {
            if (event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0) {
                onBack()
            }
            return true
        }
        return super.dispatchKeyEvent(event)
    }

    /**
     * 释放WebView，避免内存泄漏
     */
    private fun releaseAllWebViewCallback() {
        if (Build.VERSION.SDK_INT < 16) {
            try {
                var field = WebView::class.java.getDeclaredField("mWebViewCore")
                field = field.type.getDeclaredField("mBrowserFrame")
                field = field.type.getDeclaredField("sConfigCallback")
                field.isAccessible = true
                field.set(null, null)
            } catch (e: Exception) {
            }

        } else {
            try {
                val sConfigCallback = Class.forName("android.webkit.BrowserFrame").getDeclaredField("sConfigCallback")
                if (sConfigCallback != null) {
                    sConfigCallback.isAccessible = true
                    sConfigCallback.set(null, null)
                }
            } catch (e: Exception) {
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 停止浏览器缓存
        if (mWebView != null) {
            /**
             * 因为WebView构建时传入了该Activity的context对象，
             * 所以需要先从父容器中移除WebView，然后再销毁WebView
             */
            if (mLayoutBody != null) {
                mLayoutBody?.removeView(mWebView)
            }
            // 停止加载
            mWebView?.stopLoading()
            // 解决getSettings().setBuiltInZoomControls(true) 引发的crash的问题
            mWebView?.visibility = View.GONE
            // 移除所有组件
            mWebView?.removeAllViews()
            // 销毁WebView，避免OOM异常
            mWebView?.destroy()
            // WebView对象置空
            mWebView = null
            // LayoutBody对象置空
            mLayoutBody = null
            // 释放WebView
            releaseAllWebViewCallback()
        }
        if (mHandler != null) {
            // 删除handler所有的消息和回调函数，避免内存泄漏
            mHandler?.removeCallbacksAndMessages(null)
            mHandler = null
        }
    }

    companion object {

        // app返回
        private const val WHAT_GO_BACK = 0
        // app分享
        private const val WHAT_APP_SHARE = 1
        // 调用第三方浏览器
        private const val WHAT_OPEN_BROWSER = 2
        // 跳转指定界面
        private const val WHAT_JUMP_ACTIVITY = 3
    }

}
