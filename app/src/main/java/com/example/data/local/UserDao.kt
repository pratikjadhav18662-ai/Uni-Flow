package com.example.data.local

import androidx.room.*
import com.example.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:email) AND passwordHash = :password LIMIT 1")
    suspend fun login(email: String, password: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Query("UPDATE users SET passwordHash = :newPassword WHERE LOWER(email) = LOWER(:email)")
    suspend fun updatePassword(email: String, newPassword: String): Int

    @Query("SELECT * FROM users LIMIT 1")
    fun getFirstUser(): Flow<User?>
}
