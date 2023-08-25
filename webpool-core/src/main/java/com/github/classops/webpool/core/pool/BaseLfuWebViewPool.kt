package com.github.classops.webpool.core.pool

import android.content.ComponentCallbacks2
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import com.github.classops.webpool.core.R
import com.github.classops.webpool.core.TAG
import com.github.classops.webpool.core.WebState
import com.github.classops.webpool.core.WebViewFactory
import com.github.classops.webpool.core.WebViewPool
import com.github.classops.webpool.core.isWebDirty
import com.github.classops.webpool.core.webState
import java.util.Collections

/**
 * LFU方式的缓存池基类
 */
open class BaseLfuWebViewPool<T : ViewGroup>(
    private val context: Context,
    private val maxSize: Int,
    private val webViewFactory: WebViewFactory<T>
) : WebViewPool<T> {

    private val queue = ArrayDeque<T>()
    private val dirtyQueue = ArrayDeque<T>()
    private val comparator = Comparator<T> { o1, o2 ->
        val o1Num = getUsageCount(o1)
        val o2Num = getUsageCount(o2)
        o1Num - o2Num
    }

    override fun getPoolMaxSize(): Int {
        return this.maxSize
    }

    override fun getCurrentSize(): Int {
        return this.queue.size
    }

    override fun put(webView: T) {
        if (webView.isWebDirty) {
            this.dirtyQueue.addLast(webView)
            return
        }

        if (webView.webState != WebState.IDLE) return

        if (this.queue.contains(webView)) return

        this.beforePut(webView)
        this.queue.addLast(webView)
        Collections.sort(this.queue, comparator)
        val count = this.queue.size - this.maxSize
        if (count > 0) {
            for (i in 0 until count) {
                val view = this.queue.removeLastOrNull() ?: break
                destroyWebView(view)
            }
        }

        Log.d(TAG, "web pool，cache size：${this.queue.size}, count：${getUsageCount(webView)}")
    }

    override fun get(context: Context?): T {
//        val webView =
//            this.queue.removeFirstOrNull() ?: webViewFactory.create(context ?: this.context)

        val webView: T
        this.queue.removeLastOrNull().let {
            if (it == null) {
                webView = webViewFactory.create(context ?: this.context)
                Log.e(TAG, "get webView: ${webView.hashCode()}")
            } else {
                webView = it
                Log.d(TAG, "get cached webView: ${webView.hashCode()}")
            }
        }
        incUsageCount(webView)
        this.beforeGet(webView)
        return webView
    }

    override fun clear() {
        clearInternal(0)
    }

    override fun clean(webView: T): Boolean {
        return if (this.dirtyQueue.remove(webView)) {
            put(webView)
            true
        } else {
            false
        }
    }

    protected open fun beforePut(webView: T) {

    }

    protected open fun beforeGet(webView: T) {

    }

    private fun clearInternal(keep: Int) {
        while (this.queue.size > keep) {
            val webView = this.queue.removeLastOrNull() ?: break
            destroyWebView(webView)
        }

        Log.d(TAG, "clear webView, count: ${this.queue.size}")
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
        Log.d(TAG, "destroy webView: ${webView.hashCode()}")
    }

    private fun getUsageCount(webView: T?): Int {
        return webView?.getTag(R.id.web_usage_count) as? Int ?: 0
    }

    private fun incUsageCount(webView: T) {
        webView.setTag(
            R.id.web_usage_count,
            getUsageCount(webView).let {
                if (it == Int.MAX_VALUE) {
                    Int.MAX_VALUE
                } else {
                    it + 1
                }
            }
        )
    }

}