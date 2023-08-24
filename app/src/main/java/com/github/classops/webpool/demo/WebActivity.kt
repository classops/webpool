package com.github.classops.webpool.demo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import com.github.classops.webpool.WebManager
import com.github.classops.webpool.demo.databinding.ActivityWebBinding

class WebActivity : FragmentActivity() {

    companion object {
        fun nav(context: Context, title: String, url: String) {
            val intent = Intent(context, WebActivity::class.java)
            intent.putExtra("title", title)
            intent.putExtra("url", url)
            context.startActivity(intent)
        }
    }

    private lateinit var binding: ActivityWebBinding
//    private lateinit var webViewHelper: WebViewHelper
//    private val webView: WebView
//        get() = this.webViewHelper.webView

    private lateinit var webView: WebView

    private lateinit var url: String

    private var lastTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.lastTime = System.currentTimeMillis()
        this.title = intent.getStringExtra("title") ?: ""
        this.url = intent.getStringExtra("url") ?: ""
        this.binding = ActivityWebBinding.inflate(layoutInflater)
        setContentView(this.binding.root)
        this.binding.toolbar.title = intent.getStringExtra("title") ?: ""
    }

    override fun onStart() {
        super.onStart()
        initViews()
        loadData(null)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        Log.e("Web", "初始化事件：${System.currentTimeMillis() - lastTime}ms")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        this.webView.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        if (canGoBack()) {
            // 返回上一页面
            this.webView.goBack()
            return
        }
        super.onBackPressed()
    }

    private fun initViews() {
//        this.webViewHelper = WebManager.get(this).createWebViewHelper(
//            this,
//            object : WebViewClient() {
//
//            },
//            webInitializer = {
//                // 初始化 WebView
//            }
//        )
//        this.webViewHelper.bind(this)
//        this.webViewHelper.titleData.observe(this) { title ->
//            binding.toolbar.title = title
//        }

        this.webView = WebManager.get(this).getWebView(this)
        initWeb()
        this.binding.root.addView(
            this.webView,
            ConstraintLayout.LayoutParams(0, 0).apply {
                topToBottom = R.id.toolbar
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            }
        )
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWeb() {
        val webSettings = this.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.setSupportZoom(false)
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT
        // 支持通过JS打开新窗口
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.loadsImagesAutomatically = true
        // 设置 AppCache DomStorage 的 存储目录
        webSettings.domStorageEnabled = true
        webSettings.setAppCacheEnabled(true)
        // 40M
        webSettings.setAppCacheMaxSize(40 * 1024 * 1024)
        webSettings.defaultTextEncodingName = "utf-8"
        webSettings.setGeolocationEnabled(true)
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        // this.disableJavascriptDialogBlock(true)
        // 使用 Application context 创建，关闭 自动填充 功能
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.webView.importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO
        }
    }

    private fun loadData(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            Log.d("Cpp", "loadData: $url")
            this.webView.loadUrl(url)
        } else {
            this.webView.restoreState(savedInstanceState)
            Log.d("Cpp", "loadData restore: ${this.webView.url}")
            this.webView.reload()
        }
        Log.e("Web", "loadData")
    }

    fun canGoBack(): Boolean {
        val canBack = webView.canGoBack()
        if (!canBack) {
            return false
        }
        val backForwardList = webView.copyBackForwardList()
        val currentIndex = backForwardList.currentIndex
        if (currentIndex == 1 &&
            backForwardList.getItemAtIndex(0).url == WebManager.ABOUT_BLANK) {
            webView.goBack()
            return false
        }
        return true
    }

}