package com.us4ever.viderapro

import android.content.Context

object AndroidContext {
    private var appContext: Context? = null
    
    fun init(context: Context) {
        appContext = context.applicationContext
    }
    
    fun get(): Context = appContext ?: throw IllegalStateException("Context not initialized")
}
