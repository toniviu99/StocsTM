package com.tm.stocstm

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform