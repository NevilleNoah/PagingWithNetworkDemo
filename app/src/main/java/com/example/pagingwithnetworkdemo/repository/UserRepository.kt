package com.example.pagingwithnetworkdemo.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.pagingwithnetworkdemo.database.database.AppDatabase
import com.example.pagingwithnetworkdemo.network.NetworkService
import com.example.pagingwithnetworkdemo.repository.datasource.ItemKeyUserDataSource
import com.example.pagingwithnetworkdemo.repository.mediator.UserMediator

class UserRepository(private val database: AppDatabase, private val networkService: NetworkService) {

    // 持久化存储，运用remoteMediator将数据库和网络桥接起来，从网络将数据写入数据库
    fun getUsersInDb(query: String, pageSize: Int) = Pager(
        config = PagingConfig(pageSize),
        remoteMediator = UserMediator(
            database,
            networkService,
            query
        )
    ) {
        database.getUserDao().selectAll()
    }.flow

    // 非持久化存储，直接从网络写入内存
    fun getUsersInMemory(query: String, pageSize: Int) = Pager(
        config = PagingConfig(pageSize)
    ) {
        ItemKeyUserDataSource(networkService, query)
    }.flow

}