package com.namanbajaj.quizapp

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AddOrEditQuiz : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_or_edit_quiz)

        val quizName = intent.getStringExtra("Quiz Name")
        val decideFor = findViewById<TextView>(R.id.decideFor)
        decideFor.text = "What would you like to do with \'$quizName\'?"

        val addQuestions = findViewById<Button>(R.id.addquestions)
        val editQuestions = findViewById<Button>(R.id.editquestions)

        addQuestions.setOnClickListener { view ->
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            val intent = Intent(this, EditQuizQuestions::class.java)
            intent.putExtra("New Name", quizName)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }

        editQuestions.setOnClickListener { view ->
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            val intent = Intent(this, EditQuiz::class.java)
            intent.putExtra("Quiz Name", quizName)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
    }
}