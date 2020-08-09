package com.example.pagingwithnetworkdemo.network

import com.example.pagingwithnetworkdemo.database.entity.User
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

class NetworkService {

    private val response = Response(listOf(User("1","Name")), 10)

    suspend fun getData(
        query: String,
        before: String? = null,
        after: String? = null,
        limit: Int
    ): Response {
        return response
    }


    data class Response(val data: List<User>, val nextKey: Int)

}