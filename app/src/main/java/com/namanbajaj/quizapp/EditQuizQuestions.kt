package com.namanbajaj.quizapp

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class EditQuizQuestions : AppCompatActivity() {
    lateinit var questionField : EditText
    lateinit var answer1Field : EditText
    lateinit var answer2Field : EditText
    lateinit var answer3Field : EditText
    lateinit var answer4Field : EditText

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_quiz_questions)

        val newName = intent.getStringExtra("New Name")
        val editingFor = findViewById<TextView>(R.id.editingFor)
        editingFor.text = "Editing \'$newName\'"

        questionField = findViewById<EditText>(R.id.question)
        answer1Field = findViewById<EditText>(R.id.answer1)
        answer2Field = findViewById<EditText>(R.id.answer2)
        answer3Field = findViewById<EditText>(R.id.answer3)
        answer4Field = findViewById<EditText>(R.id.answer4)
        val submit = findViewById<Button>(R.id.submitNewQuestion)

        val makeToast: (String) -> (Unit) = {message: String -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show()}

        submit.setOnClickListener { view ->
            var selectedAnswerList = findViewById<RadioGroup>(R.id.rightAnswerGroup)
            var selectedAnswer = selectedAnswerList.checkedRadioButtonId
            var radioButton = findViewById<RadioButton>(selectedAnswer)
            if(questionField.text.toString().isEmpty() || answer1Field.text.toString().isEmpty() || answer2Field.text.toString().isEmpty() || selectedAnswer == -1){
                view.performHapticFeedback(HapticFeedbackConstants.REJECT)
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
                view.performHapticFeedback(HapticFeedbackConstants.REJECT)
                if(answer3Field.text.toString().isEmpty() && radioButton.text.toString().equals("3"))
                    makeToast("Answer 3 must be filled in to be an answer")
                if(answer4Field.text.toString().isEmpty() && radioButton.text.toString().equals("4"))
                    makeToast("Answer 4 must be filled in to be an answer")
            }
            else if(anyMatch(answer1Field.text.toString(), answer2Field.text.toString(), answer3Field.text.toString(), answer4Field.text.toString())){
                view.performHapticFeedback(HapticFeedbackConstants.REJECT)
                makeToast("Cannot have duplicate answers")
            }
            else if(answer3Field.text.toString().isEmpty() && !answer4Field.text.toString().isEmpty()){
                view.performHapticFeedback(HapticFeedbackConstants.REJECT)
                makeToast("Answer 3 must be filled in before answer 4")
            }
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

                if(foundQ){
                    view.performHapticFeedback(HapticFeedbackConstants.REJECT)
                    makeToast("Question already present")
                }
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
                        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                    }
                    catch(e: Exception){
                        e.printStackTrace()
                        makeToast("Insertion failed")
                        view.performHapticFeedback(HapticFeedbackConstants.REJECT)
                    }
                }
            }
        }

        val finished = findViewById<Button>(R.id.doneAdding)
        finished.setOnClickListener { view ->
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            val intent = Intent(this, QuizList::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }

        addHideKeyboardOnEnter()
    }

    fun anyMatch(answer1: String, answer2: String, answer3: String, answer4: String): Boolean {
        return (answer1 == answer2)
                || (answer1 == answer3)
                || (answer1 == answer4)
                || (answer2 == answer3)
                || (answer2 == answer4)
                || (answer3 == answer4 && answer3 != "" && answer4 != "")
    }

    fun addHideKeyboardOnEnter(){
        questionField.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(arg0: View?, arg1: Int, event: KeyEvent): Boolean {
                if (event.action === KeyEvent.ACTION_DOWN && arg1 == KeyEvent.KEYCODE_ENTER) {
                    val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(questionField.windowToken, 0)
                    return true
                }
                return false
            }
        })

        answer1Field.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(arg0: View?, arg1: Int, event: KeyEvent): Boolean {
                if (event.action === KeyEvent.ACTION_DOWN && arg1 == KeyEvent.KEYCODE_ENTER) {
                    val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(answer1Field.windowToken, 0)
                    return true
                }
                return false
            }
        })

        answer2Field.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(arg0: View?, arg1: Int, event: KeyEvent): Boolean {
                if (event.action === KeyEvent.ACTION_DOWN && arg1 == KeyEvent.KEYCODE_ENTER) {
                    val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(answer2Field.windowToken, 0)
                    return true
                }
                return false
            }
        })

        answer3Field.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(arg0: View?, arg1: Int, event: KeyEvent): Boolean {
                if (event.action === KeyEvent.ACTION_DOWN && arg1 == KeyEvent.KEYCODE_ENTER) {
                    val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(answer3Field.windowToken, 0)
                    return true
                }
                return false
            }
        })

        answer4Field.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(arg0: View?, arg1: Int, event: KeyEvent): Boolean {
                if (event.action === KeyEvent.ACTION_DOWN && arg1 == KeyEvent.KEYCODE_ENTER) {
                    val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(answer4Field.windowToken, 0)
                    return true
                }
                return false
            }
        })
    }

    override fun onBackPressed() {
        val intent = Intent(this, QuizList::class.java)
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }
}