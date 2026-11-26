package com.aymeric.japanexpensetracker_android.data.local.database

import androidx.room.TypeConverter
import com.aymeric.japanexpensetracker_android.domain.model.PaymentMethod

class Converters {
    @TypeConverter
    fun fromPaymentMethod(method: PaymentMethod): String = method.storageKey

    @TypeConverter
    fun toPaymentMethod(key: String): PaymentMethod = PaymentMethod.fromStorageKey(key)
}
