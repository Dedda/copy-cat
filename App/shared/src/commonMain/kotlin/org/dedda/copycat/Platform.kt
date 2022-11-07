package org.dedda.copycat

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform