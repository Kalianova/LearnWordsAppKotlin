package ru.kalianova.kotlin_productivity_app.database

import androidx.room.*
import ru.kalianova.kotlin_productivity_app.ListItemWord

@Dao
interface UserDao {
    @Query("SELECT * FROM ${DatabaseConstants.TABLE_NAME} " +
            "WHERE ${DatabaseConstants.COLUMN_NAME_THEME} = :theme")
    fun getAllWordsTheme(theme: Int):List<ListItemWord>

    @Query("SELECT * FROM ${DatabaseConstants.TABLE_NAME}")
    fun getAllWords():List<ListItemWord>

    @Insert
    fun insert(vararg word: ListItemWord)

    @Query( "SELECT *FROM ${DatabaseConstants.TABLE_NAME} " +
            "WHERE ${DatabaseConstants.COLUMN_NAME_ID} = :id")
    fun getItem(id: Int): List<ListItemWord>

    @Update
    fun updateItem(listItemWord: ListItemWord)

    @Query ("SELECT * FROM ${DatabaseConstants.TABLE_NAME} " +
            "WHERE ${DatabaseConstants.COLUMN_NAME_COUNT} = :count " +
            "AND ${DatabaseConstants.COLUMN_NAME_THEME} = :theme")
    fun getNewWords(count: Int, theme: Int): List<ListItemWord>

    @Query ("SELECT * FROM ${DatabaseConstants.TABLE_NAME} " +
            "WHERE ${DatabaseConstants.COLUMN_NAME_COUNT} != -1 " +
            "AND ${DatabaseConstants.COLUMN_NAME_COUNT} < 5")
    fun getRepeatWords(): List<ListItemWord>

    @Delete
    fun delete(word: ListItemWord)

    @Query ("DELETE FROM ${DatabaseConstants.TABLE_NAME}")
    fun deleteAll()

}
