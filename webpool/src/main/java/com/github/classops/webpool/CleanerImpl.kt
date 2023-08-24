package com.github.classops.webpool

import android.graphics.Bitmap
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import com.github.classops.webpool.core.Cleaner
import com.github.classops.webpool.core.WebState
import com.github.classops.webpool.core.WebViewPool
import com.github.classops.webpool.core.webState

/**
 * WebView清洗器
 */
class CleanerImpl(private val webViewPool: WebViewPool<WebView>) : Cleaner<WebView> {

    override fun clean(webView: WebView) {
        Log.d(WebManager.TAG, "on clean start")
        webView.stopLoading()
        webView.clearFocus()
        webView.clearMatches()
        webView.webViewClient = object : WebViewClient() {
            override fun doUpdateVisitedHistory(view: WebView, url: String?, p2: Boolean) {
                super.doUpdateVisitedHistory(view, url, p2)
                if (url == WebManager.ABOUT_BLANK) {
                    onHistoryUpdate(view)
                }
            }

            override fun onPageStarted(view: WebView, url: String?, p2: Bitmap?) {
                super.onPageStarted(view, url, p2)
            }

            override fun onPageFinished(view: WebView, url: String?) {
                super.onPageFinished(view, url)
                if (url == WebManager.ABOUT_BLANK) {
                    onHistoryUpdate(view)
                    onCompleted(view)
                }
            }
        }
        webView.loadUrl(WebManager.ABOUT_BLANK)
        onStart(webView)
    }

    override fun onStart(webView: WebView) {
        this.webViewPool.put(webView)
    }

    override fun onHistoryUpdate(webView: WebView) {
        Log.d(WebManager.TAG, "on clean history update")
        webView.clearHistory()
    }

    override fun onCompleted(webView: WebView) {
        Log.d(WebManager.TAG, "on clean history completed")
        // 更新状态，加入缓存
        webView.webState = WebState.IDLE
        this.webViewPool.clean(webView)
    }

}