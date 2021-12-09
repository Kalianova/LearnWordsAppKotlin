package ru.kalianova.kotlin_productivity_app

data class Question(val type: Int,
                    val listItemWord: ListItemWord,
                    var list: ArrayList<String>?,
                    var count: Int = 0)
