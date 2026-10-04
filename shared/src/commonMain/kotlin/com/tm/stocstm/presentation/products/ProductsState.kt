package com.tm.stocstm.presentation.products

import com.tm.stocstm.domain.model.Product

data class ProductSelection(
    val product: Product,
    val isSelected: Boolean
)

enum class FilterState{
    ALL,
    FOR_BUY,
    IN_STOCK
}

data class ProductsState(
    val products: List<ProductSelection> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val filterState: FilterState = FilterState.ALL
)
