package com.github.classops.webpool.core

import android.content.Context
import android.view.ViewGroup

/**
 * 创建WebView的抽象工厂
 */
interface WebViewFactory<T : ViewGroup> {

    fun create(context: Context): T

}