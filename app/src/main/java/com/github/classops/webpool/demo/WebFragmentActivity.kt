package com.github.classops.webpool.demo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class WebFragmentActivity : AppCompatActivity() {

    companion object {
        fun nav(context: Context, title: String, url: String) {
            val intent = Intent(context, WebFragmentActivity::class.java)
            intent.putExtra("title", title)
            intent.putExtra("url", url)
            context.startActivity(intent)
        }
    }

    private lateinit var url: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.url = intent.getStringExtra("url") ?: ""
        setContentView(R.layout.activity_web_fragment)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(
                    android.R.id.content,
                    WebFragment.newInstance(url),
                    "web"
                )
                .commit()
        }
    }
}