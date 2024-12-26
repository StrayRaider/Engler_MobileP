package com.example.engler.data.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.map
import com.example.engler.data.dao.WordDao
import com.example.engler.data.entities.Word
import kotlinx.coroutines.launch

class WordsViewModel(val dao:WordDao ): ViewModel() {
    var newWordEn = ""
    var newWordTr = ""
    private val words = dao.getAllWords()
    val wordsString = words.map { words ->
        formatWords(words)
    }

    fun addWord() {
        viewModelScope.launch {
            val word = Word()
            word.wordEn = newWordEn
            word.wordTr = newWordTr
            dao.insertWord(word)
        }
    }
    fun formatWords(words: List<Word>): String {
        return words.fold("") { str, item ->
            str + '\n' + formatWord(item)
        }
    }
    fun formatWord(word: Word): String {
        var str = "ID: ${word.wordId}"
        str += '\n' + "Word: ${word.wordEn}"
        str += '\n' + "Word TR: ${word.wordTr}" + '\n'
        str += '\n' + "Date: ${word.createdAt}" + '\n'
        return str
    }
}