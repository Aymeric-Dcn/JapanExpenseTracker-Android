package com.aymeric.japanexpensetracker_android.data.repository

import android.content.Context
import com.aymeric.japanexpensetracker_android.data.remote.FrankfurterParser
import com.aymeric.japanexpensetracker_android.domain.model.ExchangeRate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

/**
 * Daily JPY -> EUR rate from the Frankfurter API (European Central Bank reference rates),
 * cached so the app keeps converting amounts offline.
 */
class ExchangeRateRepository(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun cached(): ExchangeRate? {
        val rate = prefs.getFloat(KEY_RATE, 0f).toDouble()
        val date = prefs.getString(KEY_DATE, null)
        if (rate <= 0.0 || date == null) return null
        return ExchangeRate(rate, date, ExchangeRate.Source.CACHED)
    }

    /** Downloads the latest rate; on any failure returns the cached rate, then the fallback. */
    suspend fun refresh(): ExchangeRate = withContext(Dispatchers.IO) {
        try {
            val quote = FrankfurterParser.parse(download())
                ?: return@withContext cached() ?: ExchangeRate.FALLBACK
            prefs.edit()
                .putFloat(KEY_RATE, quote.yenToEur.toFloat())
                .putString(KEY_DATE, quote.date)
                .apply()
            ExchangeRate(quote.yenToEur, quote.date, ExchangeRate.Source.LIVE)
        } catch (e: Exception) {
            cached() ?: ExchangeRate.FALLBACK
        }
    }

    private fun download(): String {
        val connection = URL(API_URL).openConnection() as HttpURLConnection
        return try {
            connection.connectTimeout = TIMEOUT_MS
            connection.readTimeout = TIMEOUT_MS
            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                throw IllegalStateException("HTTP ${connection.responseCode}")
            }
            connection.inputStream.bufferedReader().use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }

    private companion object {
        const val API_URL = "https://api.frankfurter.dev/v1/latest?base=JPY&symbols=EUR"
        const val TIMEOUT_MS = 5000
        const val PREFS_NAME = "exchange_rate"
        const val KEY_RATE = "jpy_to_eur"
        const val KEY_DATE = "date"
    }
}
