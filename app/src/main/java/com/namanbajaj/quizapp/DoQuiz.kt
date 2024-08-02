package com.namanbajaj.quizapp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.*


class DoQuiz : AppCompatActivity(), View.OnClickListener {
    private var progressBar: ProgressBar? = null
    private var progress: TextView? = null
    private var questionField: TextView? = null
    private var answer1: TextView? = null
    private var answer2: TextView? = null
    private var answer3: TextView? = null
    private var answer4: TextView? = null

    private var currentPosition: Int = 0
    private var questions: ArrayList<Question> = ArrayList()
    private var selectedOptionPosition: Int = -1
    private var n: Int = 0

    private var score: Int = 0

    private var submit : Button? = null

    private var goHome : Button? = null

    private var submitState: Boolean = false

    private var questionToReview: ArrayList<String> = ArrayList()

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_do_quiz)

        progressBar = findViewById(R.id.progressBar)
        progress = findViewById(R.id.progressText)
        questionField = findViewById(R.id.questionField)
        answer1 = findViewById(R.id.answer1)
        answer2 = findViewById(R.id.answer2)
        answer3 = findViewById(R.id.answer3)
        answer4 = findViewById(R.id.answer4)
        submit = findViewById(R.id.submitButton)
        goHome = findViewById(R.id.goHomeButton)

        answer1?.setOnClickListener(this)
        answer2?.setOnClickListener(this)
        answer3?.setOnClickListener(this)
        answer4?.setOnClickListener(this)
        submit?.setOnClickListener(this)

        goHome?.setOnClickListener {
            val intent = Intent(this, QuizList::class.java)
            startActivity(intent)
            finish()
        }

        val quizName = intent.getStringExtra("Quiz Name").toString()
        val db = DBHelper(this, null)
        var cursorQuestions = db.getQuestions(quizName)
        if(cursorQuestions != null) {
            while(cursorQuestions.moveToNext()) {
                val question = cursorQuestions.getString(cursorQuestions.getColumnIndex(DBHelper.QUESTION_COL))

                var curAnswers = ArrayList<String>()
                var rightAnswerIndex = -1
                var curIndex = 0

                var cursorAnswers = db.getAnswers(quizName, question)
                if(cursorAnswers != null) {
                    while(cursorAnswers.moveToNext()) {
                        val answer = cursorAnswers.getString(cursorAnswers.getColumnIndex(DBHelper.ANSWER_COL))
                        if(cursorAnswers.getString(cursorAnswers.getColumnIndex(DBHelper.RIGHT_ANSWER_COL)).toInt() == 1)
                            rightAnswerIndex = curIndex
                        else
                            curIndex++
                        curAnswers.add(answer)
                    }
                }
                cursorAnswers?.close()

                questions.add(Question(question, curAnswers, rightAnswerIndex))
            }
        }
        cursorQuestions?.close()

        println(questions)

        // Randomize array
        questions.shuffle();

        // Randomize answers
        for(i in 0 until questions.size) {
            val answers = questions[i].answers
            val rightAnswer = answers[questions[i].rightAnswerIndex]
            answers.shuffle()
            for(j in 0 until answers.size){
                if(answers[j] == rightAnswer){
                    questions[i].rightAnswerIndex = j
                }
            }
            questions[i].answers = answers
        }

        println(questions)

        n = questions.size
        progressBar?.max = n

        setQuestion()
    }

    private fun setQuestion() {
        questionField?.text = questions[currentPosition].question
        answer1?.text = questions[currentPosition].answers[0]
        answer2?.text = questions[currentPosition].answers[1]
        if(questions[currentPosition].answers.size > 2){
            answer3?.text = questions[currentPosition].answers[2]
            answer3?.visibility = View.VISIBLE
        }
        else
            answer3?.visibility = View.INVISIBLE
        if(questions[currentPosition].answers.size > 3){
            answer4?.text = questions[currentPosition].answers[3]
            answer4?.visibility = View.VISIBLE
        }
        else
            answer4?.visibility = View.INVISIBLE
        progress?.text = (currentPosition+1).toString() + "/" + n.toString()
        progressBar?.progress = currentPosition + 1
    }

    private fun setDefaultOptionsView() {
        val answerList = ArrayList<TextView>()
        answerList.add(answer1!!)
        answerList.add(answer2!!)
        answerList.add(answer3!!)
        answerList.add(answer4!!)
        for(i in 0 until answerList.size) {
//            answerList[i].visibility = View.VISIBLE
            answerList[i].setTextColor(Color.parseColor("#7A8089"))
            answerList[i].typeface = Typeface.DEFAULT
            answerList[i].background = ContextCompat.getDrawable(this, R.drawable.default_option_border_bg)
        }
    }

    private fun selectedOptionView(t: TextView, selectedOptionPosition: Int) {
        setDefaultOptionsView()
        this.selectedOptionPosition = selectedOptionPosition

        t.setTextColor(Color.parseColor("#000000"))
        t.setTypeface(t.typeface, Typeface.BOLD)
        t.background = ContextCompat.getDrawable(this, R.drawable.selected_option_border_bg)
    }

    override fun onClick(v: View?) {
        v!!.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        when(v?.id) {
            R.id.answer1 -> {
                selectedOptionView(answer1!!, 0)
            }
            R.id.answer2 -> {
                selectedOptionView(answer2!!, 1)
            }
            R.id.answer3 -> {
                selectedOptionView(answer3!!, 2)
            }
            R.id.answer4 -> {
                selectedOptionView(answer4!!, 3)
            }

            R.id.submitButton -> {
                if(submitState) {
                    selectedOptionPosition = -1

                    submit?.text = "Submit"
                    submitState = false

                    if(currentPosition < n) {
                        setQuestion()
                        setDefaultOptionsView()
                    } else {
                        val intent = Intent(this, FinalScore::class.java)
                        intent.putExtra("name", intent.getStringExtra("Quiz Name"))
                        intent.putExtra("score", score.toString())
                        intent.putExtra("size", n.toString())
                        intent.putStringArrayListExtra("Questions", questionToReview)
                        startActivity(intent)
                        finish()
                    }
                }

                else {
                    if(selectedOptionPosition == -1) {
                        Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show()
                        v!!.performHapticFeedback(HapticFeedbackConstants.REJECT)
                    } else {
                        if(questions[currentPosition].rightAnswerIndex == selectedOptionPosition) {
                            answerView(selectedOptionPosition, R.drawable.correct_option_border_bg)
                            v!!.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                            score++
                        } else {
                            answerView(selectedOptionPosition, R.drawable.wrong_option_border_bg)
                            v!!.performHapticFeedback(HapticFeedbackConstants.REJECT)
                            questionToReview.add(questions[currentPosition].question)
                        }
                        currentPosition++

                        submit?.text = "Next"
                        submitState = true
                    }
                }
            }
        }
    }

    private fun answerView(answer: Int, drawableView: Int) {
        when(answer) {
            0 -> {
                answer1?.background = ContextCompat.getDrawable(this, drawableView)
            }
            1 -> {
                answer2?.background = ContextCompat.getDrawable(this, drawableView)
            }
            2 -> {
                answer3?.background = ContextCompat.getDrawable(this, drawableView)
            }
            3 -> {
                answer4?.background = ContextCompat.getDrawable(this, drawableView)
            }
        }
    }

}