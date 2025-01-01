package com.example.engler.data.entities
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_words")
data class UserWord(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_word_id")
    var userWordId: Int = 0,

    @ColumnInfo(name = "user_id")
    var userId: Int = 0,

    @ColumnInfo(name = "word_id")
    var wordId: Int= 0,

    @ColumnInfo(name = "score")
    var score: Int = 0,

    @ColumnInfo(name = "definition")
    var definition: String = "",

    @ColumnInfo(name = "added_at")
    var addedAt: Long = System.currentTimeMillis()
)
