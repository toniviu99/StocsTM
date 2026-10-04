package com.tm.stocstm.domain.model

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

enum class ProductStock {
    TO_BUY,
    LOW_STOCK,
    IN_STOCK
}
const val daysForLowStock = 2

data class Product(
    val id: Long = 0,
    val name: String,
    val quantity: String,
    val durationDays: Int,
    val lastPurchasedAt: LocalDate? = null,
    val nextPurchaseAt: LocalDate? = null,
    val isActive: Boolean = true
) {
    val type: ProductStock
        get() = calculateType()

    @OptIn(ExperimentalTime::class)
    fun calculateType(
        today: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
    ): ProductStock {
        val targetDate = nextPurchaseAt
            ?: lastPurchasedAt?.plus(durationDays, DateTimeUnit.DAY)

        if (targetDate == null) return ProductStock.TO_BUY

        val daysLeft = targetDate.toEpochDays() - today.toEpochDays()

        return when {
            daysLeft <= 0 -> ProductStock.TO_BUY
            daysLeft <= daysForLowStock -> ProductStock.LOW_STOCK
            else -> ProductStock.IN_STOCK
        }
    }
}