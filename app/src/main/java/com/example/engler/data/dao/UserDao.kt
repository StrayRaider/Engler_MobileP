package com.example.engler.data.dao
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.engler.data.entities.User

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAllUsers(): LiveData<List<User>>

    @Query("SELECT * FROM users WHERE user_id = :userId")
    fun getUserById(userId: Int): User?

    @Query("SELECT * FROM users WHERE username = :userName")
    fun getUserByUsername(userName: String): User?
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)

    @Query("UPDATE users SET jwtToken = :jwtToken WHERE username = :username AND email = :password")
    fun updateJwt(username: String, password: String, jwtToken: String)

    @Update
    fun updateUser(user: User)

}
