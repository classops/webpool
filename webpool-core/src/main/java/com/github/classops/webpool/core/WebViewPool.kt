package com.github.classops.webpool.core

import android.content.Context
import android.view.ViewGroup

/**
 * WebView缓存池，对 空闲、脏的 处理。
 */
interface WebViewPool<T : ViewGroup> {

    fun getPoolMaxSize(): Int

    fun getCurrentSize(): Int

    /**
     * 完成清理，将 Dirty队列 的WebView 移动到 队列
     */
    fun clean(webView: T): Boolean

    fun put(webView: T)

    /**
     * 创建、获取缓存 WebView
     */
    fun get(context: Context?): T

    fun clear()

    fun trimMemory(level: Int)

}