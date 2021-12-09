package ru.kalianova.kotlin_productivity_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.room.Room
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.kalianova.kotlin_productivity_app.database.DatabaseConstants
import ru.kalianova.kotlin_productivity_app.database.WordsDatabase
import kotlin.random.Random

class TestNewWords : AppCompatActivity() {
    private var position = 0
    var listQuestions: ArrayList<Question> = arrayListOf<Question>()
    lateinit var textView: TextView
    lateinit var variant1: Button
    lateinit var variant2: Button
    lateinit var variant3: Button
    lateinit var variant4: Button
    lateinit var nextButton: Button
    lateinit var writeAnswer: EditText
    lateinit var sendAnswer: Button
    lateinit var db: WordsDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_new_words)
        textView = findViewById(R.id.textView)
        variant1 = findViewById(R.id.button)
        variant2 = findViewById(R.id.button2)
        variant3 = findViewById(R.id.button3)
        variant4 = findViewById(R.id.button4)
        nextButton = findViewById(R.id.button5)
        sendAnswer = findViewById(R.id.buttonSendAnswer)
        writeAnswer = findViewById(R.id.editTextWriteAnswer)
        variant1.setOnClickListener{
            onClickListener(variant1.text.toString())
        }
        variant2.setOnClickListener{
            onClickListener(variant2.text.toString())
        }
        variant3.setOnClickListener{
            onClickListener(variant3.text.toString())
        }
        variant4.setOnClickListener{
            onClickListener(variant4.text.toString())
        }
        nextButton.setOnClickListener{
            if (position >= listQuestions.size) {
                var count = 0
                val smth = listQuestions.groupBy { it.listItemWord.id }
                for (i in smth) {
                    if (i.value[0].count + i.value[1].count + i.value[2].count == 3) {
                        count++
                        db.userDao().updateScore(0, i.key!!)
                    }
                }

            }
            updateUINewQuestion()
            nextButton.visibility = Button.INVISIBLE
        }
        sendAnswer.setOnClickListener{
            onClickListener(writeAnswer.text.toString())
        }
        db = Room.databaseBuilder(applicationContext, WordsDatabase::class.java, DatabaseConstants.TABLE_NAME).build()
        makeQuestions()
    }

    fun makeQuestions(){
        GlobalScope.launch {
            val wordsArray = db.userDao().getNewWords(-1) //addTheme
            for (i in (0..9))  {
                if (wordsArray.size <= i)
                    break
                listQuestions.add(Question(2, wordsArray[i], null))
                listQuestions.add(Question(3, wordsArray[i], null))
            }
            listQuestions.shuffle(Random)
            for (i in (0..9)) {
                if (wordsArray.size <= i)
                    break
                listQuestions.add(Question(1, wordsArray[i], null))
            }
            updateUINewQuestion()
        }
    }

    fun makeVariants(){

    }

    fun onClickListener(answer: String){
        var question: Question = listQuestions[position]
        when(question.type){
            1->{
                if (answer.equals(question.listItemWord.word)){
                    question.count++

                }
            }
            2 -> {
                if (answer.equals(question.listItemWord.translation)){
                    question.count++
                }
            }
            3 -> {
                if (answer.equals(question.listItemWord.word)){
                    question.count++
                }
            }
        }
        nextButton.visibility = Button.VISIBLE
        position++
    }

    fun updateUIType1Question() {
        variant1.visibility = Button.INVISIBLE
        variant2.visibility = Button.INVISIBLE
        variant3.visibility = Button.INVISIBLE
        variant4.visibility = Button.INVISIBLE
        writeAnswer.visibility = EditText.VISIBLE
        sendAnswer.visibility = Button.VISIBLE
    }

    fun updateUINewQuestion() {
        if (listQuestions[position].type == 1)
        {
            updateUIType1Question()
            textView.text = listQuestions[position].listItemWord.translation
            writeAnswer.setText("")
        }
        else {
            variant1.text = listQuestions[position]?.list?.get(0) ?: ""
            variant2.text = listQuestions[position]?.list?.get(1) ?: ""
            variant3.text = listQuestions[position]?.list?.get(2) ?: ""
            variant4.text = listQuestions[position]?.list?.get(3) ?: ""
            if (listQuestions[position].type == 2)
                textView.text = listQuestions[position].listItemWord.word
            else
                textView.text = listQuestions[position].listItemWord.translation
        }
    }

    fun onClickSendAnswer(view: View) {}
}