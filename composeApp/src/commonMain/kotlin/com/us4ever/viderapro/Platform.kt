package com.us4ever.viderapro

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform