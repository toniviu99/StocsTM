package com.tm.stocstm.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.tm.stocstm.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query("SELECT * FROM products WHERE isActive = 1 ORDER BY name ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("""
        SELECT * FROM products
        WHERE isActive = 1
        AND nextPurchaseAt <= :currentTime
        ORDER BY nextPurchaseAt ASC
    """)
    fun getProductsToBuy(currentTime: Long): Flow<List<ProductEntity>>

    @Query("""
        SELECT * FROM products
        WHERE isActive = 1
        AND nextPurchaseAt > :currentTime
        ORDER BY nextPurchaseAt ASC
    """)
    fun getProductsNotNeededYet(currentTime: Long): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Long): ProductEntity?

    @Insert
    suspend fun insertProduct(product: ProductEntity): Long

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)
}