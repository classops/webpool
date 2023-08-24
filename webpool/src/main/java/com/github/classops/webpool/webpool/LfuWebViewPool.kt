package com.github.classops.webpool.webpool

import android.content.Context
import android.webkit.WebView
import com.github.classops.webpool.core.WebViewFactory
import com.github.classops.webpool.core.pool.BaseLfuWebViewPool

/**
 * WebView的LfuPool实现销毁
 */
class LfuWebViewPool(
    context: Context, maxSize: Int, webViewFactory: WebViewFactory<WebView>
) : BaseLfuWebViewPool<WebView>(context, maxSize, webViewFactory) {

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