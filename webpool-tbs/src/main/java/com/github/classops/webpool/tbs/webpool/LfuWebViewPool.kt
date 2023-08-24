package com.github.classops.webpool.tbs.webpool

import android.content.Context
import com.github.classops.webpool.core.WebViewFactory
import com.github.classops.webpool.core.pool.BaseLfuWebViewPool
import com.tencent.smtt.sdk.WebView

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