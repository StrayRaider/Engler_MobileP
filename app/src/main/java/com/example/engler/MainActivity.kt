package com.example.engler

import com.example.engler.data.viewmodel.WordsViewModel


import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.engler.data.MyAppDatabase
import com.example.engler.data.factory.WordsViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var englishWordInput: EditText
    private lateinit var translationInput: EditText
    private lateinit var addButton: Button
    private lateinit var wordsList: TextView

    private val viewModel: WordsViewModel by viewModels {
        WordsViewModelFactory(MyAppDatabase.getInstance(applicationContext).wordDao)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI components
        initializeViews()

        // Set up observers
        setupObservers()

        // Set up click listeners
        setupClickListeners()
    }

    private fun initializeViews() {
        englishWordInput = findViewById(R.id.editTextEnglishWord)
        translationInput = findViewById(R.id.editTextTranslation)
        addButton = findViewById(R.id.buttonAdd)
        wordsList = findViewById(R.id.textViewWords)
    }

    private fun setupObservers() {
        viewModel.wordsString.observe(this, Observer { words ->
            wordsList.text = words
        })
    }

    private fun setupClickListeners() {
        addButton.setOnClickListener {
            if (validateInputs()) {
                viewModel.apply {
                    newWordEn = englishWordInput.text.toString()
                    newWordTr = translationInput.text.toString()
                    addWord()
                }
                clearInputs()
            }
        }
    }

    private fun validateInputs(): Boolean {
        val englishWord = englishWordInput.text.toString()
        val translation = translationInput.text.toString()

        if (englishWord.isEmpty()) {
            englishWordInput.error = "Please enter an English word"
            return false
        }

        if (translation.isEmpty()) {
            translationInput.error = "Please enter a translation"
            return false
        }

        return true
    }

    private fun clearInputs() {
        englishWordInput.text.clear()
        translationInput.text.clear()
        englishWordInput.requestFocus()
    }
}