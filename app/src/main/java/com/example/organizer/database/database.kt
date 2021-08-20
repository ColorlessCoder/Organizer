package com.example.organizer.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.organizer.database.dao.*
import com.example.organizer.database.entity.*
import com.example.organizer.database.services.SalatService
import java.util.*


@Database(
    entities = [
        Account::class,
        Transaction::class,
        Category::class,
        TransactionPlan::class,
        TemplateTransaction::class,
        Debt::class,
        TransactionChart::class,
        TransactionChartPoint::class,
        TransactionChartValue::class,
        TransactionChartToCategory::class,
        SalatTime::class,
        SalatSettings::class
    ], version = 15 ,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDAO
    abstract fun transactionDao(): TransactionDAO
    abstract fun categoryDao(): CategoryDAO
    abstract fun transactionPlanDao(): TransactionPlanDAO
    abstract fun templateTransactionDao(): TemplateTransactionDAO
    abstract fun debtDao(): DebtDAO
    abstract fun utilDao(): UtilDAO
    abstract fun transactionChartDao(): TransactionChartDAO
    abstract fun salatTimesDao(): SalatTimesDAO
    abstract fun salatSettingsDao(): SalatSettingsDAO

    companion object {
        private final const val DB_NAME: String = "organizer.db"

        private var instance: AppDatabase? = null;

        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    DB_NAME
                ).enableMultiInstanceInvalidation()
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3,
                        MIGRATION_3_4,
                        MIGRATION_4_5,
                        MIGRATION_5_6,
                        MIGRATION_6_7,
                        MIGRATION_7_8,
                        MIGRATION_8_9,
                        MIGRATION_9_10,
                        MIGRATION_10_11,
                        MIGRATION_11_12,
                        MIGRATION_12_13,
                        MIGRATION_13_14,
                        MIGRATION_14_15
                    )
                    .build()
            }
            return instance as AppDatabase
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
            }
        }
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE `categories` (`id` TEXT NOT NULL," +
                            " `category_name` TEXT NOT NULL, " +
                            " `transaction_type` INTEGER NOT NULL, " +
                            " `background_color` INTEGER NOT NULL, " +
                            " `font_color` INTEGER NOT NULL, " +
                            "PRIMARY KEY(`id`))"
                )
            }
        }
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE `transaction_plans` (`id` TEXT NOT NULL," +
                            " `name` TEXT NOT NULL, " +
                            " `delete_after_execute` INTEGER NOT NULL, " +
                            " `color` INTEGER NOT NULL, " +
                            "PRIMARY KEY(`id`))"
                )
                database.execSQL(
                    "CREATE TABLE `template_transactions` (`id` TEXT NOT NULL," +
                            " `transaction_plan_id` TEXT NOT NULL, " +
                            " `transaction_type` INTEGER NOT NULL, " +
                            " `amount` REAL NOT NULL, " +
                            " `from_account` TEXT , " +
                            " `to_account` TEXT , " +
                            " `transaction_category_id` TEXT , " +
                            " `details` TEXT , " +
                            " `order` INTEGER NOT NULL, " +
                            "PRIMARY KEY(`id`))"
                )
            }
        }
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE `debts` (`id` TEXT NOT NULL," +
                            " `debt_type` INTEGER NOT NULL, " +
                            " `amount` REAL NOT NULL, " +
                            " `paid_so_far` REAL NOT NULL, " +
                            " `details` TEXT NOT NULL, " +
                            " `created_at` INTEGER NOT NULL, " +
                            " `completed_at` INTEGER, " +
                            " `scheduled_at` INTEGER, " +
                            "PRIMARY KEY(`id`))"
                )
                database.execSQL("ALTER TABLE `transactions` ADD COLUMN debt_id TEXT")
            }
        }
        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `transactions` ADD COLUMN from_account_old_amount REAL")
                database.execSQL("ALTER TABLE `transactions` ADD COLUMN to_account_old_amount REAL")
                database.execSQL("ALTER TABLE `transactions` ADD COLUMN from_account_new_amount REAL")
                database.execSQL("ALTER TABLE `transactions` ADD COLUMN to_account_new_amount REAL")
            }
        }

        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE transactions RENAME TO old_transactions")
                database.execSQL(
                    "CREATE TABLE \"transactions\" (\n" +
                            "  `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, \n" +
                            "  `transaction_type` INTEGER NOT NULL,\n" +
                            "  `amount` REAL NOT NULL, `from_account` TEXT,\n" +
                            "  `to_account` TEXT, `scheduled_transaction_id` TEXT,\n" +
                            "  `transaction_category_id` TEXT,\n" +
                            "  `details` TEXT,\n" +
                            "  `transacted_at` INTEGER NOT NULL,\n" +
                            "  debt_id TEXT, from_account_old_amount REAL,\n" +
                            "  to_account_old_amount REAL,\n" +
                            "  from_account_new_amount REAL,\n" +
                            "  to_account_new_amount REAL)"
                )
                database.execSQL("INSERT INTO transactions\n" +
                        "(transaction_type, amount, from_account, to_account, scheduled_transaction_id, transaction_category_id, details, transacted_at, debt_id, from_account_old_amount, to_account_old_amount, from_account_new_amount, to_account_new_amount)\n" +
                        "SELECT transaction_type, amount, from_account, to_account, scheduled_transaction_id, transaction_category_id, details, transacted_at, debt_id, from_account_old_amount, to_account_old_amount, from_account_new_amount, to_account_new_amount FROM old_transactions ORDER BY old_transactions.transacted_at");
            }
        }

        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE `transaction_charts` (\n" +
                        "`id` TEXT NOT NULL,\n" +
                        "`chart_name` TEXT NOT NULL,\n" +
                        "`chart_type` INTEGER NOT NULL,\n" +
                        "`chart_order` INTEGER NOT NULL,\n" +
                        "`chart_entity` INTEGER NOT NULL,\n" +
                        "`start_after_transaction_id` INTEGER NOT NULL,\n" +
                        "`x_type` INTEGER NOT NULL,\n" +
                        "PRIMARY KEY(`id`))")
                database.execSQL("CREATE UNIQUE INDEX transaction_charts01 ON transaction_charts (chart_name)")
                database.execSQL("CREATE TABLE `transaction_chart_points` (\n" +
                        "`id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                        "`chart_id` TEXT NOT NULL,\n" +
                        "`label` TEXT NOT NULL,\n" +
                        "`created_at` INTEGER NOT NULL,\n" +
                        "`from_transaction_id` INTEGER NOT NULL,\n" +
                        "`to_transaction_id` INTEGER NOT NULL)\n")
                database.execSQL("CREATE TABLE `transaction_chart_values` (\n" +
                        "`id` TEXT NOT NULL PRIMARY KEY,\n" +
                        "`point_id` INTEGER NOT NULL,\n" +
                        "`value` REAL NOT NULL,\n" +
                        "`entity_id` TEXT)")
            }
        }
        private val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE `transaction_chart_to_category` (\n" +
                        "`id` TEXT NOT NULL,\n" +
                        "`chart_id` TEXT NOT NULL,\n" +
                        "`category_id` TEXT NOT NULL,\n" +
                        "PRIMARY KEY(`id`))")
                database.execSQL("CREATE UNIQUE INDEX transaction_chart_to_category_1 ON transaction_chart_to_category (chart_id, category_id)")
                database.execSQL("ALTER TABLE `transaction_charts` ADD COLUMN show_extra_one_point INTEGER NOT NULL default 0")
                database.execSQL("ALTER TABLE `transaction_charts` ADD COLUMN extra_point_label TEXT")
            }
        }
        private val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `transaction_charts` ADD COLUMN filter_categories INTEGER NOT NULL default 0")
                database.execSQL("ALTER TABLE `transaction_charts` ADD COLUMN group_categories INTEGER NOT NULL default 1")
                database.execSQL("ALTER TABLE `transaction_charts` ADD COLUMN group_transaction_type INTEGER NOT NULL default 1")
            }
        }
        private val MIGRATION_10_11 = object : Migration(10, 11) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE UNIQUE INDEX transaction_charts02 ON transaction_charts (chart_order)")
            }
        }
        private val MIGRATION_11_12 = object : Migration(11, 12) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP INDEX transaction_charts02")
            }
        }
        private val MIGRATION_12_13 = object : Migration(12, 13) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `transaction_charts` ADD COLUMN schedule_interval_type INTEGER NOT NULL default 0")
                database.execSQL("ALTER TABLE `transaction_charts` ADD COLUMN generate_at INTEGER")
                database.execSQL("ALTER TABLE `transaction_charts` ADD COLUMN last_generated_at INTEGER")
                database.execSQL("ALTER TABLE `transaction_charts` ADD COLUMN save_point INTEGER NOT NULL default 1")
            }
        }
        private val MIGRATION_13_14 = object : Migration(13, 14) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE `salat_times` (`id` TEXT NOT NULL," +
                        " `address` TEXT NOT NULL, " +
                        " `date` TEXT NOT NULL, " +
                        " `fajr_start` TEXT NOT NULL, " +
                        " `sunrise` TEXT NOT NULL, " +
                        " `dhuhr_start` TEXT NOT NULL, " +
                        " `asr_start` TEXT NOT NULL, " +
                        " `sunset` TEXT NOT NULL, " +
                        " `maghrib_start` TEXT NOT NULL, " +
                        " `isha_start` TEXT NOT NULL, " +
                        " `imsak` TEXT NOT NULL, " +
                        " `midnight` TEXT NOT NULL, " +
                        "PRIMARY KEY(`id`))")
                database.execSQL("CREATE UNIQUE INDEX index_salat_time_address_date \n" +
                        "ON salat_times(`address`, `date`)")
                database.execSQL("CREATE TABLE `salat_settings` (`id` TEXT NOT NULL," +
                        " `settings_name` TEXT NOT NULL UNIQUE, " +
                        " `address` TEXT NULL, " +
                        " `active` INTEGER NOT NULL, " +
                        " `salat_alert` INTEGER NOT NULL, " +
                        " `fajr_alert` INTEGER NOT NULL, " +
                        " `dhuhr_alert` INTEGER NOT NULL, " +
                        " `asr_alert` INTEGER NOT NULL, " +
                        " `maghrib_alert` INTEGER NOT NULL, " +
                        " `isha_alert` INTEGER NOT NULL, " +
                        " `fajr_safety` INTEGER NOT NULL, " +
                        " `dhuhr_safety` INTEGER NOT NULL, " +
                        " `asr_safety` INTEGER NOT NULL, " +
                        " `maghrib_safety` INTEGER NOT NULL, " +
                        " `isha_safety` INTEGER NOT NULL, " +
                        " `sunrise_redzone` INTEGER NOT NULL, " +
                        " `midday_redzone` INTEGER NOT NULL, " +
                        " `sunset_redzone` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`id`))")
                database.execSQL("CREATE UNIQUE INDEX index_salat_settings_settings_name \n" +
                        "ON salat_settings(settings_name)")
                val defaultSettings = SalatService.defaultBdSalatSettings()
                val args = mutableListOf<Any>()
                args.add(defaultSettings.id);
                args.add(defaultSettings.settingsName);
                args.add(defaultSettings.address?:"");
                args.add(defaultSettings.active);
                args.add(defaultSettings.salatAlert);
                args.add(defaultSettings.fajrAlert);
                args.add(defaultSettings.dhuhrAlert);
                args.add(defaultSettings.asrAlert);
                args.add(defaultSettings.maghribAlert);
                args.add(defaultSettings.ishaAlert);
                args.add(defaultSettings.fajrSafety);
                args.add(defaultSettings.dhuhrSafety);
                args.add(defaultSettings.asrSafety);
                args.add(defaultSettings.maghribSafety);
                args.add(defaultSettings.ishaSafety);
                args.add(defaultSettings.sunriseRedzone);
                args.add(defaultSettings.middayRedzone);
                args.add(defaultSettings.sunsetRedzone);
                database.execSQL("Insert into salat_settings (id, settings_name,\n" +
                        "address,\n" +
                        "active,\n" +
                        "salat_alert,\n" +
                        "fajr_alert,\n" +
                        "dhuhr_alert,\n" +
                        "asr_alert,\n" +
                        "maghrib_alert,\n" +
                        "isha_alert,\n" +
                        "fajr_safety,\n" +
                        "dhuhr_safety,\n" +
                        "asr_safety,\n" +
                        "maghrib_safety,\n" +
                        "isha_safety,\n" +
                        "sunrise_redzone,\n" +
                        "midday_redzone,\n" +
                        "sunset_redzone)" +
                        " VALUES " +
                        " ( ? " + ", ?".repeat(17) + ")", args.toTypedArray())
            }
        }
        private val MIGRATION_14_15 = object : Migration(14, 15) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `salat_settings` ADD COLUMN fajr_alert_active INTEGER NOT NULL default 0")
                database.execSQL("ALTER TABLE `salat_settings` ADD COLUMN dhuhr_alert_active INTEGER NOT NULL default 0")
                database.execSQL("ALTER TABLE `salat_settings` ADD COLUMN asr_alert_active INTEGER NOT NULL default 0")
                database.execSQL("ALTER TABLE `salat_settings` ADD COLUMN maghrib_alert_active INTEGER NOT NULL default 0")
                database.execSQL("ALTER TABLE `salat_settings` ADD COLUMN isha_alert_active INTEGER NOT NULL default 0")
            }
        }

        fun destroyInstance() {
            instance?.close();
            instance = null;
        }
    }
}