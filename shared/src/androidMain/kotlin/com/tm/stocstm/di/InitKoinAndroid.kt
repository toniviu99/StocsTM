package com.tm.stocstm.di

import android.content.Context

fun initKoinAndroid(context: Context) {
    initKoin {
        modules(platformModule(context))
    }
}
