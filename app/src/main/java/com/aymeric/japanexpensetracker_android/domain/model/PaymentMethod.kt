package com.aymeric.japanexpensetracker_android.domain.model

/**
 * How an expense was paid.
 *
 * [storageKey] is the value written to the database. "Cash" and "Card" match the
 * strings used by version 1 of the schema, so existing rows keep working.
 */
enum class PaymentMethod(val storageKey: String, val label: String) {
    CASH("Cash", "Cash"),
    CARD("Card", "Card"),
    IC_CARD("IC", "IC card");

    companion object {
        fun fromStorageKey(key: String): PaymentMethod =
            entries.firstOrNull { it.storageKey == key } ?: CARD
    }
}
