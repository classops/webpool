package com.github.classops.webpool.demo

import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.github.classops.webpool.WebManager
import com.github.classops.webpool.core.WebCallback
import com.github.classops.webpool.demo.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private val urls = arrayListOf(
        Pair(
            "云",
            "https://www.aliyun.com/"
        ),
        Pair(
            "百度",
            "https://www.baidu.com"
        ),
        Pair(
            "分享",
            "https://github.com/classops"
        )
    )

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val random by lazy {
        Random(1)
    }

    private fun getRandomUrl(): Pair<String, String> {
        return urls.getOrNull((random.nextFloat() * urls.size).toInt()) ?: urls[0]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        WebManager.get(this).idleCreate()
        WebManager.get(this).webCallback = object : WebCallback<WebView> {
            override fun onWebViewAttach(webView: WebView) {
                Log.d("Test", "web ${webView.hashCode()} attach")
            }

            override fun onWebViewDetach(webView: WebView) {
                Log.d("Test", "web ${webView.hashCode()} detach")
            }

            override fun onWebViewClean(webView: WebView) {
                Log.d("Test", "web ${webView.hashCode()} clean")
            }
        }

        binding.btnLoad.setOnClickListener {
            val info = getRandomUrl()
            WebFragmentActivity.nav(
                this,
                info.first,
                info.second
            )
        }
    }
}