package com.github.classops.webpool.factory

import android.content.Context
import android.content.MutableContextWrapper
import android.webkit.WebView
import com.github.classops.webpool.core.WebViewFactory

/**
 * WebView工厂类
 */
class DefaultWebViewFactory : WebViewFactory<WebView> {

    override fun create(context: Context): WebView {
        return WebView(MutableContextWrapper(context))
    }

}