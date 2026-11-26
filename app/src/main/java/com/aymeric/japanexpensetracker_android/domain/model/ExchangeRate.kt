package com.aymeric.japanexpensetracker_android.domain.model

data class ExchangeRate(
    /** Value of one yen in euro. */
    val yenToEur: Double,
    /** Reference date published by the rate provider, null for the built-in fallback. */
    val date: String?,
    val source: Source
) {
    enum class Source { LIVE, CACHED, FALLBACK }

    companion object {
        /** Used only until a first rate has been downloaded. */
        val FALLBACK = ExchangeRate(yenToEur = 0.0055, date = null, source = Source.FALLBACK)
    }
}
