package com.example.uvlevel.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) var userId : Long?,
    @ColumnInfo(name = "skinColor") var skinColor: String,
    @ColumnInfo(name = "eyesColor") var eyesColor: String,
    @ColumnInfo(name = "hairColor") var hairColor: String,
    @ColumnInfo(name = "freckles") var freckles: Boolean,
    @ColumnInfo(name = "gender") var gender: String
) : Serializable