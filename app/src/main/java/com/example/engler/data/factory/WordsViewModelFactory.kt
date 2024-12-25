package com.example.engler.data.factory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.engler.dao.WordDao
import com.example.engler.data.viewmodel.WordsViewModel

class WordsViewModelFactory(private val dao: WordDao)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WordsViewModel::class.java)) {
            return WordsViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}