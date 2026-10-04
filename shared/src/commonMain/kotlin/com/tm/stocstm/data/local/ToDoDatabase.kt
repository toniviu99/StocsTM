package com.tm.stocstm.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.tm.stocstm.data.local.dao.ProductDao
import com.tm.stocstm.data.local.entity.ProductEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(
    entities = [ProductEntity::class],
    version = 1
)
@ConstructedBy(TodoDatabaseConstructor::class)
abstract class StocsDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
}

@Suppress("KotlinNoActualForExpect")
expect object TodoDatabaseConstructor : RoomDatabaseConstructor<StocsDatabase> {
    override fun initialize(): StocsDatabase
}

fun createStocsDatabase(
    builder: RoomDatabase.Builder<StocsDatabase>
): StocsDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}