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
import java.time.LocalDateTime
import kotlin.random.Random

class TestNewWords : AppCompatActivity() {
    private var position = 0
    var name = 0
    private var toast: Toast? = null
    val numberQuestions = 5
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
        setContentView(R.layout.activity_test_new_words)
        name = intent.getStringExtra("theme")?.toInt() ?: 0
        textView = findViewById(R.id.textView)
        variant1 = findViewById(R.id.button)
        variant2 = findViewById(R.id.button2)
        variant3 = findViewById(R.id.button3)
        variant4 = findViewById(R.id.button4)
        nextButton = findViewById(R.id.button5)
        sendAnswer = findViewById(R.id.buttonSendAnswer)
        writeAnswer = findViewById(R.id.editTextWriteAnswer)
        variant1.setOnClickListener {
            if (nextButton.visibility == Button.INVISIBLE)
                onClickListener(null, variant1)
        }
        variant2.setOnClickListener {
            if (nextButton.visibility == Button.INVISIBLE)
                onClickListener(null, variant2)
        }
        variant3.setOnClickListener {
            if (nextButton.visibility == Button.INVISIBLE)
                onClickListener(null, variant3)
        }
        variant4.setOnClickListener {
            if (nextButton.visibility == Button.INVISIBLE)
                onClickListener(null, variant4)
        }
        db = Room.databaseBuilder(
            applicationContext,
            WordsDatabase::class.java,
            DatabaseConstants.TABLE_NAME
        )
            .createFromAsset("databases/weather.db").build()
        nextButton.setOnClickListener {
            if (position >= listQuestions.size) {
                var count = 0
                val smth = listQuestions.groupBy { it.listItemWord.id }
                for (i in smth) {
                    if (i.value[0].count + i.value[1].count + i.value[2].count == 3) {
                        count++
                        i.value[0].listItemWord.count++
                        i.value[0].listItemWord.date_time = LocalDateTime.now().toString()
                        GlobalScope.launch {
                            db.userDao().updateItem(i.value[0].listItemWord)
                            finish()
                        }
                    }
                }
                makeToastFromHtml(
                    "<font color='#3D974C' ><b>" + "???????????? ??????????????: $countRight/${listQuestions.size}" + "</b>" +
                            "<b><br>" + "???????? ??????????????: $count/${listQuestions.size / 3}" + "</br><b></font>"
                )
                finish()
            } else {
                updateUINewQuestion()
                nextButton.visibility = Button.INVISIBLE
            }
        }
        sendAnswer.setOnClickListener {
            onClickListener(writeAnswer.text.toString(), null)
            sendAnswer.visibility = Button.INVISIBLE
        }
        makeQuestions()
    }

    fun makeQuestions() {
        GlobalScope.launch {
            val wordsArray = db.userDao().getNewWords(-1, name)
            if (wordsArray.isEmpty()) {
                withContext(Dispatchers.Main) {
                    makeToastFromHtml("<font color='#DF345C' ><b>?????? ???????? ?????? ????????????????</b></font>")
                }
                finish()
            } else {
                for (i in (0 until numberQuestions)) {
                    if (wordsArray.size <= i)
                        break
                    listQuestions.add(
                        Question(
                            2,
                            wordsArray[i],
                            arrayListOf(wordsArray[i].translation)
                        )
                    )
                    listQuestions.add(Question(3, wordsArray[i], arrayListOf(wordsArray[i].word)))
                }
                makeVariants()
                listQuestions.shuffle(Random)
                for (i in (0 until numberQuestions)) {
                    if (wordsArray.size <= i)
                        break
                    listQuestions.add(Question(1, wordsArray[i], null))
                }
                updateUINewQuestion()
            }
        }
    }

    fun makeVariants(){
        val allWords = ArrayList(db.userDao().getAllWords())
        allWords.shuffle(Random)
        var index = 0
        val count = listQuestions.size / 2
        for(i in (0 until count)){
            for(j in (0 until 3)) {
                var str = allWords[(index + j) % allWords.size].translation
                while (str == listQuestions[i * 2].list?.get(0))
                    str = allWords[(++index + j) % allWords.size].translation
                listQuestions[i * 2].list?.add(str)
            }
            index+=3
            listQuestions[i * 2].list?.shuffle(Random)
            for(j in (0 until 3)) {
                var str = allWords[(index + j) % allWords.size].word
                while (str == listQuestions[i * 2 + 1].list?.get(0))
                    str = allWords[(++index + j) % allWords.size].word
                listQuestions[i * 2 + 1].list?.add(str)
            }
            index+=3
            listQuestions[i * 2 + 1].list?.shuffle(Random)
        }
    }

    fun onClickListener(answer: String?, button: Button?){
        val question: Question = listQuestions[position]
        var str = ""
        when(question.type){
            1-> {
                str = question.listItemWord.word
                if (answer?.trim(' ').equals(question.listItemWord.word, true)) {
                    question.count++
                    countRight++
                    makeToastFromHtml("<font color='#3D974C' ><b>" + "?????????? ????????????" + "</b></font>")
                } else {
                    makeToastFromHtml("<font color='#DF345C' ><b>?????????? ????????????????. ???????????????????? ??????????: $str</b></font>")
                }
            }
            2, 3 -> {
                if (question.type == 2)
                    str = question.listItemWord.translation
                else
                    str = question.listItemWord.word
                if (button?.text.toString() == str){
                    question.count++
                    countRight++
                }
                else
                    button?.background?.setTint(ContextCompat.getColor(applicationContext, R.color.red))
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