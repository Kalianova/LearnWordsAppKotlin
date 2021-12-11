package ru.kalianova.kotlin_productivity_app.vocabular

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.kalianova.kotlin_productivity_app.ListItemTheme
import ru.kalianova.kotlin_productivity_app.ListItemWord
import ru.kalianova.kotlin_productivity_app.R
import ru.kalianova.kotlin_productivity_app.TestNewWords
import ru.kalianova.kotlin_productivity_app.database.DatabaseConstants
import ru.kalianova.kotlin_productivity_app.database.WordsDatabase

class ThemesList : AppCompatActivity() {
    var type: String = "vocab"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_themes_list)
        type = intent.getStringExtra("type").toString()
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ThemesAdapter(getWords()) {
            var intent: Intent
            if (type == "vocab")
                intent = Intent(this, wordsList::class.java)
            else
                intent = Intent(this, TestNewWords::class.java)
            intent.putExtra("theme", it.intent.toString())
            startActivity(intent)
        }
    }

    private fun getWords(): List<ListItemTheme> {
        var res = listOf<ListItemTheme>(
            ListItemTheme("Личный словарь", "Personal vocabulary", 0),
            ListItemTheme("Погода и времена года", "Weather and seasons", 1),
            ListItemTheme("Настроение", "Mood", 2),
            ListItemTheme("Природа", "Nature", 3),
            ListItemTheme("Семья и друзья", "Family and friends", 4),
            ListItemTheme("Родной дом", "Native home", 5),
        )
        return res
    }
}
