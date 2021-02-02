package com.example.uvlevel.data

interface UserHandler{
        fun userCreated(user: User)
        fun userUpdated(user: User)
    }