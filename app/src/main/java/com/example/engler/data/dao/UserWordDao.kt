package com.example.engler.data.dao
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.engler.data.entities.UserWord

@Dao
interface UserWordDao {
    @Query("SELECT * FROM user_words WHERE user_word_id = :userId")
    fun getUserWords(userId: Int): LiveData<List<UserWord>>

    @Query("SELECT * FROM user_words WHERE user_word_id = :userWordId")
    fun getUserWordById(userWordId: Int): UserWord

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserWord(userWord: UserWord)

    @Update
    suspend fun updateUserWord(userWord: UserWord)

    @Delete
    suspend fun deleteUserWord(userWord: UserWord)
    fun getUserWords()
}