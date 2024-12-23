package com.example.engler.data.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.engler.data.dao.UserWordDao
import com.example.engler.data.viewmodel.UserWordsViewModel

class UserWordsViewModelFactory(private val dao: UserWordDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserWordsViewModel::class.java)) {
            return UserWordsViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}