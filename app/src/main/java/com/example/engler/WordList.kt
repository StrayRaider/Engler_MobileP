package com.example.engler

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.engler.data.MyAppDatabase
import com.example.engler.data.entities.UserWord
import com.example.engler.data.entities.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import android.util.Log


class WordList : AppCompatActivity() {
    private val wordList = mutableListOf<String>()
    private lateinit var adapter: WordListAdapter
    private var checkdetectedText = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.word_list)

        val listOfWords: ListView = findViewById(R.id.lvWords)
        val btnAddWord: Button = findViewById(R.id.btnAddWord)
        val btnCamera: Button = findViewById(R.id.btnCamera)
        val btnQuiz: Button = findViewById(R.id.btnQuiz)
        val detectedText = intent.getStringExtra("detectedText") ?: ""
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnChatBot: Button = findViewById(R.id.btnChatbot)


        // Set up the adapter
        adapter = WordListAdapter(
            this, wordList,
            onEditClick = { word -> showEditWordDialog(word) },
            onDeleteClick = { word -> deleteWord(word) }
        )
        listOfWords.adapter = adapter

        val db = MyAppDatabase.getInstance(applicationContext)
        val userWordDao = db.userWordDao
        val wordDao = db.wordDao

        // Observe the user's words and update the list
        userWordDao.getUserWords(3).observe(this) { userWordList ->
            lifecycleScope.launch(Dispatchers.IO) {
                val newList = mutableListOf<String>()
                userWordList?.forEach { userWord ->
                    val word = wordDao.getWordById(userWord.wordId)
                    word?.let {
                        newList.add("${it.wordEn} + ${it.wordTr} (${userWord.definition})")
                    }
                }

                withContext(Dispatchers.Main) {
                    if (newList.isEmpty()) {
                        showMessage("No words found.")
                    } else {
                        wordList.clear()
                        wordList.addAll(newList)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }

        // If detected text is passed, show the Add Word dialog
        if (detectedText.isNotEmpty()) {
            showAddWordDialog(adapter, detectedText)
        }

        // Button actions
        btnAddWord.setOnClickListener { showAddWordDialog(adapter, detectedText) }
        btnCamera.setOnClickListener { startActivity(Intent(this, Camera::class.java)) }
        btnQuiz.setOnClickListener { startActivity(Intent(this, Quiz::class.java)) }
        btnChatBot.setOnClickListener { startActivity(Intent(this, AiCaller::class.java)) }
        btnBack.setOnClickListener { onBackPressed() }
    }

    private fun showAddWordDialog(adapter: WordListAdapter, detectedText: String) {
        val inputEditText = EditText(this).apply { hint = "Enter English word" }
        val inputEditTextTranslation = EditText(this).apply { hint = "Enter translation" }
        val inputEditTextDescription = EditText(this).apply { hint = "Enter description" }

        // Translate Button
        val translateButton = Button(this).apply {
            text = "Translate"
            setOnClickListener {
                val wordToTranslate = inputEditText.text.toString().trim()
                if (wordToTranslate.isNotEmpty()) {
                    fetchTranslation(wordToTranslate) { translation ->
                        inputEditTextTranslation.setText(translation)
                    }
                } else {
                    showMessage("Please enter a word to translate.")
                }
            }
        }

        // Create a layout to hold the inputs
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
            addView(inputEditText)
            addView(inputEditTextTranslation)
            addView(inputEditTextDescription)
            addView(translateButton)
        }

        // Show dialog
        AlertDialog.Builder(this).apply {
            setTitle("Add a New Word")
            setView(layout)
            setPositiveButton("Add") { _, _ ->
                val newWord = inputEditText.text.toString().trim()
                val translation = inputEditTextTranslation.text.toString().trim()
                val description = inputEditTextDescription.text.toString().trim()

                if (newWord.isNotEmpty() && translation.isNotEmpty() && description.isNotEmpty()) {
                    val word = Word(0, newWord, translation, System.currentTimeMillis())
                    lifecycleScope.launch {
                        val db = MyAppDatabase.getInstance(applicationContext)
                        val wordDao = db.wordDao
                        val insertedWordId = wordDao.insertWord(word).toInt()

                        val userWord = UserWord(
                            0, 3, insertedWordId, 0, description, System.currentTimeMillis()
                        )
                        val userWordDao = db.userWordDao
                        userWordDao.insertUserWord(userWord)

                        wordList.add(newWord)
                        adapter.notifyDataSetChanged()
                        showMessage("Word added successfully!")
                    }
                } else {
                    showMessage("All fields must be filled out.")
                }
            }
            setNegativeButton("Cancel", null)
        }.show()
    }

    private fun fetchTranslation(word: String, callback: (String) -> Unit) {
        val url = "https://translation.googleapis.com/language/translate/v2?key=YOUR_API_KEY"
        val jsonBody = JSONObject().apply {
            put("q", word)
            put("target", "tr")
        }

        val requestBody = RequestBody.create("application/json".toMediaType(), jsonBody.toString())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .build()

        val client = OkHttpClient()

        lifecycleScope.launch {
            val response = withContext(Dispatchers.IO) { client.newCall(request).execute() }
            if (response.isSuccessful) {
                val responseBody = response.body?.string() ?: ""
                val jsonResponse = JSONObject(responseBody)
                val translation = jsonResponse.getJSONObject("data")
                    .getJSONArray("translations")
                    .getJSONObject(0)
                    .getString("translatedText")
                callback(translation)
            } else {
                showMessage("Failed to fetch translation.")
            }
        }
    }

    private fun showEditWordDialog(word: String) {
        val (wordEn, wordTr, description) = parseWordDetails(word)
        val inputEditText = EditText(this).apply { setText(wordEn) }
        val inputEditTextTranslation = EditText(this).apply { setText(wordTr) }
        val inputEditTextDescription = EditText(this).apply { setText(description) }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
            addView(inputEditText)
            addView(inputEditTextTranslation)
            addView(inputEditTextDescription)
        }

        var db = MyAppDatabase.getInstance(applicationContext)

        val wordDao = db.wordDao
        val userWordDao=db.userWordDao


        val dialog = AlertDialog.Builder(this)

            .setTitle("Edit Word")
            .setView(layout)
            .setMessage("Do you want to edit the word: $word?")
            .setPositiveButton("Edit") { _, _ ->
                val newWord = inputEditText.text.toString().trim()
                val translation = inputEditTextTranslation.text.toString().trim()
                val description = inputEditTextDescription.text.toString().trim()

                lifecycleScope.launch (Dispatchers.IO){
                    val word=wordDao.getWordByEnAndTr(wordEn,wordTr)
                    val wordId=word.wordId

                    Log.d("test", "${translation}+ ${newWord}")


                    //WORD TR -EN UPDATE
                    wordDao.updateTrEn(wordId,translation,newWord)

                    val userWord=userWordDao.getUserWordByUserIdAndWordId(3,wordId)

                    //DESCRIPTION UPDATE
                    val userWordId= userWord.userWordId
                    userWordDao.updateDescription(userWordId,description)

                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss() // Dialog kapatılır
            }
            .create()

        dialog.show()

    }



    private fun deleteWord(word: String) {

        val (wordEn, wordTr, description) = parseWordDetails(word)

        var db = MyAppDatabase.getInstance(applicationContext)
        val wordDao = db.wordDao
        val userWordDao=db.userWordDao

        lifecycleScope.launch (Dispatchers.IO){


            val word=wordDao.getWordByEnAndTr(wordEn,wordTr)
            val wordId=word.wordId
            wordDao.deleteWord(word)

            //userword Delete

            val userWord=userWordDao.getUserWordByUserIdAndWordId(3,wordId)
            userWordDao.deleteUserWord(userWord)

        }


    }

    fun parseWordDetails(input: String): Triple<String, String, String> {
        val regex = """(.+?)\s*\+\s*(.+?)\s*\((.+)\)""".toRegex()
        val matchResult = regex.matchEntire(input)

        return if (matchResult != null) {
            val (wordEn, wordTr, description) = matchResult.destructured
            Triple(wordEn.trim(), wordTr.trim(), description.trim())
        } else {
            Triple("", "", "") // Uyumlu değilse boş değer döner
        }


    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
