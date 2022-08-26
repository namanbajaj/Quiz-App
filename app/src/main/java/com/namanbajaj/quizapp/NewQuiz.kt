package com.namanbajaj.quizapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class NewQuiz : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_quiz)

        val quizName = findViewById<EditText>(R.id.quizNameField)
        val submitButton = findViewById<Button>(R.id.createNewQuiz)

        val names : ArrayList<String> = intent.getStringArrayListExtra("Current Quiz Names") as ArrayList<String>
        for(name in names)
            println(name)

        submitButton.setOnClickListener {
            println(quizName.text)
            if (names != null) {
                if(quizName.text.toString() in names)
                    Toast.makeText(this, "Quiz with same name present", Toast.LENGTH_LONG).show()
                else {
                    val intent = Intent(this, EditQuizQuestions::class.java)
                    intent.putExtra("New Name", quizName.text.toString())
                    startActivity(intent)
                }
            }
        }

    }
}