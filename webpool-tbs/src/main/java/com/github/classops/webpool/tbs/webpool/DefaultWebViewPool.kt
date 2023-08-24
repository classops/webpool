package com.github.classops.webpool.tbs.webpool

import android.content.Context
import com.github.classops.webpool.core.WebViewFactory
import com.github.classops.webpool.core.pool.DefaultWebViewPool
import com.tencent.smtt.sdk.WebView

/**
 * 默认缓存池
 */
class DefaultWebViewPool(
    context: Context, maxSize: Int, webViewFactory: WebViewFactory<WebView>
) : DefaultWebViewPool<WebView>(context, maxSize, webViewFactory) {

    override fun destroyWebView(webView: WebView) {
        super.destroyWebView(webView)
        try {
            webView.removeAllViews()
            webView.destroy()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}