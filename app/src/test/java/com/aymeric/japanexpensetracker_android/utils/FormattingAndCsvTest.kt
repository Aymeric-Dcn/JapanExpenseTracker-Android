package com.aymeric.japanexpensetracker_android.utils

import com.aymeric.japanexpensetracker_android.UTC
import com.aymeric.japanexpensetracker_android.domain.model.PaymentMethod
import com.aymeric.japanexpensetracker_android.expense
import com.aymeric.japanexpensetracker_android.utcMillis
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FormattingAndCsvTest {

    @Test
    fun `yen are formatted without decimals`() {
        assertEquals("¥0", formatYen(0))
        assertEquals("¥1,234,567", formatYen(1_234_567))
        assertEquals("-¥450", formatYen(-450))
    }

    @Test
    fun `euro conversion uses the given rate`() {
        assertEquals("5.50 €", formatEuro(1_000, 0.0055))
        assertEquals("¥1,000", formatAmount(1_000, showEuro = false, yenToEur = 0.0055))
        assertEquals("5.50 €", formatAmount(1_000, showEuro = true, yenToEur = 0.0055))
    }

    @Test
    fun `csv has a header and one line per expense`() {
        val csv = CsvExporter.toCsv(
            listOf(
                expense(1_200, PaymentMethod.CASH, "Food", utcMillis(2026, 11, 26, 12, 30), note = "ramen"),
                expense(220, PaymentMethod.IC_CARD, "Transport", utcMillis(2026, 11, 25, 8, 5), externalId = "ic:x")
            ),
            timeZone = UTC,
            withByteOrderMark = false
        )

        val expected = "date,amount_jpy,payment_method,category,note,source\r\n" +
            "2026-11-26 12:30,1200,Cash,Food,ramen,manual\r\n" +
            "2026-11-25 08:05,220,IC,Transport,,ic_card\r\n"
        assertEquals(expected, csv)
    }

    @Test
    fun `csv fields with separators or quotes are escaped`() {
        assertEquals("plain", CsvExporter.escape("plain"))
        assertEquals("\"a,b\"", CsvExporter.escape("a,b"))
        assertEquals("\"say \"\"hi\"\"\"", CsvExporter.escape("say \"hi\""))
        assertEquals("\"two\nlines\"", CsvExporter.escape("two\nlines"))
    }

    @Test
    fun `csv starts with a byte order mark by default`() {
        assertTrue(CsvExporter.toCsv(emptyList(), UTC).startsWith("﻿date,"))
    }
}
