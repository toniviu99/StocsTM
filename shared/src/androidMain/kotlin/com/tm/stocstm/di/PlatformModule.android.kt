package com.tm.stocstm.di

import android.content.Context
import com.tm.stocstm.data.local.StocsDatabase
import com.tm.stocstm.data.local.createStocsDatabase
import com.tm.stocstm.data.local.getDatabaseBuilder
import org.koin.dsl.module

fun platformModule(context: Context) = module {

    single<StocsDatabase> {
        createStocsDatabase(
            getDatabaseBuilder(context)
        )
    }

    single {
        get<StocsDatabase>().productDao()
    }
}