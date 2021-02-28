package com.example.organizer.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.organizer.database.dao.*
import com.example.organizer.database.entity.*


@Database(
    entities = [
        Account::class,
        Transaction::class,
        Category::class,
        TransactionPlan::class,
        TemplateTransaction::class
    ], version = 4
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDAO
    abstract fun transactionDao(): TransactionDAO
    abstract fun categoryDao(): CategoryDAO
    abstract fun transactionPlanDao(): TransactionPlanDAO
    abstract fun templateTransactionDao(): TemplateTransactionDAO

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
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
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
                database.execSQL("CREATE TABLE `categories` (`id` TEXT NOT NULL," +
                        " `category_name` TEXT NOT NULL, " +
                        " `transaction_type` INTEGER NOT NULL, " +
                        " `background_color` INTEGER NOT NULL, " +
                        " `font_color` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`id`))")
            }
        }
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE `transaction_plans` (`id` TEXT NOT NULL," +
                        " `name` TEXT NOT NULL, " +
                        " `delete_after_execute` INTEGER NOT NULL, " +
                        " `color` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`id`))")
                database.execSQL("CREATE TABLE `template_transactions` (`id` TEXT NOT NULL," +
                        " `transaction_plan_id` TEXT NOT NULL, " +
                        " `transaction_type` INTEGER NOT NULL, " +
                        " `amount` REAL NOT NULL, " +
                        " `from_account` TEXT , " +
                        " `to_account` TEXT , " +
                        " `transaction_category_id` TEXT , " +
                        " `details` TEXT , " +
                        " `order` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`id`))")
            }
        }

        fun destroyInstance() {
            instance?.close();
            instance = null;
        }
    }
}