package com.example.engler

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.engler.data.MyAppDatabase
import kotlinx.coroutines.launch

class Quiz : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.quiz)

        // Start the first question
        loadNewQuestion()
    }

    private fun loadNewQuestion() {
        val db = MyAppDatabase.getInstance(applicationContext)
        val userWordDao = db.userWordDao
        val wordDao = db.wordDao

        // Observe LiveData for user words
        userWordDao.getUserWords(3).observe(this, Observer { userWords ->
            // Check if the list is null or empty
            if (userWords.isNullOrEmpty()) {
                showMessage("No words found for the user.")
                return@Observer
            }

            val randomUserWord = userWords.random()
            val correctAnswer = wordDao.getWordById(randomUserWord.wordId)?.wordEn
            showMessage("${correctAnswer}")

            if (correctAnswer == null) {
                showMessage("Word not found in database. ${correctAnswer}")
                return@Observer
            }

            // Observe allWords LiveData
            wordDao.getAllWords().observe(this, Observer { allWords ->
                // Check if allWords is null or empty
                if (allWords.isNullOrEmpty()) {
                    showMessage("No words found in the database.")
                    return@Observer
                }

                // Filter out wrong answers and shuffle them
                val wrongAnswers = allWords.filter { it.wordId != randomUserWord.wordId }
                    .shuffled()
                    .take(3)
                    .mapNotNull { it.wordEn }

                if (wrongAnswers.size < 3) {
                    showMessage("Not enough incorrect words to create the quiz.")
                    return@Observer
                }

                // Combine wrong answers with correct answer and shuffle
                val allAnswers = wrongAnswers + correctAnswer
                val shuffledAnswers = allAnswers.shuffled()

                // Show the quiz dialog with question and answers
                showQuizDialog(wordDao.getWordById(randomUserWord.wordId).wordTr ?: "Translation not available", shuffledAnswers, correctAnswer)
            })
        })
    }

    private fun showQuizDialog(question: String, answers: List<String>, correctAnswer: String) {
        val tvQuestion = findViewById<TextView>(R.id.tvQuestion)
        val btnOption1 = findViewById<Button>(R.id.btnOption1)
        val btnOption2 = findViewById<Button>(R.id.btnOption2)
        val btnOption3 = findViewById<Button>(R.id.btnOption3)
        val btnOption4 = findViewById<Button>(R.id.btnOption4)

        tvQuestion.text = "Translate: '$question'"

        // Assign answers to buttons
        btnOption1.text = answers[0]
        btnOption2.text = answers[1]
        btnOption3.text = answers[2]
        btnOption4.text = answers[3]

        // Button click listeners
        btnOption1.setOnClickListener { checkAnswer(answers[0], correctAnswer) }
        btnOption2.setOnClickListener { checkAnswer(answers[1], correctAnswer) }
        btnOption3.setOnClickListener { checkAnswer(answers[2], correctAnswer) }
        btnOption4.setOnClickListener { checkAnswer(answers[3], correctAnswer) }
    }

    private fun checkAnswer(selectedAnswer: String, correctAnswer: String) {
        if (selectedAnswer == correctAnswer) {
            showMessage("Correct! Well done.")
        } else {
            showMessage("Incorrect! The correct answer is '$correctAnswer'.")
        }

        // After answering, load a new question
        loadNewQuestion()
    }

    // Show message as a Toast
    private fun showMessage(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }
}
