package com.example.uvlevel.data

import androidx.room.*

@Dao
interface UserDAO {
    @Query("SELECT * FROM users")
    fun getUsers(): List<User>

    @Insert
    fun insertUser(user: User) : Long

    @Delete
    fun deleteUser(user: User)

    @Update
    fun updateUser(user: User)
}