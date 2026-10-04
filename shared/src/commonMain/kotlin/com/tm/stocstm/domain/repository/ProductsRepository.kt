package com.tm.stocstm.domain.repository

import com.tm.stocstm.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductsRepository {
    fun getAll(): Flow<List<Product>>

    suspend fun insertTest()
}