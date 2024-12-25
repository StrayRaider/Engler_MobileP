package com.example.engler.data.entities
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class Word(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "word_id")
    var wordId: Int = 0,

    @ColumnInfo(name = "word_tr")
    var wordTr: String = "",

    @ColumnInfo(name = "word_en")
    var wordEn: String = "",

    @ColumnInfo(name = "created_at")
    var createdAt: Long = System.currentTimeMillis()
)