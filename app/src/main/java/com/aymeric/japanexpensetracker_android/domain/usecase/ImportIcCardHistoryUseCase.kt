package com.aymeric.japanexpensetracker_android.domain.usecase

import com.aymeric.felica.ProcessType
import com.aymeric.felica.SuicaCard
import com.aymeric.felica.SuicaHistoryEntry
import com.aymeric.japanexpensetracker_android.data.local.entity.ExpenseEntity
import com.aymeric.japanexpensetracker_android.domain.model.PaymentMethod
import java.util.Calendar
import java.util.TimeZone

/**
 * Turns the payments found in an IC card history into expenses.
 *
 * Top-ups are not expenses (the money is only moved onto the card), so only
 * entries that lowered the balance are kept. Each expense gets a stable
 * external id so scanning the same card twice does not create duplicates.
 */
class ImportIcCardHistoryUseCase(
    /** The card only stores a date, so imported expenses are placed at noon in Japan. */
    private val cardTimeZone: TimeZone = TimeZone.getTimeZone("Asia/Tokyo")
) {
    operator fun invoke(card: SuicaCard): List<ExpenseEntity> =
        card.history
            .filter { it.isPayment }
            .map { entry ->
                ExpenseEntity(
                    dateMillis = toMillis(entry),
                    amountYen = -entry.amountYen!!.toLong(),
                    paymentMethod = PaymentMethod.IC_CARD,
                    category = categoryFor(entry.processType),
                    note = "${entry.processType.label} (IC card)",
                    externalId = externalId(card.idm, entry)
                )
            }

    private fun toMillis(entry: SuicaHistoryEntry): Long =
        Calendar.getInstance(cardTimeZone).apply {
            clear()
            set(entry.date.year, entry.date.month - 1, entry.date.day, 12, 0, 0)
        }.timeInMillis

    private fun categoryFor(type: ProcessType): String = when (type) {
        ProcessType.FARE, ProcessType.BUS -> "Transport"
        ProcessType.PURCHASE -> "Shopping"
        ProcessType.CHARGE, ProcessType.OTHER -> "IC card"
    }

    companion object {
        fun externalId(idm: String, entry: SuicaHistoryEntry): String =
            "ic:$idm:${entry.sequence}:${entry.date}"
    }
}
