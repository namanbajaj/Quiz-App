package com.namanbajaj.quizapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class QuizList : AppCompatActivity() {

    var previouslySelectedRadioButtonId: Int = -1

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

            button.buttonDrawable = null

            quizList.addView(button)
        }

        quizList.setOnCheckedChangeListener { radioGroup, i ->
            radioGroup.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            var selectedQuiz = quizList.checkedRadioButtonId
            println(selectedQuiz)
            var radioButton = findViewById<RadioButton>(selectedQuiz)
            if(selectedQuiz != -1) {
                radioButton.background = ContextCompat.getDrawable(this, R.drawable.selected_option_border_bg)
                if(previouslySelectedRadioButtonId != -1 && previouslySelectedRadioButtonId != selectedQuiz){
                    var previouslySelectedRadioButton = findViewById<RadioButton>(previouslySelectedRadioButtonId)
                    previouslySelectedRadioButton.background = ContextCompat.getDrawable(this, R.drawable.border)
                }
                previouslySelectedRadioButtonId = selectedQuiz
            }
        }


        val doQuiz = findViewById<Button>(R.id.doQuiz)
        val newQuiz = findViewById<Button>(R.id.newQuiz)
        val editQuiz = findViewById<Button>(R.id.editQuiz)
        val deleteQuiz = findViewById<Button>(R.id.deleteQuiz)

        doQuiz.setOnClickListener{ view ->
            var selectedQuiz = quizList.checkedRadioButtonId
            println(selectedQuiz)
            var radioButton = findViewById<RadioButton>(selectedQuiz)
            if(selectedQuiz != -1){
                view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                var quizName = radioButton.text
                println(quizName)
                val intent = Intent(this, DoQuiz::class.java)
                intent.putExtra("Quiz Name", quizName)
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            }
            else{
                view.performHapticFeedback(HapticFeedbackConstants.REJECT)
                val toast = Toast.makeText(this, "Please select a quiz", Toast.LENGTH_LONG)
                toast.show()
            }
        }

        newQuiz.setOnClickListener{ view ->
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            val intent = Intent(this, NewQuiz::class.java)
            intent.putStringArrayListExtra("Current Quiz Names", names)
            startActivity(intent)
        }

        editQuiz.setOnClickListener{ view ->
            var selectedQuiz = quizList.checkedRadioButtonId
            println(selectedQuiz)
            var radioButton = findViewById<RadioButton>(selectedQuiz)
            if(selectedQuiz != -1){
                view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                var quizName = radioButton.text
                println(quizName)
                val intent = Intent(this, AddOrEditQuiz::class.java)
                intent.putExtra("Quiz Name", quizName)
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            }
            else{
                view.performHapticFeedback(HapticFeedbackConstants.REJECT)
                val toast = Toast.makeText(this, "Please select a quiz", Toast.LENGTH_LONG)
                toast.show()
            }
        }

        deleteQuiz.setOnClickListener{ view ->
            var selectedQuiz = quizList.checkedRadioButtonId
            println(selectedQuiz)
            var radioButton = findViewById<RadioButton>(selectedQuiz)
            if(selectedQuiz != -1){
                view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                var quizName = radioButton.text
                println(quizName)
                val db = DBHelper(this, null)
                db.deleteQuiz(quizName as String)

                recreate()
            }
            else{
                view.performHapticFeedback(HapticFeedbackConstants.REJECT)
                val toast = Toast.makeText(this, "Please select a quiz", Toast.LENGTH_LONG)
                toast.show()
            }
        }

        val privacypolicy = findViewById<TextView>(R.id.privacypolicybutton)
        privacypolicy.setOnClickListener{ view ->
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            val intent = Intent(android.content.Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://sites.google.com/view/quizbiz-privacy-policy/home")
            startActivity(intent)
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