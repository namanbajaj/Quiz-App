package com.namanbajaj.quizapp

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import java.lang.Exception

class EditQuizQuestions : AppCompatActivity() {
    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_quiz_questions)

        val newName = intent.getStringExtra("New Name")
        val editingFor = findViewById<TextView>(R.id.editingFor)
        editingFor.text = "Editing \'$newName\'"

        val questionField = findViewById<EditText>(R.id.question)
        val answer1Field = findViewById<EditText>(R.id.answer1)
        val answer2Field = findViewById<EditText>(R.id.answer2)
        val answer3Field = findViewById<EditText>(R.id.answer3)
        val answer4Field = findViewById<EditText>(R.id.answer4)
        val submit = findViewById<Button>(R.id.submitNewQuestion)

        val makeToast: (String) -> (Unit) = {message: String -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show()}

        submit.setOnClickListener {
            var selectedAnswerList = findViewById<RadioGroup>(R.id.rightAnswerGroup)
            var selectedAnswer = selectedAnswerList.checkedRadioButtonId
            var radioButton = findViewById<RadioButton>(selectedAnswer)
            if(questionField.text.toString().isEmpty() || answer1Field.text.toString().isEmpty() || answer2Field.text.toString().isEmpty() || selectedAnswer == -1){
                if(questionField.text.toString().isEmpty())
                    makeToast("Question cannot be empty")
                if(answer1Field.text.toString().isEmpty())
                    makeToast("First answer choice cannot be empty")
                if(answer2Field.text.toString().isEmpty())
                    makeToast("Second answer choice cannot be empty")
                if(selectedAnswer == -1)
                    makeToast("Select an answer choice")
            }
            else if((answer3Field.text.toString().isEmpty() && radioButton.text.toString().equals("3")) || (answer4Field.text.toString().isEmpty()) && radioButton.text.toString().equals("4")){
                if(answer3Field.text.toString().isEmpty() && radioButton.text.toString().equals("3"))
                    makeToast("Answer 3 must be filled in to be an answer")
                if(answer4Field.text.toString().isEmpty() && radioButton.text.toString().equals("4"))
                    makeToast("Answer 4 must be filled in to be an answer")
            }
            else if(anyMatch(answer1Field.text.toString(), answer2Field.text.toString(), answer3Field.text.toString(), answer4Field.text.toString()))
                makeToast("Cannot have duplicate answers")
            else if(answer3Field.text.toString().isEmpty() && !answer4Field.text.toString().isEmpty())
                makeToast("Answer 3 must be filled in before answer 4")
            else{
                val db = DBHelper(this, null)

                val question = questionField.text.toString()
                var cursor = db.getQuestions(newName.toString())
                var foundQ = false
                if(cursor != null) {
                    while(cursor.moveToNext()){
                        val questionName = cursor.getString(cursor.getColumnIndex(DBHelper.QUESTION_COL))
                        println(questionName)
                        if(questionName.toString().equals(question)){
                            foundQ = true
                            break
                        }
                    }
                }
                cursor?.close()

                if(foundQ)
                    makeToast("Question already present")
                else{
                    val answer1 = answer1Field.text.toString()
                    val answer2 = answer2Field.text.toString()
                    val answer3 = answer3Field.text.toString()
                    val answer4 = answer4Field.text.toString()
                    val answers = ArrayList<String>()
                    answers.add(answer1)
                    answers.add(answer2)
                    if(!answer3.isEmpty())
                        answers.add(answer3)
                    if(!answer4.isEmpty())
                        answers.add(answer4)
                    val rightAnswer = radioButton.text.toString().toInt() - 1
                    try {
                        db.addQuiz(newName.toString(), question, answers, rightAnswer)
                        questionField.text.clear()
                        answer1Field.text.clear()
                        answer2Field.text.clear()
                        answer3Field.text.clear()
                        answer4Field.text.clear()
                        selectedAnswerList.clearCheck()
                        makeToast("Insertion successful")
                    }
                    catch(e: Exception){
                        e.printStackTrace()
                        makeToast("Insertion failed")
                    }
                }
            }
        }

        val finished = findViewById<Button>(R.id.doneAdding)
        finished.setOnClickListener {
            val intent = Intent(this, QuizList::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }

    }

    fun anyMatch(answer1: String, answer2: String, answer3: String, answer4: String): Boolean {
        return (answer1 == answer2)
                || (answer1 == answer3)
                || (answer1 == answer4)
                || (answer2 == answer3)
                || (answer2 == answer4)
                || (answer3 == answer4 && answer3 != "" && answer4 != "")
    }

    override fun onBackPressed() {
        val intent = Intent(this, QuizList::class.java)
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }
}