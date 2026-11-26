package com.aymeric.japanexpensetracker_android.data

import com.aymeric.japanexpensetracker_android.data.remote.FrankfurterParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FrankfurterParserTest {

    @Test
    fun `rate and date are read from the API response`() {
        val json = """{"amount":1.0,"base":"JPY","date":"2026-11-26","rates":{"EUR":0.0055}}"""
        assertEquals(FrankfurterParser.Quote(0.0055, "2026-11-26"), FrankfurterParser.parse(json))
    }

    @Test
    fun `whitespace and scientific notation are accepted`() {
        val json = """
            { "base": "JPY", "date": "2026-11-26",
              "rates": { "EUR": 5.5E-3 } }
        """.trimIndent()
        assertEquals(0.0055, FrankfurterParser.parse(json)!!.yenToEur, 1e-12)
    }

    @Test
    fun `missing or invalid data gives null`() {
        assertNull(FrankfurterParser.parse("""{"date":"2026-11-26","rates":{"USD":0.0066}}"""))
        assertNull(FrankfurterParser.parse("""{"rates":{"EUR":0.0055}}"""))
        assertNull(FrankfurterParser.parse("""{"date":"2026-11-26","rates":{"EUR":0}}"""))
        assertNull(FrankfurterParser.parse("Service Unavailable"))
    }
}
