package com.example.pagingwithnetworkdemo.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pagingwithnetworkdemo.database.entity.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<User>)

    @Query("SELECT * FROM users")
    fun selectAll(): PagingSource<Int, User>

    @Query("DELETE FROM users")
    suspend fun clearAll()
}