package com.example.pagingwithnetworkdemo.database.entity

import androidx.room.Entity

@Entity(tableName = "users")
data class User (
    val id: String,
    val label: String
)