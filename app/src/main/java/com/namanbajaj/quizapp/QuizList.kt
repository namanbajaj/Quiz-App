package com.namanbajaj.quizapp

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.*

class QuizList : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_list)

        val names = loadData()
//        println("size is " + names.size)

        for(qname in names)
            println(qname)

        val quizList = findViewById<RadioGroup>(R.id.quizNamesRadio)
        quizList.gravity = Gravity.CENTER
        for(quiz in names){
            val button = RadioButton(this)

            button.setPadding(10, 20, 10, 20)
            val params = RadioGroup.LayoutParams(this, null)
            params.setMargins(0, 20, 0, 20)
            params.width = RadioGroup.LayoutParams.MATCH_PARENT
            params.height = RadioGroup.LayoutParams.MATCH_PARENT
            button.layoutParams = params

            button.text = quiz
            button.textSize = 24F
            button.setTextColor(Color.BLACK)
            button.typeface = Typeface.DEFAULT_BOLD
            button.setBackgroundResource(R.drawable.border)
            button.gravity = Gravity.CENTER
            quizList.addView(button)
        }



        val doQuiz = findViewById<Button>(R.id.doQuiz)
        val newQuiz = findViewById<Button>(R.id.newQuiz)
        val editQuiz = findViewById<Button>(R.id.editQuiz)
        val deleteQuiz = findViewById<Button>(R.id.deleteQuiz)

        doQuiz.setOnClickListener{
            var selectedQuiz = quizList.checkedRadioButtonId
            println(selectedQuiz)
            var radioButton = findViewById<RadioButton>(selectedQuiz)
            if(selectedQuiz != -1){
                var quizName = radioButton.text
                println(quizName)
                val intent = Intent(this, DoQuiz::class.java)
                intent.putExtra("Quiz Name", quizName)
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            }
            else{
                val toast = Toast.makeText(this, "Please select a quiz", Toast.LENGTH_LONG)
                toast.show()
            }
        }

        newQuiz.setOnClickListener{
            val intent = Intent(this, NewQuiz::class.java)
            intent.putStringArrayListExtra("Current Quiz Names", names)
            startActivity(intent)
        }

        editQuiz.setOnClickListener{
            var selectedQuiz = quizList.checkedRadioButtonId
            println(selectedQuiz)
            var radioButton = findViewById<RadioButton>(selectedQuiz)
            if(selectedQuiz != -1){
                var quizName = radioButton.text
                println(quizName)
                val intent = Intent(this, AddOrEditQuiz::class.java)
                intent.putExtra("Quiz Name", quizName)
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            }
            else{
                val toast = Toast.makeText(this, "Please select a quiz", Toast.LENGTH_LONG)
                toast.show()
            }
        }

        deleteQuiz.setOnClickListener{
            var selectedQuiz = quizList.checkedRadioButtonId
            println(selectedQuiz)
            var radioButton = findViewById<RadioButton>(selectedQuiz)
            if(selectedQuiz != -1){
                var quizName = radioButton.text
                println(quizName)
                val db = DBHelper(this, null)
                db.deleteQuiz(quizName as String)

                recreate()
            }
            else{
                val toast = Toast.makeText(this, "Please select a quiz", Toast.LENGTH_LONG)
                toast.show()
            }
        }
    }

    @SuppressLint("Range")
    fun loadData(): ArrayList<String> {
        val result = ArrayList<String>()
        val dbHelper = DBHelper(this, null)
        val cursor = dbHelper.getQuizNames()
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val quizName = cursor.getString(cursor.getColumnIndex(DBHelper.NAME_COl))
                result.add(quizName)
                // println(qname)
            }
        }
        cursor?.close()
        return result
    }
}