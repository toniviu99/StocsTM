package com.tm.stocstm.data.local.mapper

import com.tm.stocstm.data.local.entity.ProductEntity
import com.tm.stocstm.domain.model.Product
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

fun ProductEntity.toDomain(): Product {
    return Product(
        id = id,
        name = name,
        quantity = quantity,
        durationDays = durationDays,
        lastPurchasedAt = lastPurchasedAt?.toLocalDate(),
        nextPurchaseAt = nextPurchaseAt?.toLocalDate(),
        isActive = isActive
    )
}

@OptIn(ExperimentalTime::class)
private fun Long.toLocalDate(): LocalDate {
    return Instant.fromEpochMilliseconds(this)
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date
}