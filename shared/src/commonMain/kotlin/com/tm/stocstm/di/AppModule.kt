package com.tm.stocstm.di

import com.tm.stocstm.data.repository.ProductRepositoryImpl
import com.tm.stocstm.domain.repository.ProductsRepository
import com.tm.stocstm.presentation.products.ProductViewModel
import org.koin.dsl.module

val appModule = module {

    single<ProductsRepository> {
        ProductRepositoryImpl(
            productDao = get()
        )
    }

    factory {
        ProductViewModel(
            productsRepository = get()
        )
    }
}