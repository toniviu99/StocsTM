package com.tm.stocstm.presentation.products


sealed interface ProductsEvent {
    data class SelectProduct(val id: Long): ProductsEvent
    data class ProductFiltersChange(val selectedFilter: FilterState) : ProductsEvent
}