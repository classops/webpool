package com.github.classops.webpool.tbs

import android.app.Activity
import android.content.Context
import android.content.MutableContextWrapper
import android.util.Log
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.github.classops.webpool.core.Cleaner
import com.github.classops.webpool.core.WebCallbackProvider
import com.github.classops.webpool.core.WebState
import com.github.classops.webpool.core.WebViewPool
import com.github.classops.webpool.core.webState
import com.tencent.smtt.sdk.WebView
import java.lang.ref.WeakReference

/**
 * WebView帮助类
 */
class WebViewHelper(
    private val context: Context,
    val webView: WebView,
    private val webViewPool: WebViewPool<WebView>,
    private val webCallbackProvider: WebCallbackProvider<WebView>
) : DefaultLifecycleObserver {

    private val cleaner: Cleaner<WebView> by lazy {
        TbsCleaner(webViewPool)
    }

    private var isCleaning: Boolean = false

    private lateinit var activityRef: WeakReference<Activity>

    fun attach(activity: ComponentActivity) {
        Log.d("WebView", "attach")
        setupWebViewContext(webView, activity)
        this.webView.webState = WebState.IN_USE
        activity.lifecycle.addObserver(this)
        activityRef = WeakReference(activity)
        webCallbackProvider.getWebCallback()?.onWebViewAttach(this.webView)
    }

    fun attach(fragment: Fragment) {
        setupWebViewContext(webView, fragment.requireContext())
        fragment.lifecycle.addObserver(this)
        webCallbackProvider.getWebCallback()?.onWebViewAttach(this.webView)
    }

    fun detach() {
        Log.d("WebView", "detach")
        setupWebViewContext(webView, context.applicationContext)
        (webView.parent as? ViewGroup)?.removeView(webView)
        webCallbackProvider.getWebCallback()?.onWebViewDetach(this.webView)
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        if (::activityRef.isInitialized && activityRef.get()?.isFinishing == true) {
            clean()
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        clean()
        detach()
    }

    private fun setupWebViewContext(view: WebView, context: Context) {
        (view.context as? MutableContextWrapper)?.baseContext = context
    }

    private fun clean() {
        if (isCleaning) return

        isCleaning = true
        this.webView.webState = WebState.DIRTY
        webCallbackProvider.getWebCallback()?.onWebViewClean(this.webView)
        this.cleaner.clean(this.webView)
    }

}