package com.aymeric.japanexpensetracker_android.utils

import com.aymeric.japanexpensetracker_android.data.local.entity.ExpenseEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object CsvExporter {
    private const val HEADER = "date,amount_jpy,payment_method,category,note,source"

    /**
     * RFC 4180 CSV, one line per expense. The UTF-8 byte order mark lets Excel
     * open notes written in Japanese without garbling them.
     */
    fun toCsv(
        expenses: List<ExpenseEntity>,
        timeZone: TimeZone = TimeZone.getDefault(),
        withByteOrderMark: Boolean = true
    ): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).apply {
            this.timeZone = timeZone
        }
        val lines = expenses.map { expense ->
            listOf(
                dateFormat.format(Date(expense.dateMillis)),
                expense.amountYen.toString(),
                expense.paymentMethod.storageKey,
                expense.category,
                expense.note,
                if (expense.externalId == null) "manual" else "ic_card"
            ).joinToString(",") { escape(it) }
        }
        val bom = if (withByteOrderMark) "﻿" else ""
        return bom + (listOf(HEADER) + lines).joinToString("\r\n") + "\r\n"
    }

    internal fun escape(field: String): String =
        if (field.any { it == ',' || it == '"' || it == '\n' || it == '\r' }) {
            "\"" + field.replace("\"", "\"\"") + "\""
        } else {
            field
        }
}
