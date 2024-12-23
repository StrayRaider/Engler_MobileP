package com.example.engler.data
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.engler.data.dao.UserWordDao
import com.example.engler.data.dao.WordDao
import com.example.engler.data.entities.UserWord
import com.example.engler.data.entities.Word
@Database(entities = [Word::class,UserWord::class], version = 1, exportSchema = false)
abstract class MyAppDatabase : RoomDatabase() {
    abstract val wordDao: WordDao
    abstract val userWordDao: UserWordDao
    companion object {
        @Volatile
        private var INSTANCE: MyAppDatabase? = null
        fun getInstance(context: Context): MyAppDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        MyAppDatabase::class.java,
                        "myapp_database"
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}