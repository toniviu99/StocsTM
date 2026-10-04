package com.tm.stocstm.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getDatabaseBuilder(
    context: Context
): RoomDatabase.Builder<StocsDatabase> {

    val appContext = context.applicationContext

    val dbFile = appContext.getDatabasePath("stocs.db")

    return Room.databaseBuilder<StocsDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}