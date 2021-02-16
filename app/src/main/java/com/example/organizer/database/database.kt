package com.example.organizer.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.organizer.database.dao.AccountDAO
import com.example.organizer.database.dao.TransactionDAO
import com.example.organizer.database.entity.Account
import com.example.organizer.database.entity.Transaction


@Database(
    entities = [
        Account::class,
        Transaction::class
    ], version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDAO
    abstract fun transactionDao(): TransactionDAO

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
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance as AppDatabase
        }

        fun destroyInstance() {
            instance?.close();
            instance = null;
        }
    }
}