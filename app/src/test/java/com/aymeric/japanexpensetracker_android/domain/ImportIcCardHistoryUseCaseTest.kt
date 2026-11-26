package com.aymeric.japanexpensetracker_android.domain

import com.aymeric.felica.CardDate
import com.aymeric.felica.ProcessType
import com.aymeric.felica.SuicaCard
import com.aymeric.felica.SuicaHistoryEntry
import com.aymeric.japanexpensetracker_android.domain.model.PaymentMethod
import com.aymeric.japanexpensetracker_android.domain.usecase.ImportIcCardHistoryUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class ImportIcCardHistoryUseCaseTest {

    private val tokyo = TimeZone.getTimeZone("Asia/Tokyo")
    private val importer = ImportIcCardHistoryUseCase(tokyo)

    private fun entry(sequence: Int, type: ProcessType, balance: Int, amount: Int?) = SuicaHistoryEntry(
        sequence = sequence,
        date = CardDate(2026, 11, 26),
        processCode = 0,
        processType = type,
        balanceYen = balance,
        amountYen = amount
    )

    private val card = SuicaCard(
        idm = "0114B30A520F772C",
        balanceYen = 1530,
        history = listOf(
            entry(12, ProcessType.PURCHASE, 1530, -450),
            entry(11, ProcessType.CHARGE, 1980, 1000),
            entry(10, ProcessType.FARE, 980, -220),
            entry(9, ProcessType.FARE, 1200, null)
        )
    )

    @Test
    fun `only payments are imported, as positive IC card expenses`() {
        val expenses = importer(card)

        assertEquals(listOf(450L, 220L), expenses.map { it.amountYen })
        assertEquals(listOf("Shopping", "Transport"), expenses.map { it.category })
        expenses.forEach { assertEquals(PaymentMethod.IC_CARD, it.paymentMethod) }
    }

    @Test
    fun `imported expenses are dated at noon in Japan`() {
        val date = Calendar.getInstance(tokyo).apply { timeInMillis = importer(card).first().dateMillis }

        assertEquals(2026, date.get(Calendar.YEAR))
        assertEquals(Calendar.NOVEMBER, date.get(Calendar.MONTH))
        assertEquals(26, date.get(Calendar.DAY_OF_MONTH))
        assertEquals(12, date.get(Calendar.HOUR_OF_DAY))
    }

    @Test
    fun `external ids are stable and unique per transaction`() {
        val first = importer(card).map { it.externalId }
        val second = importer(card).map { it.externalId }

        assertEquals(first, second)
        assertEquals("ic:0114B30A520F772C:12:2026-11-26", first[0])
        assertNotEquals(first[0], first[1])
    }
}
