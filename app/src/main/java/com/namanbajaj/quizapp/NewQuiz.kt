package com.namanbajaj.quizapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class NewQuiz : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_quiz)

        val quizName = findViewById<EditText>(R.id.quizNameField)
        val submitButton = findViewById<Button>(R.id.createNewQuiz)

        val names : ArrayList<String> = intent.getStringArrayListExtra("Current Quiz Names") as ArrayList<String>
        for(name in names)
            println(name)

        submitButton.setOnClickListener { view ->
            println(quizName.text)
            if (names != null) {
                if(quizName.text.toString() in names){
                    Toast.makeText(this, "Quiz with same name present", Toast.LENGTH_LONG).show()
                    view.performHapticFeedback(HapticFeedbackConstants.REJECT)
                }
                else {
                    view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                    val intent = Intent(this, EditQuizQuestions::class.java)
                    intent.putExtra("New Name", quizName.text.toString())
                    startActivity(intent)
                }
            }
        }


        quizName.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(arg0: View?, arg1: Int, event: KeyEvent): Boolean {
                Log.i("Key pressed: ", arg1.toString())
                if (event.action == KeyEvent.ACTION_DOWN && arg1 == KeyEvent.KEYCODE_ENTER) {
                    val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(quizName.windowToken, 0)
                    return true
                }
                return false
            }
        })

    }

}