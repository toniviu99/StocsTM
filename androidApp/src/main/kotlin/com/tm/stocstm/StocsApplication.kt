package com.tm.stocstm

import android.app.Application
import com.tm.stocstm.di.initKoinAndroid

class StocsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoinAndroid(this)
    }
}
