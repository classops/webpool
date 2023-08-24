package com.github.classops.webpool.core

import android.view.ViewGroup

/**
 * WebView 绑定、解绑、清理回调方法
 */
interface WebCallback<T : ViewGroup> {

    fun onWebViewAttach(webView: T)

    fun onWebViewDetach(webView: T)

    fun onWebViewClean(webView: T)

}