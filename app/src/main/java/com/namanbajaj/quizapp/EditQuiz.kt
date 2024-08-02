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

class EditQuiz : AppCompatActivity() {

    lateinit var editQuestion : Button
    lateinit var deleteQuestion : Button
    lateinit var prev : Button
    lateinit var next : Button

    lateinit var questionField : TextView
    lateinit var answer1Field : TextView
    lateinit var answer2Field : TextView
    lateinit var answer3Field : TextView
    lateinit var answer4Field : TextView

    lateinit var r1: RadioButton
    lateinit var r2: RadioButton
    lateinit var r3: RadioButton
    lateinit var r4: RadioButton
    lateinit var rightAnswerRadio: RadioGroup

    lateinit var quizName: String

    lateinit var question: String
    lateinit var answer1: String
    lateinit var answer2: String
    lateinit var answer3: String
    lateinit var answer4: String
    var rightAnswerIndex: Int = -1

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_quiz)

        val makeToast: (String) -> (Unit) = {message: String -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show()}

        val newName = intent.getStringExtra("Quiz Name")
        val editingFor = findViewById<TextView>(R.id.editingFor)
        editingFor.text = "Editing \'$newName\'"
        if (newName != null) {
            quizName = newName
        }

        // Disable radio buttons
        rightAnswerRadio = findViewById<RadioGroup>(R.id.rightAnswerGroup)
        r1 = findViewById<RadioButton>(R.id.checkBox1)
        r2 = findViewById<RadioButton>(R.id.checkBox2)
        r3 = findViewById<RadioButton>(R.id.checkBox3)
        r4 = findViewById<RadioButton>(R.id.checkBox4)
        r1.isEnabled = false
        r2.isEnabled = false
        r3.isEnabled = false
        r4.isEnabled = false

        // Disable question/answer fields
        questionField = findViewById<EditText>(R.id.question)
        answer1Field = findViewById<EditText>(R.id.answer1)
        answer2Field = findViewById<EditText>(R.id.answer2)
        answer3Field = findViewById<EditText>(R.id.answer3)
        answer4Field = findViewById<EditText>(R.id.answer4)
        questionField.isEnabled = false
        answer1Field.isEnabled = false
        answer2Field.isEnabled = false
        answer3Field.isEnabled = false
        answer4Field.isEnabled = false

        // If data is currently being edited
        var currentlyEditing = false

        // List of questions
        var questions = ArrayList<String>()

        val db = DBHelper(this, null)
        var cursor = db.getQuestions(newName.toString())
        if(cursor != null) {
            while(cursor.moveToNext()){
                val question = cursor.getString(cursor.getColumnIndex(DBHelper.QUESTION_COL))
//                val answer = cursor.getString(cursor.getColumnIndex(DBHelper.ANSWER_COL))
//                val rightAnswer = cursor.getString(cursor.getColumnIndex(DBHelper.RIGHT_ANSWER_COL)).toInt()
//                val q = Question(question, answer, rightAnswer)
                println(question.toString())
                questions.add(question.toString())
            }
        }
        cursor?.close()

        // Current index in questions list
        var curIndex = 0

        // set question fields as needed, contains original data if cancel clicked
        question = questions[0]
        questionField.text = question
        answer1 = ""
        answer2 = ""
        answer3 = ""
        answer4 = ""
        var curAnswerIndex = 0
        rightAnswerIndex = 0

        cursor = db.getAnswers(newName.toString(), question)
        if(cursor != null) {
            while(cursor.moveToNext()){
                val answer = cursor.getString(cursor.getColumnIndex(DBHelper.ANSWER_COL))
                val rightAnswer = cursor.getString(cursor.getColumnIndex(DBHelper.RIGHT_ANSWER_COL)).toInt()
                when(curAnswerIndex){
                    0 -> {
                        answer1Field.text = answer
                        answer1 = answer
                        if(rightAnswer == 1){
                            rightAnswerIndex = r1.id
                            rightAnswerRadio.check(r1.id)
                        }
                    }
                    1 -> {
                        answer2Field.text = answer
                        answer2 = answer
                        if(rightAnswer == 1){
                            rightAnswerIndex = r2.id
                            rightAnswerRadio.check(r2.id)
                        }
                    }
                    2 -> {
                        answer3Field.text = answer
                        answer3 = answer
                        if(rightAnswer == 1){
                            rightAnswerIndex = r3.id
                            rightAnswerRadio.check(r3.id)
                        }
                    }
                    3 -> {
                        answer4Field.text = answer
                        answer4 = answer
                        if(rightAnswer == 1){
                            rightAnswerIndex = r4.id
                            rightAnswerRadio.check(r4.id)
                        }
                    }
                }
                curAnswerIndex++
            }
        }
        cursor?.close()

        val finished = findViewById<Button>(R.id.doneAdding)
        finished.setOnClickListener { view ->
            if(!currentlyEditing)
            {
                view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                val intent = Intent(this, QuizList::class.java)
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            }
            // Delete old question and add new one
            else {
                var selectedAnswer = rightAnswerRadio.checkedRadioButtonId
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
                    makeToast("Cannot have duplicate answers")
                }
                else if(answer3Field.text.toString().isEmpty() && !answer4Field.text.toString().isEmpty()){
                    view.performHapticFeedback(HapticFeedbackConstants.REJECT)
                    makeToast("Answer 3 must be filled in before answer 4")
                }
                else {
                    val db2 = DBHelper(this, null)
                    db2.deleteQuestion(newName.toString(), question.toString())

                    val newQuestion = questionField.text.toString()
                    val cursor2 = db2.getQuestions(newName.toString())
                    var foundQ = false
                    if (cursor2 != null) {
                        while (cursor2.moveToNext()) {
                            val questionName =
                                cursor2.getString(cursor2.getColumnIndex(DBHelper.QUESTION_COL))
                            println(questionName)
                            if (questionName.toString() == newQuestion) {
                                foundQ = true
                                break
                            }
                        }
                    }
                    cursor2?.close()

                    if (foundQ){
                        view.performHapticFeedback(HapticFeedbackConstants.REJECT)
                        makeToast("Question already present")
                    }
                    else {
                        val newAnswer1 = answer1Field.text.toString()
                        val newAnswer2 = answer2Field.text.toString()
                        val newAnswer3 = answer3Field.text.toString()
                        val newAnswer4 = answer4Field.text.toString()
                        val answers = ArrayList<String>()
                        answers.add(newAnswer1)
                        answers.add(newAnswer2)
                        if (!newAnswer3.isEmpty())
                            answers.add(newAnswer3)
                        if (!newAnswer4.isEmpty())
                            answers.add(newAnswer4)
                        val rightAnswer = radioButton.text.toString().toInt() - 1
                        try {
                            db2.addQuiz(newName.toString(), newQuestion, answers, rightAnswer)
                            makeToast("Insertion successful")
                            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            makeToast("Insertion failed")
                            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                        }

                        currentlyEditing = false
                        deleteQuestion.visibility = View.VISIBLE
                        prev.visibility = View.VISIBLE
                        next.visibility = View.VISIBLE
                        editQuestion.text = "Edit"
                        finished.text = "Done"
                        rightAnswerRadio.isEnabled = false
                        questionField.isEnabled = false
                        answer1Field.isEnabled = false
                        answer2Field.isEnabled = false
                        answer3Field.isEnabled = false
                        answer4Field.isEnabled = false
                        r1.isEnabled = false
                        r2.isEnabled = false
                        r3.isEnabled = false
                        r4.isEnabled = false
                    }
                }
            }
        }

        editQuestion = findViewById<Button>(R.id.editQuestion)
        deleteQuestion = findViewById<Button>(R.id.deleteQuestion)
        prev = findViewById<Button>(R.id.prevQuestion)
        next = findViewById<Button>(R.id.nextQuestion)

        prev.visibility = View.INVISIBLE
        if(questions.size == 1)
            next.visibility = View.INVISIBLE

        editQuestion.setOnClickListener { view ->
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)

            if(currentlyEditing){
                currentlyEditing = false
                deleteQuestion.visibility = View.VISIBLE
                prev.visibility = View.VISIBLE
                next.visibility = View.VISIBLE
                editQuestion.text = "Edit"
                finished.text = "Done"
                rightAnswerRadio.isEnabled = false
                questionField.isEnabled = false
                answer1Field.isEnabled = false
                answer2Field.isEnabled = false
                answer3Field.isEnabled = false
                answer4Field.isEnabled = false
                r1.isEnabled = false
                r2.isEnabled = false
                r3.isEnabled = false
                r4.isEnabled = false

                questionField.text = question
                answer1Field.text = answer1
                answer2Field.text = answer2
                answer3Field.text = answer3
                answer4Field.text = answer4
                rightAnswerRadio.check(rightAnswerIndex)
            }

            else{
                currentlyEditing = true

                question = questionField.text.toString()
                answer1 = answer1Field.text.toString()
                answer2 = answer2Field.text.toString()
                answer3 = answer3Field.text.toString()
                answer4 = answer4Field.text.toString()

                deleteQuestion.visibility = View.INVISIBLE
                prev.visibility = View.INVISIBLE
                next.visibility = View.INVISIBLE

                editQuestion.text = "Cancel"
                finished.text = "Change"

                rightAnswerRadio.isEnabled = true
                questionField.isEnabled = true
                answer1Field.isEnabled = true
                answer2Field.isEnabled = true
                answer3Field.isEnabled = true
                answer4Field.isEnabled = true
                r1.isEnabled = true
                r2.isEnabled = true
                r3.isEnabled = true
                r4.isEnabled = true
            }

        }

        next.setOnClickListener { view ->
//            questionField.text = ""
//            answer1Field.text = ""
//            answer2Field.text = ""
//            answer3Field.text = ""
//            answer4Field.text = ""

            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)

            if(prev.visibility == View.INVISIBLE)
                prev.visibility = View.VISIBLE

            curIndex++
            if(curIndex >= questions.size - 1) {
                next.visibility = View.INVISIBLE
                curIndex = questions.size - 1
            }
            loadQuestion(questions, curIndex)
        }

        prev.setOnClickListener { view ->
//            questionField.text = ""
//            answer1Field.text = ""
//            answer2Field.text = ""
//            answer3Field.text = ""
//            answer4Field.text = ""

            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)

            if(next.visibility == View.INVISIBLE)
                next.visibility = View.VISIBLE

            curIndex--
            if(curIndex <= 0){
                prev.visibility = View.INVISIBLE
                curIndex = 0
            }
            loadQuestion(questions, curIndex)
        }

        val deleteButton = findViewById<Button>(R.id.deleteQuestion)
        deleteButton.setOnClickListener { view ->
            if(questions.size == 1){
                view.performHapticFeedback(HapticFeedbackConstants.REJECT)
                makeToast("Cannot have quiz with 0 questions, delete quiz or add questions")
            }
            else {
                view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                questions.removeAt(curIndex)
                curIndex = 0
                db.deleteQuestion(quizName, questionField.text.toString())
                loadQuestion(questions, curIndex)
                makeToast("Question deleted")
            }
        }

        addHideKeyboardOnEnter()
    }

    @SuppressLint("Range")
    fun loadQuestion(questions : ArrayList<String>, curIndex : Int){
        questionField.text = ""
        answer1Field.text = ""
        answer2Field.text = ""
        answer3Field.text = ""
        answer4Field.text = ""

        val db = DBHelper(this, null)

        var question: String = questions[curIndex]
        questionField.text = question
        answer1 = ""
        answer2 = ""
        answer3 = ""
        answer4 = ""
        var curAnswerIndex = 0

        val cursor = db.getAnswers(quizName.toString(), question)
        if(cursor != null) {
            while(cursor.moveToNext()){
                val answer = cursor.getString(cursor.getColumnIndex(DBHelper.ANSWER_COL))
                val rightAnswer = cursor.getString(cursor.getColumnIndex(DBHelper.RIGHT_ANSWER_COL)).toInt()
                when(curAnswerIndex){
                    0 -> {
                        answer1Field.text = answer
                        answer1 = answer
                        if(rightAnswer == 1){
                            rightAnswerIndex = r1.id
                            rightAnswerRadio.check(r1.id)
                        }
                    }
                    1 -> {
                        answer2Field.text = answer
                        answer2 = answer
                        if(rightAnswer == 1){
                            rightAnswerIndex = r2.id
                            rightAnswerRadio.check(r2.id)
                        }
                    }
                    2 -> {
                        answer3Field.text = answer
                        answer3 = answer
                        if(rightAnswer == 1){
                            rightAnswerIndex = r3.id
                            rightAnswerRadio.check(r3.id)
                        }
                    }
                    3 -> {
                        answer4Field.text = answer
                        answer4 = answer
                        if(rightAnswer == 1){
                            rightAnswerIndex = r4.id
                            rightAnswerRadio.check(r4.id)
                        }
                    }
                }
                curAnswerIndex++
            }
        }
        cursor?.close()

//        println(curAnswerIndex)
    }

    fun anyMatch(answer1: String, answer2: String, answer3: String, answer4: String): Boolean {
        return (answer1 == answer2)
                || (answer1 == answer3)
                || (answer1 == answer4)
                || (answer2 == answer3)
                || (answer2 == answer4)
                || (answer3 == answer4 && answer3 != "")
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
}