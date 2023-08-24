package com.github.classops.webpool.tbs.factory

import android.content.Context
import android.content.MutableContextWrapper
import com.github.classops.webpool.core.WebViewFactory
import com.tencent.smtt.sdk.WebView

/**
 * 默认WebView工厂类
 */
class DefaultWebViewFactory : WebViewFactory<WebView> {

    override fun create(context: Context): WebView {
        return WebView(MutableContextWrapper(context))
    }

}