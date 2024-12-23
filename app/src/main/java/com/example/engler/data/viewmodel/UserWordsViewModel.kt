package com.example.engler.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.example.engler.data.dao.UserWordDao
import com.example.engler.data.entities.UserWord
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserWordsViewModel(private val dao: UserWordDao) : ViewModel() {
    // Properties for new word input
    private val _userId = MutableLiveData<Int>()
    var wordId: Int = 0
    var score: Int = 0
    var definition: String = ""

    // Set user ID from UI
    fun setUserId(id: Int) {
        _userId.value = id
    }

    // Get user's words - now updates when userId changes
    private val userWords: LiveData<List<UserWord>> = _userId.switchMap { userId ->
        dao.getUserWords(userId)
    }

    // Format user words for display
    val userWordsString: LiveData<String> = userWords.map { words ->
        formatUserWords(words)
    }

    // Add new user word
    fun addUserWord() {
        val currentUserId = _userId.value
        if (currentUserId != null) {
            viewModelScope.launch {
                val userWord = UserWord(
                    userId = currentUserId,
                    wordId = wordId,
                    score = score,
                    definition = definition
                    // addedAt will be set automatically by default value
                )
                dao.insertUserWord(userWord)
            }
        }
    }

    // Update existing user word
    fun updateUserWord(userWord: UserWord) {
        viewModelScope.launch {
            dao.updateUserWord(userWord)
        }
    }

    // Delete user word
    fun deleteUserWord(userWord: UserWord) {
        viewModelScope.launch {
            dao.deleteUserWord(userWord)
        }
    }

    // Helper function to format user words list
    private fun formatUserWords(words: List<UserWord>): String {
        return words.fold("") { str, item ->
            str + '\n' + formatUserWord(item)
        }
    }

    // Helper function to format single user word
    private fun formatUserWord(userWord: UserWord): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val formattedDate = dateFormat.format(Date(userWord.addedAt))

        return buildString {
            append("ID: ${userWord.userWordId}")
            append('\n')
            append("User ID: ${userWord.userId}")
            append('\n')
            append("Word ID: ${userWord.wordId}")
            append('\n')
            append("Score: ${userWord.score}")
            append('\n')
            append("Definition: ${userWord.definition}")
            append('\n')
            append("Added: $formattedDate")
            append('\n')
        }
    }
}