package ru.kalianova.kotlin_productivity_app.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.kalianova.kotlin_productivity_app.ListItemWord

@Database(entities = [ListItemWord::class], version = DatabaseConstants.DB_VERSION)
abstract class WordsDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
}