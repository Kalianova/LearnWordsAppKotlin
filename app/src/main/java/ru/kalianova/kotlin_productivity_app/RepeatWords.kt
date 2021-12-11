package ru.kalianova.kotlin_productivity_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.kalianova.kotlin_productivity_app.database.DatabaseConstants
import ru.kalianova.kotlin_productivity_app.database.WordsDatabase
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import kotlin.random.Random

class RepeatWords : AppCompatActivity() {
    private var position = 0
    private var toast: Toast? = null
    private val listRepearDays = listOf<Int>(1, 2, 4, 0, 14)
    val numberQuestions = 10
    var countRight = 0
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
        setContentView(R.layout.activity_repeat_words)
        textView = findViewById(R.id.textView)
        variant1 = findViewById(R.id.button)
        variant2 = findViewById(R.id.button2)
        variant3 = findViewById(R.id.button3)
        variant4 = findViewById(R.id.button4)
        nextButton = findViewById(R.id.button5)
        sendAnswer = findViewById(R.id.buttonSendAnswer)
        writeAnswer = findViewById(R.id.editTextWriteAnswer)
        variant1.setOnClickListener{
            if (nextButton.visibility == Button.INVISIBLE)
                onClickListener(null, variant1)
        }
        variant2.setOnClickListener{
            if (nextButton.visibility == Button.INVISIBLE)
                onClickListener(null, variant2)
        }
        variant3.setOnClickListener{
            if (nextButton.visibility == Button.INVISIBLE)
                onClickListener(null, variant3)
        }
        variant4.setOnClickListener{
            if (nextButton.visibility == Button.INVISIBLE)
                onClickListener(null, variant4)
        }
        db = Room.databaseBuilder(applicationContext, WordsDatabase::class.java, DatabaseConstants.TABLE_NAME)
            .createFromAsset("databases/weather.db").build()
        nextButton.setOnClickListener{
            if (position >= listQuestions.size) {
                makeToastFromHtml(
                    "<font color='#3D974C' ><b>" + "Верных ответов: $countRight/${listQuestions.size}" + "</b></font>"
                )
                finish()
            }
            else {
                updateUINewQuestion()
                nextButton.visibility = Button.INVISIBLE
            }
        }
        sendAnswer.setOnClickListener{
            onClickListener(writeAnswer.text.toString(), null)
            sendAnswer.visibility = Button.INVISIBLE
        }
        makeQuestions()
    }

    fun makeQuestions() {
        GlobalScope.launch {
            var wordsArray = db.userDao().getRepeatWords()
            wordsArray = wordsArray.filter {
                Period.between(
                    LocalDateTime.parse(it.date_time).toLocalDate(),
                    LocalDate.now()
                ).days >= listRepearDays[it.count]
            }
            if (wordsArray.isEmpty()) {
                withContext(Dispatchers.Main) {
                    makeToastFromHtml("<font color='#DF345C' ><b>Нет слов для изучения</b></font>")
                }
                finish()
            } else {
                for (i in (0 until numberQuestions)) {
                    if (wordsArray.size <= i)
                        break
                    when ((1..3).random()){
                        1->listQuestions.add(Question(1, wordsArray[i], null))
                        2->listQuestions.add(
                            Question(
                                2,
                                wordsArray[i],
                                arrayListOf(wordsArray[i].translation)
                            )
                        )
                        3->listQuestions.add(Question(3, wordsArray[i], arrayListOf(wordsArray[i].word)))
                    }
                    if (wordsArray.size <= i)
                        break
                }
                makeVariants()
                listQuestions.shuffle(Random)
                updateUINewQuestion()
            }
        }
    }

    fun makeVariants() {
        val allWords = ArrayList(db.userDao().getAllWords())
        allWords.shuffle(Random)
        var index = 0
        val count = listQuestions.size
        for (i in (0 until count)) {
            when (listQuestions[i].type) {
                1 -> continue
                2 -> {
                    for (j in (0 until 3)) {
                        var str = allWords[(index + j) % allWords.size].translation
                        while (str == listQuestions[i].list?.get(0))
                            str = allWords[(++index + j) % allWords.size].translation
                        listQuestions[i].list?.add(str)
                    }
                }
                3 -> {
                    for (j in (0 until 3)) {
                        var str = allWords[(index + j) % allWords.size].word
                        while (str == listQuestions[i].list?.get(0))
                            str = allWords[(++index + j) % allWords.size].word
                        listQuestions[i].list?.add(str)
                    }
                }
            }
            index += 3
            listQuestions[i].list?.shuffle(Random)
        }
    }

    fun onClickListener(answer: String?, button: Button?){
        val question: Question = listQuestions[position]
        var str = ""
        when(question.type){
            1-> {
                str = question.listItemWord.word
                if (answer?.trim(' ').equals(question.listItemWord.word, true)) {
                    question.listItemWord.count++
                    question.listItemWord.date_time = LocalDateTime.now().toString()
                    countRight++
                    makeToastFromHtml("<font color='#3D974C' ><b>" + "Ответ верный" + "</b></font>")
                } else {
                    question.listItemWord.count = -1
                    makeToastFromHtml("<font color='#DF345C' ><b>Ответ неверный. Правильный ответ: $str</b></font>")
                }
            }
            2, 3 -> {
                if (question.type == 2)
                    str = question.listItemWord.translation
                else
                    str = question.listItemWord.word
                if (button?.text.toString() == str){
                    question.listItemWord.count++
                    question.listItemWord.date_time = LocalDateTime.now().toString()
                    countRight++
                }
                else {
                    button?.background?.setTint(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.red
                        )
                    )
                    question.listItemWord.count = -1
                }
                if (variant1.text.toString() == str)
                    variant1.background.setTint(ContextCompat.getColor(applicationContext, R.color.green))
                if (variant2.text.toString() == str)
                    variant2.background.setTint(ContextCompat.getColor(applicationContext, R.color.green))
                if (variant3.text.toString() == str)
                    variant3.background.setTint(ContextCompat.getColor(applicationContext, R.color.green))
                if (variant4.text.toString() == str)
                    variant4.background.setTint(ContextCompat.getColor(applicationContext, R.color.green))
            }
        }
        GlobalScope.launch {
            db.userDao().updateItem(question.listItemWord)
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
            sendAnswer.visibility = Button.VISIBLE
        }
        else {
            variant1.background.setTint(ContextCompat.getColor(applicationContext, R.color.purple_200))
            variant2.background.setTint(ContextCompat.getColor(applicationContext, R.color.purple_200))
            variant3.background.setTint(ContextCompat.getColor(applicationContext, R.color.purple_200))
            variant4.background.setTint(ContextCompat.getColor(applicationContext, R.color.purple_200))
            variant1.text = listQuestions[position].list?.get(0) ?: ""
            variant2.text = listQuestions[position].list?.get(1) ?: ""
            variant3.text = listQuestions[position].list?.get(2) ?: ""
            variant4.text = listQuestions[position].list?.get(3) ?: ""
            if (listQuestions[position].type == 2)
                textView.text = listQuestions[position].listItemWord.word
            else
                textView.text = listQuestions[position].listItemWord.translation
        }
    }

    fun makeToastFromHtml(message: String) {
        toast?.cancel()
        toast = Toast.makeText(
            applicationContext,
            Html.fromHtml(
                message,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            ),
            Toast.LENGTH_SHORT
        )
        toast?.setGravity(Gravity.TOP, 0, 50)
        toast?.show()
    }

    fun onClickSendAnswer(view: View) {}
}