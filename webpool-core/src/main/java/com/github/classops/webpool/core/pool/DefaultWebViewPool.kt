package com.github.classops.webpool.core.pool

import android.content.ComponentCallbacks2
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import com.github.classops.webpool.core.TAG
import com.github.classops.webpool.core.WebState
import com.github.classops.webpool.core.WebViewFactory
import com.github.classops.webpool.core.WebViewPool
import com.github.classops.webpool.core.webState
import java.util.ArrayDeque

/**
 * 默认缓存池基类
 */
open class DefaultWebViewPool<T : ViewGroup>(
    private val context: Context,
    private val maxSize: Int,
    private val webViewFactory: WebViewFactory<T>
) : WebViewPool<T> {

    private val queue = ArrayDeque<T>()

    override fun getPoolMaxSize(): Int {
        return this.maxSize
    }

    override fun getCurrentSize(): Int {
        return this.queue.size
    }

    override fun put(webView: T) {
        val webState = webView.webState
        if (this.queue.size < this.maxSize && webState == WebState.IDLE) {
            this.queue.addFirst(webView)
            Log.d(TAG, "web poll put webview: ${webView.hashCode()}，cache size：${this.queue.size}")
        }
    }

    override fun get(context: Context?): T {
        val webView = this.queue.find {
            it.webState == WebState.IDLE
        }

        return if (webView != null) {
            Log.d(TAG, "get cached webView: ${webView.hashCode()}")
            this.queue.remove(webView)
            webView
        } else {
            createWebView(context ?: this.context)
        }
    }

    private fun createWebView(context: Context): T {
        // 创建WebView，初始化配置
        return webViewFactory.create(context)
    }

    override fun clear() {
        clearInternal(0)
    }

    override fun clean(webView: T): Boolean {
        put(webView)
        return true
    }

    private fun clearInternal(keep: Int) {
        while (queue.size > keep) {
            val webView = queue.pollLast() ?: break
            destroyWebView(webView)
        }
    }

    override fun trimMemory(level: Int) {
        // 回收内存
        when (level) {
            ComponentCallbacks2.TRIM_MEMORY_COMPLETE -> clearInternal(0)

            ComponentCallbacks2.TRIM_MEMORY_MODERATE,
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL,
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW -> clearInternal(1)
        }
    }

    open fun destroyWebView(webView: T) {

    }

}