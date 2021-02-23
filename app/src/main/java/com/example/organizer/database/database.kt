package com.example.organizer.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.organizer.database.dao.AccountDAO
import com.example.organizer.database.dao.CategoryDAO
import com.example.organizer.database.dao.TransactionDAO
import com.example.organizer.database.entity.Account
import com.example.organizer.database.entity.Category
import com.example.organizer.database.entity.Transaction


@Database(
    entities = [
        Account::class,
        Transaction::class,
        Category::class
    ], version = 3
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDAO
    abstract fun transactionDao(): TransactionDAO
    abstract fun categoryDao(): CategoryDAO

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
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
            }
            return instance as AppDatabase
        }
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
            }
        }
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE `categories` (`id` TEXT NOT NULL," +
                        " `category_name` TEXT NOT NULL, " +
                        " `transaction_type` INTEGER NOT NULL, " +
                        " `background_color` INTEGER NOT NULL, " +
                        " `font_color` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`id`))")
            }
        }

        fun destroyInstance() {
            instance?.close();
            instance = null;
        }
    }
}