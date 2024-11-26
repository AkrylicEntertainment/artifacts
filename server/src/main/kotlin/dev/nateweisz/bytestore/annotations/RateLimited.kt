package dev.nateweisz.bytestore.annotations

// Temporary annotation until I properly implement rate limiting just need a way to keep tracking of all limited endpoints
annotation class RateLimited(
    val requestsPerMinute: Int
) {}