package com.github.classops.webpool.core

import android.view.ViewGroup

const val TAG = "WebPool"

var ViewGroup.webState: WebState
    set(value) {
        setTag(R.id.web_state, value)
    }
    get() = getTag(R.id.web_state) as? WebState? ?: WebState.IDLE

val ViewGroup.isWebDirty: Boolean
    get() = this.webState == WebState.DIRTY