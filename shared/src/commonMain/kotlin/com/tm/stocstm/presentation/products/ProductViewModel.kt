package com.tm.stocstm.presentation.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tm.stocstm.domain.repository.ProductsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductViewModel (
    private val productsRepository: ProductsRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ProductsState())
    val state: StateFlow<ProductsState> = _state.asStateFlow()

    init {
        loadProducts()
    }

    fun onEvent(event: ProductsEvent) {
        when(event) {
            is ProductsEvent.SelectProduct -> {
                val newList = _state.value.products.map {
                    if (it.product.id == event.id) {
                        it.copy(isSelected = !it.isSelected)
                    } else {
                        it
                    }
                }

                _state.value = _state.value.copy(
                    products = newList
                )
            }

            is ProductsEvent.ProductFiltersChange -> {
                _state.value = _state.value.copy(
                    filterState = event.selectedFilter
                )
            }
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                error = null
            )

            try {
                productsRepository.getAll()
                    .collect { products ->
                        _state.value = ProductsState(
                            products = products.map {
                                ProductSelection(
                                    isSelected = false,
                                    product = it
                                )
                            },
                            isLoading = false
                        )
                    }

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun add() {
        viewModelScope.launch(Dispatchers.IO) {
            productsRepository.insertTest()
        }
    }


}