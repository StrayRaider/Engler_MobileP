package com.example.engler.data.dao
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.engler.data.entities.Word
@Dao
interface WordDao {
    @Query("SELECT * FROM words")
    fun getAllWords(): LiveData<List<Word>>

    @Query("SELECT * FROM words WHERE word_id = :wordId")
    fun getWordById(wordId: Int): Word

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWord(word: Word):Long

    @Update
    fun updateWord(word: Word)

    @Query("SELECT * FROM words WHERE word_en = :wordEn AND word_tr = :wordTr LIMIT 1")
    fun getWordByEnAndTr(wordEn: String, wordTr: String): Word


    @Query("UPDATE words SET word_tr = :newTr, word_en= :newEn WHERE word_id = :wordId")
    fun updateTrEn(wordId: Int, newTr: String, newEn:String)
    @Delete
    fun deleteWord(word: Word)
}