package com.namanbajaj.quizapp

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class AddOrEditQuiz : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_or_edit_quiz)

        val quizName = intent.getStringExtra("Quiz Name")
        val decideFor = findViewById<TextView>(R.id.decideFor)
        decideFor.text = "What would you like to do with \'$quizName\'?"

        val addQuestions = findViewById<Button>(R.id.addquestions)
        val editQuestions = findViewById<Button>(R.id.editquestions)

        addQuestions.setOnClickListener {
            val intent = Intent(this, EditQuizQuestions::class.java)
            intent.putExtra("New Name", quizName)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }

        editQuestions.setOnClickListener {
            val intent = Intent(this, EditQuiz::class.java)
            intent.putExtra("Quiz Name", quizName)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
    }
}