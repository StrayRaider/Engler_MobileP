package com.example.engler.data.dao
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.engler.data.entities.UserWord
import java.time.zone.ZoneOffsetTransitionRule.TimeDefinition

@Dao
interface UserWordDao {
    @Query("SELECT * FROM user_words WHERE user_id = :userId")
    fun getUserWords(userId: Int): LiveData<List<UserWord>>

    @Query("SELECT * FROM user_words WHERE user_word_id = :userWordId")
    fun getUserWordById(userWordId: Int): UserWord

    @Query("SELECT * FROM user_words WHERE user_id = :userId AND word_id= :wordId")
    fun getUserWordByUserIdAndWordId(userId: Int, wordId: Int): UserWord


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserWord(userWord: UserWord)

    @Query("UPDATE user_words SET definition = :newDescription WHERE user_word_id = :userWordId")
    fun updateDescription(userWordId: Int, newDescription: String)
    @Delete
    fun deleteUserWord(userWord: UserWord)
}