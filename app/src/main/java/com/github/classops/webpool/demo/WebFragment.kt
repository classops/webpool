package com.github.classops.webpool.demo

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.github.classops.webpool.WebManager


private const val ARG_URL = "url"

class WebFragment : Fragment() {

    private var url: String? = null
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            url = it.getString(ARG_URL)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_web, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.webView = WebManager.get(requireContext()).getWebView(this)
        initWeb()
        (view as ViewGroup).addView(
            this.webView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
        this.webView.loadUrl(url!!)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWeb() {
        val webSettings = this.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.setSupportZoom(false)
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT
        // 支持通过JS打开新窗口
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.loadsImagesAutomatically = true
        // 设置 AppCache DomStorage 的 存储目录
        webSettings.domStorageEnabled = true
        webSettings.setAppCacheEnabled(true)
        // 40M
        webSettings.setAppCacheMaxSize(40 * 1024 * 1024)
        webSettings.defaultTextEncodingName = "utf-8"
        webSettings.setGeolocationEnabled(true)
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        // this.disableJavascriptDialogBlock(true)
        // 使用 Application context 创建，关闭 自动填充 功能
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.webView.importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String) =
            WebFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_URL, param1)
                }
            }
    }
}