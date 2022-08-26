package com.namanbajaj.quizapp

data class Question(
    val question: String,
    var answers: ArrayList<String>,
    var rightAnswerIndex: Int
)
