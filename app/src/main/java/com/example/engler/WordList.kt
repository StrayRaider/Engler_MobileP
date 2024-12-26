package com.example.engler

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog

class WordList: AppCompatActivity() {
    private val wordList = mutableListOf("Apple", "Banana", "Cherry", "Date", "Elderberry","Apple", "Banana", "Cherry", "Date", "Elderberry")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.word_list)

        val listOfWords: ListView= findViewById<ListView>(R.id.lvWords)
        val btnAddWord: Button= findViewById<Button>(R.id.btnAddWord)
        val btnCamera: Button= findViewById<Button>(R.id.btnCamera)


        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, wordList)
        listOfWords.adapter = adapter

        // Set up the Add Word button
        btnAddWord.setOnClickListener {
            showAddWordDialog(adapter)
        }

        btnCamera.setOnClickListener{
            val intent = Intent(this, Camera::class.java)
            startActivity(intent)
        }

    }

    private fun showAddWordDialog(adapter: ArrayAdapter<String>) {
        // Yeni kelime için giriş alanları oluşturuluyor
        val inputEditText = EditText(this).apply {
            hint = "Kelimenin İngilizcesi"
        }
        val inputEditTextTranslation = EditText(this).apply {
            hint = "Kelimenin Türkçesi"
        }
        val inputEditTextDescription = EditText(this).apply {
            hint = "Kelimenin Açıklaması"
        }

        // Tüm EditText'leri bir LinearLayout içine ekliyoruz
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10) // Görsel düzenleme için padding
            addView(inputEditText)
            addView(inputEditTextTranslation)
            addView(inputEditTextDescription)
        }

        AlertDialog.Builder(this).apply {
            setTitle("Add a New Word")
            setMessage("Enter a new word to add to the list:")
            setView(layout) // Layout'u dialog'a ekliyoruz

            setPositiveButton("Add") { _, _ ->
                val newWord = inputEditText.text.toString().trim()
                val translation = inputEditTextTranslation.text.toString().trim()
                val description = inputEditTextDescription.text.toString().trim()

                if (newWord.isNotEmpty() && translation.isNotEmpty() && description.isNotEmpty()) {
                    val formattedWord = "$newWord - $translation ($description)"
                    wordList.add(formattedWord) // Kelimeyi listeye ekliyoruz
                    adapter.notifyDataSetChanged() // Listeyi güncelliyoruz
                } else {
                    Toast.makeText(this@WordList, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }

            setNegativeButton("Cancel", null)
        }.show()
    }

}
