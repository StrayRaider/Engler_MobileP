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
import androidx.lifecycle.lifecycleScope
import com.example.engler.data.MyAppDatabase
import com.example.engler.data.entities.UserWord
import com.example.engler.data.entities.Word
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log
import android.widget.ImageButton


class WordList : AppCompatActivity() {
    private val wordList = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String> // Initialize adapter here
    private var checkdetectedText: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.word_list)

        val listOfWords: ListView = findViewById(R.id.lvWords)
        val btnAddWord: Button = findViewById(R.id.btnAddWord)
        val btnCamera: Button = findViewById(R.id.btnCamera)
        val btnQuiz: Button = findViewById(R.id.btnQuiz)
        val detectedText = intent.getStringExtra("detectedText") ?: ""
        val btnBack= findViewById<ImageButton>(R.id.btnBack)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, wordList) // Initialize
        listOfWords.adapter = adapter

        val db = MyAppDatabase.getInstance(applicationContext)
        val userWordDao = db.userWordDao
        val wordDao = db.wordDao

        userWordDao.getUserWords(3).observe(this) { userWordList ->
            lifecycleScope.launch(Dispatchers.IO) { // Run the whole process in IO
                val newList = mutableListOf<String>()


                userWordList?.forEach { userWord ->

                    val word = wordDao.getWordById(userWord.wordId)
                    word?.let {
                        newList.add("${it.wordEn} + ${userWord.definition}")
                    }
                }

                withContext(Dispatchers.Main) {
                    if (newList.isEmpty()) {
                        showMessage("No words found in userWords")
                        Log.d("WordListActivity", "No words found in userWords")
                        // Handle empty list case (e.g., show a message)
                    } else {
                        wordList.clear()
                        wordList.addAll(newList)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }


        if (detectedText != "") {
            detectedText.let {
                checkdetectedText = true
                showAddWordDialog(adapter, detectedText)
            }
        }

        // Set up the Add Word button
        btnAddWord.setOnClickListener {
            checkdetectedText = false
            showAddWordDialog(adapter, detectedText)
        }

        btnCamera.setOnClickListener {
            val intent = Intent(this, Camera::class.java)
            startActivity(intent)
        }

        btnQuiz.setOnClickListener {
            val intent = Intent(this, Quiz::class.java)
            startActivity(intent)
        }

        btnBack.setOnClickListener {
            onBackPressed()
        }

    }

    private fun showAddWordDialog(adapter: ArrayAdapter<String>, detectedText: String) {
        // Yeni kelime için giriş alanları oluşturuluyor
        val inputEditText = EditText(this).apply {
            hint = "Kelimenin İngilizcesi"
            if (checkdetectedText) { setText(detectedText) }
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

                val word = Word(
                    wordId = 0,  // Başlangıçta ID 0
                    wordTr = translation,
                    wordEn = newWord,
                    createdAt = System.currentTimeMillis()
                )

                val db = MyAppDatabase.getInstance(applicationContext)
                val wordDao = db.wordDao

                lifecycleScope.launch {
                    // Kelimeyi veritabanına ekleyin ve ID'yi alın
                    val insertedWordId = wordDao.insertWord(word)

                    // Burada, insert işleminden sonra dönen ID'yi kullanabilirsiniz
                    Log.d("WordListActivity", "The wordId is: $insertedWordId")

                    // Yeni kelimeyi ve açıklamayı 'user_words' tablosuna ekleyin
                    val userWord = UserWord(
                        userWordId = 0,
                        userId = 3,  // currentUser.id'yi burada kullanıyoruz
                        wordId = insertedWordId.toInt(),  // ID'yi kullanın
                        score = 0,
                        definition = description,
                        addedAt = System.currentTimeMillis()
                    )

                    val userWordDao = db.userWordDao
                    userWordDao.insertUserWord(userWord)  // Kullanıcı açıklamasını veritabanına ekleyin

                    wordList.add(newWord)
                    adapter.notifyDataSetChanged()

                    showMessage("Word and definition added successfully!")
                }

            }
            setNegativeButton("Cancel", null)
        }.show()
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
