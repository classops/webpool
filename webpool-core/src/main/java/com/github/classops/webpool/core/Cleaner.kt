package com.github.classops.webpool.core

import android.view.ViewGroup

/**
 * WebView清洗器
 */
interface Cleaner<T : ViewGroup> {

    fun clean(webView: T)

    fun onStart(webView: T)

    fun onHistoryUpdate(webView: T)

    fun onCompleted(webView: T)

}