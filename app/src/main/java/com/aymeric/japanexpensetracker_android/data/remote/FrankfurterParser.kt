package com.aymeric.japanexpensetracker_android.data.remote

/**
 * Parses the response of `GET https://api.frankfurter.dev/v1/latest?base=JPY&symbols=EUR`:
 *
 * {"amount":1.0,"base":"JPY","date":"2026-11-26","rates":{"EUR":0.0055}}
 *
 * The response is tiny and fixed, so a regex keeps this parser free of Android's
 * org.json and runnable in plain JVM unit tests.
 */
object FrankfurterParser {
    data class Quote(val yenToEur: Double, val date: String)

    private val RATE = Regex("\"EUR\"\\s*:\\s*([0-9]+(?:\\.[0-9]+)?(?:[eE][-+]?[0-9]+)?)")
    private val DATE = Regex("\"date\"\\s*:\\s*\"([0-9]{4}-[0-9]{2}-[0-9]{2})\"")

    fun parse(json: String): Quote? {
        val rate = RATE.find(json)?.groupValues?.get(1)?.toDoubleOrNull() ?: return null
        val date = DATE.find(json)?.groupValues?.get(1) ?: return null
        if (rate <= 0.0) return null
        return Quote(rate, date)
    }
}
