package com.example.engler

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog

class WordList: AppCompatActivity() {
    private val wordList = mutableListOf("Apple", "Banana", "Cherry", "Date", "Elderberry")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.word_list)

        val listOfWords: ListView= findViewById<ListView>(R.id.lvWords)
        val btnAddWord: Button= findViewById<Button>(R.id.btnAddWord)

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, wordList)
        listOfWords.adapter = adapter

        // Set up the Add Word button
        btnAddWord.setOnClickListener {
            showAddWordDialog(adapter)
        }
    }

    private fun showAddWordDialog(adapter: ArrayAdapter<String>) {
        val inputEditText = EditText(this) // Input field for the new word

        AlertDialog.Builder(this).apply {
            setTitle("Add a New Word")
            setMessage("Enter a new word to add to the list:")
            setView(inputEditText)
            setPositiveButton("Add") { _, _ ->
                val newWord = inputEditText.text.toString().trim()
                if (newWord.isNotEmpty()) {
                    wordList.add(newWord) // Add the new word to the list
                    adapter.notifyDataSetChanged() // Refresh the ListView
                } else {
                    Toast.makeText(this@WordList, "Word cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            setNegativeButton("Cancel", null)
        }.show()
    }
}
