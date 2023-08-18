package com.namanbajaj.quizapp

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.view.setPadding

class FinalScore : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_final_score)

        val name = intent.getStringExtra("name")
        val score = intent.getStringExtra("score")
        val size = intent.getStringExtra("size")
        val questions = intent.getStringArrayListExtra("Questions")
        println(name)
        println(score)
        println(size)
        println(questions)

        val scoreText = findViewById<TextView>(R.id.finalScore)
        scoreText.text = "$score/$size"


        val textView = findViewById<TextView>(R.id.textView)
        val textView2 = findViewById<TextView>(R.id.textView2)
        if (score != null && size != null && score.toInt() == size.toInt()) {
            textView.text = "Congratulations, you got a perfect score!"
            textView2.text = ""
        }

        else {
            val layout = findViewById<LinearLayout>(R.id.linearLayout)
            if (questions != null) {
                for (i in 0 until questions.size) {
                    val params = RadioGroup.LayoutParams(this, null)
                    params.setMargins(20, 20, 20, 20)
                    params.width = RadioGroup.LayoutParams.MATCH_PARENT
                    params.height = RadioGroup.LayoutParams.MATCH_PARENT

                    val textView3 = TextView(this)
                    textView3.text = questions[i] ?: "No Questions"
                    textView3.textSize = 20f
                    textView3.setTextColor(resources.getColor(R.color.white))
                    textView3.layoutParams = params
                    textView3.setPadding(20)

                    layout.addView(textView3)
                }
            }
        }

        val goHome = findViewById<TextView>(R.id.home)
        goHome.setOnClickListener { view ->
            view.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY)
            val intent = Intent(this, QuizList::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, QuizList::class.java)
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }
}