package ru.kalianova.kotlin_productivity_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.room.Room
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.kalianova.kotlin_productivity_app.database.DatabaseConstants
import ru.kalianova.kotlin_productivity_app.database.WordsDatabase
import ru.kalianova.kotlin_productivity_app.vocabular.ThemesList
import ru.kalianova.kotlin_productivity_app.vocabular.wordsList
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {

    lateinit var db: WordsDatabase
    private lateinit var saveButton: Button
    private lateinit var wordText: EditText
    private lateinit var wordTranslation: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        saveButton = findViewById(R.id.add_vocab_button)
        wordText = findViewById(R.id.word_text)
        wordTranslation = findViewById(R.id.translation_text)


        db = Room.databaseBuilder(
            applicationContext,
            WordsDatabase::class.java,
            DatabaseConstants.TABLE_NAME
        ).build()
        saveButton.setOnClickListener {
            val word = wordText.text.toString()
            val translation = wordTranslation.text.toString()
            if (word.isEmpty() || translation.isEmpty())
                Toast.makeText(this, "Заполнены не все поля", Toast.LENGTH_SHORT).show()
            else {
                wordText.setText("")
                wordTranslation.setText("")
                GlobalScope.launch {
                    db.userDao().insert(
                        ListItemWord(
                            word.trim(' '),
                            0,
                            translation.trim(' '),
                            -1,
                            LocalDateTime.now().toString()
                        )
                    )
                }
            }
        }
    }

    fun openVocabular(view: View) {
        val intent = Intent(this, ThemesList::class.java)
        intent.putExtra("type", "vocab")
        startActivity(intent)
    }

    fun openLearningNewWords(view: View) {
        val intent = Intent(this, ThemesList::class.java)
        intent.putExtra("type", "test")
        startActivity(intent)
    }

    fun openRepeatingWords(view: View) {
        val intent = Intent(this, RepeatWords::class.java)
        startActivity(intent)
    }
}