package org.company.app.androidApp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform