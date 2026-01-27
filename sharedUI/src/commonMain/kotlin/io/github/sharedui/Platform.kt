package io.github.sharedui

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform