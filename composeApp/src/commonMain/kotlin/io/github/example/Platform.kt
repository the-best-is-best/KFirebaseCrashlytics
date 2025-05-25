package io.github.example

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform