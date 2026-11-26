package com.aymeric.japanexpensetracker_android.domain.usecase

import com.aymeric.japanexpensetracker_android.data.local.entity.ExpenseEntity
import com.aymeric.japanexpensetracker_android.data.local.entity.WithdrawalEntity
import com.aymeric.japanexpensetracker_android.domain.model.PaymentMethod
import java.util.Calendar
import java.util.TimeZone
import kotlin.random.Random

/**
 * Realistic sample data (three weeks of daily life in Osaka) for screenshots and demos.
 * Only reachable from debug builds. Generation is deterministic for a given day.
 */
object DemoData {

    data class Dataset(val expenses: List<ExpenseEntity>, val withdrawals: List<WithdrawalEntity>)

    private data class Template(
        val category: String,
        val note: String,
        val minYen: Int,
        val maxYen: Int,
        val payment: PaymentMethod
    )

    private val DAILY = listOf(
        Template("Transport", "Train to campus (Osaka Metro)", 230, 290, PaymentMethod.IC_CARD),
        Template("Food", "Lunch at the university cafeteria", 450, 650, PaymentMethod.CASH)
    )

    private val OCCASIONAL = listOf(
        Template("Food", "Ichiran ramen", 980, 1290, PaymentMethod.CASH),
        Template("Food", "Takoyaki in Dotonbori", 600, 800, PaymentMethod.CASH),
        Template("Food", "Conveyor belt sushi", 1500, 2600, PaymentMethod.CARD),
        Template("Food", "Izakaya with the lab team", 3000, 4500, PaymentMethod.CASH),
        Template("Konbini", "Lawson: onigiri and coffee", 350, 700, PaymentMethod.IC_CARD),
        Template("Konbini", "7-Eleven snacks", 300, 600, PaymentMethod.IC_CARD),
        Template("Groceries", "Life supermarket", 1800, 3800, PaymentMethod.CARD),
        Template("Shopping", "Don Quijote", 1200, 4800, PaymentMethod.CARD),
        Template("Shopping", "Electronic parts in Nipponbashi", 800, 3500, PaymentMethod.CASH),
        Template("Leisure", "Karaoke", 1200, 2000, PaymentMethod.CASH),
        Template("Leisure", "Osaka Castle entrance", 1200, 1200, PaymentMethod.CASH),
        Template("Transport", "JR to Kyoto", 580, 580, PaymentMethod.IC_CARD),
        Template("Transport", "Bus", 230, 230, PaymentMethod.IC_CARD)
    )

    fun generate(
        nowMillis: Long = System.currentTimeMillis(),
        timeZone: TimeZone = TimeZone.getDefault(),
        days: Int = 21
    ): Dataset {
        val today = Calendar.getInstance(timeZone).apply {
            timeInMillis = nowMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val random = Random(today.get(Calendar.YEAR) * 1000 + today.get(Calendar.DAY_OF_YEAR))

        fun at(daysAgo: Int, hour: Int, minute: Int): Long =
            (today.clone() as Calendar).apply {
                add(Calendar.DAY_OF_MONTH, -daysAgo)
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
            }.timeInMillis.coerceAtMost(nowMillis)

        fun expense(template: Template, time: Long, index: Int) = ExpenseEntity(
            dateMillis = time,
            amountYen = random.nextInt(template.minYen, template.maxYen + 1).roundTo10(),
            paymentMethod = template.payment,
            category = template.category,
            note = template.note,
            externalId = "demo:$index"
        )

        val expenses = mutableListOf<ExpenseEntity>()
        for (daysAgo in days - 1 downTo 0) {
            val dayOfWeek = (today.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, -daysAgo) }
                .get(Calendar.DAY_OF_WEEK)
            if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) { // commute and cafeteria
                expenses += expense(DAILY[0], at(daysAgo, 8, 20), expenses.size)
                expenses += expense(DAILY[1], at(daysAgo, 12, 15), expenses.size)
            }
            repeat(random.nextInt(1, 4)) {
                val template = OCCASIONAL[random.nextInt(OCCASIONAL.size)]
                expenses += expense(template, at(daysAgo, random.nextInt(9, 22), random.nextInt(60)), expenses.size)
            }
        }

        val withdrawals = listOf(days - 1, days / 2).map { daysAgo ->
            WithdrawalEntity(dateMillis = at(daysAgo, 10, 0), amountYen = 30_000)
        }
        return Dataset(expenses, withdrawals)
    }

    private fun Int.roundTo10(): Long = (this / 10 * 10).toLong()
}
