package com.github.classops.webpool.core

import android.view.ViewGroup

interface WebCallbackProvider<T : ViewGroup> {

    fun getWebCallback(): WebCallback<T>?

}