package ru.kalianova.kotlin_productivity_app.vocabular

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.kalianova.kotlin_productivity_app.ListItemWord
import ru.kalianova.kotlin_productivity_app.R
import ru.kalianova.kotlin_productivity_app.database.DatabaseConstants
import ru.kalianova.kotlin_productivity_app.database.WordsDatabase

class wordsList : AppCompatActivity() {
    lateinit var db: WordsDatabase
    var name = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_words_list)

        name = intent.getStringExtra("theme")?.toInt() ?: 0
        db = Room.databaseBuilder(
            applicationContext,
            WordsDatabase::class.java,
            DatabaseConstants.TABLE_NAME
        ).createFromAsset("databases/weather.db").build()
        //db.userDao().deleteAll()
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        GlobalScope.launch {
            recyclerView.adapter = WordsAdapter(getWords()){
                GlobalScope.launch {
                    db.userDao().delete(it)
                }
            }
        }
    }

    private suspend fun getWords(): MutableList<ListItemWord> {
        return db.userDao().getAllWordsTheme(name).toMutableList()
    }
}