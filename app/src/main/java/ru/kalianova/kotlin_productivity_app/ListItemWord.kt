package ru.kalianova.kotlin_productivity_app

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import ru.kalianova.kotlin_productivity_app.database.DatabaseConstants
import java.time.LocalDateTime

@Entity
data class ListItemWord(
    @ColumnInfo(name = DatabaseConstants.COLUMN_NAME_WORD) var word: String,
    @ColumnInfo(name = DatabaseConstants.COLUMN_NAME_THEME, defaultValue = "0") var theme: Int,
    @ColumnInfo(name = DatabaseConstants.COLUMN_NAME_TRANSLATION)var translation: String,
    @ColumnInfo(name = DatabaseConstants.COLUMN_NAME_COUNT)var count: Int,
    @ColumnInfo(name = DatabaseConstants.COLUMN_NAME_DATE_TIME)var date_time: String)
{
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = DatabaseConstants.COLUMN_NAME_ID) var id: Int? = null
}

data class ListItemTheme(val name: String, val key: String, val intent: Int)