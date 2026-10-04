package com.tm.stocstm.data.repository

import com.tm.stocstm.data.local.dao.ProductDao
import com.tm.stocstm.data.local.entity.ProductEntity
import com.tm.stocstm.data.local.mapper.toDomain
import com.tm.stocstm.domain.model.Product
import com.tm.stocstm.domain.repository.ProductsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductRepositoryImpl(
    private val productDao: ProductDao
) : ProductsRepository {
    override fun getAll(): Flow<List<Product>> {
        return productDao.getAllProducts().map { list ->
            list.map {
                it.toDomain()
            }
        }
    }

    override suspend fun insertTest() {
        productDao.insertProduct(
            ProductEntity(
                name = "macarrons",
                quantity = "dos",
                durationDays = 2,
                lastPurchasedAt = null,
                nextPurchaseAt = null,
                isActive = true
            )
        )
    }
}