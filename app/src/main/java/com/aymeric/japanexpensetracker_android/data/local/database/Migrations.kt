package com.aymeric.japanexpensetracker_android.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Version 1 -> 2:
 * - amounts move from REAL to INTEGER (whole yen), values are rounded;
 * - expenses get a nullable, unique `externalId` column for IC card imports.
 *
 * SQLite cannot change a column type in place, so both tables are rebuilt.
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `expenses_new` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `dateMillis` INTEGER NOT NULL,
                `amountYen` INTEGER NOT NULL,
                `paymentMethod` TEXT NOT NULL,
                `category` TEXT NOT NULL,
                `note` TEXT NOT NULL,
                `externalId` TEXT
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            INSERT INTO `expenses_new` (`id`, `dateMillis`, `amountYen`, `paymentMethod`, `category`, `note`)
            SELECT `id`, `dateMillis`, CAST(ROUND(`amountYen`) AS INTEGER), `paymentMethod`, `category`, `note`
            FROM `expenses`
            """.trimIndent()
        )
        db.execSQL("DROP TABLE `expenses`")
        db.execSQL("ALTER TABLE `expenses_new` RENAME TO `expenses`")
        db.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS `index_expenses_externalId` ON `expenses` (`externalId`)"
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `withdrawals_new` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `dateMillis` INTEGER NOT NULL,
                `amountYen` INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            INSERT INTO `withdrawals_new` (`id`, `dateMillis`, `amountYen`)
            SELECT `id`, `dateMillis`, CAST(ROUND(`amountYen`) AS INTEGER)
            FROM `withdrawals`
            """.trimIndent()
        )
        db.execSQL("DROP TABLE `withdrawals`")
        db.execSQL("ALTER TABLE `withdrawals_new` RENAME TO `withdrawals`")
    }
}
