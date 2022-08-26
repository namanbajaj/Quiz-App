package com.namanbajaj.quizapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + NAME_COl + " TEXT, "
                + QUESTION_COL + " TEXT, "
                + ANSWER_COL + " TEXT, "
                + RIGHT_ANSWER_COL + " INTEGER, PRIMARY KEY ("
                + NAME_COl + "," + QUESTION_COL + "," + ANSWER_COL +
                "))")

        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    fun addQuiz(name : String, question : String, answers: ArrayList<String>, right_answer: Int ){
        val db = this.writableDatabase

        val values = ContentValues()
        for(answer in answers){
            values.put(NAME_COl, name)
            values.put(QUESTION_COL, question)
            values.put(ANSWER_COL, answer)
            if(answers.indexOf(answer) == right_answer)
                values.put(RIGHT_ANSWER_COL, 1)
            else
                values.put(RIGHT_ANSWER_COL, 0)

            db.insert(TABLE_NAME, null, values)
            values.clear()
        }
        db.close()
    }

    fun getQuizNames(): Cursor? {
        val db = this.readableDatabase
        val query = "SELECT DISTINCT $NAME_COl FROM $TABLE_NAME"
        return db.rawQuery(query, null)
    }

    fun getQuestions(quizName: String): Cursor? {
        val db = this.readableDatabase
        val query = "SELECT DISTINCT $QUESTION_COL FROM $TABLE_NAME WHERE $NAME_COl=\'$quizName\'"
        return db.rawQuery(query, null)
    }

    fun getAnswers(quizName: String, questionName: String): Cursor? {
        val db = this.readableDatabase
        val query = "SELECT $ANSWER_COL, $RIGHT_ANSWER_COL FROM $TABLE_NAME WHERE $NAME_COl = \'$quizName\' AND $QUESTION_COL = \'$questionName\'"
        return db.rawQuery(query, null)
    }

    fun deleteQuiz(quizName: String) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$NAME_COl = \'$quizName\'", null)
    }

    fun deleteQuestion(quizName: String, question: String) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$NAME_COl = \'$quizName\' AND $QUESTION_COL = \'$question\'", null)
    }

    companion object{
        // database name
        private val DATABASE_NAME = "QUIZZES"

        // database version
        private val DATABASE_VERSION = 1

        // table name
        val TABLE_NAME = "quizzes"

        // quiz name column
        val NAME_COl = "name"

        // question name column
        val QUESTION_COL = "question_name"

        // answer name column
        val ANSWER_COL = "answer_choice"

        // right answer column
        val RIGHT_ANSWER_COL = "right_answer"

    }
}