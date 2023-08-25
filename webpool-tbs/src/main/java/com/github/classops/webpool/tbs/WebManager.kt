package com.github.classops.webpool.tbs

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import com.github.classops.webpool.core.WebCallback
import com.github.classops.webpool.core.WebCallbackProvider
import com.github.classops.webpool.core.WebViewPool
import com.github.classops.webpool.tbs.factory.DefaultWebViewFactory
import com.github.classops.webpool.tbs.webpool.LfuWebViewPool
import com.tencent.smtt.sdk.WebView

/**
 * WebView管理类
 */
class WebManager(
    private val context: Context,
    var webViewPool: WebViewPool<WebView> = LfuWebViewPool(
        context,
        3,
        DefaultWebViewFactory()
    )
) {

    companion object {
        internal const val TAG = "WebViewPool"

        const val ABOUT_BLANK = "about:blank"

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var sWebManager: WebManager? = null

        fun get(context: Context): WebManager {
            if (sWebManager == null) {
                synchronized(WebManager::class.java) {
                    if (sWebManager == null) {
                        sWebManager = WebManager(context.applicationContext)
                    }
                }
            }
            return sWebManager!!
        }

        /**
         * 判断是否可以后退，处理了 缓存的最后空白页 问题
         */
        fun canGoBack(webView: WebView): Boolean {
            val canBack = webView.canGoBack()
            if (!canBack) {
                return false
            }
            val backForwardList = webView.copyBackForwardList()
            if (backForwardList.currentIndex == 1 &&
                backForwardList.getItemAtIndex(0).url == ABOUT_BLANK
            ) {
                webView.goBack()
                return false
            }
            return true
        }
    }

    private var isPreCreated = false

    val currentPoolSize: Int
        get() = this.webViewPool.getCurrentSize()

    var webCallback: WebCallback<WebView>? = null

    private val webCallbackProvider = object : WebCallbackProvider<WebView> {
        override fun getWebCallback(): WebCallback<WebView>? {
            return this@WebManager.webCallback
        }
    }


    /**
     * 空闲时预创建WebView，返回false仅执行一次
     */
    fun idleCreate() {
        Looper.myQueue().addIdleHandler {
            preCreate()
            false
        }
    }

    fun preCreate() {
        if (currentPoolSize > 0 || isPreCreated) return
        Log.d(TAG, "preCreate")
        val webView = webViewPool.get(null)
        webViewPool.put(webView)
        this.isPreCreated = true
    }

    fun getWebView(activity: ComponentActivity, attach: Boolean = true): WebView {
        val webView = webViewPool.get(activity)
        val helper = WebViewHelper(this.context, webView, this.webViewPool, this.webCallbackProvider)
        if (attach) {
            helper.attach(activity)
        }
        return webView
    }

    fun getWebView(fragment: Fragment, attach: Boolean = true): WebView {
        val webView = webViewPool.get(fragment.requireContext())
        val helper = WebViewHelper(this.context, webView, this.webViewPool, this.webCallbackProvider)
        if (attach) {
            helper.attach(fragment)
        }
        return webView
    }

    fun trimMemory(level: Int) {
        Log.d(TAG, "trimMemory: $level")
        this.webViewPool.trimMemory(level)
    }

}