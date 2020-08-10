package com.example.pagingwithnetworkdemo.repository.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.pagingwithnetworkdemo.database.database.AppDatabase
import com.example.pagingwithnetworkdemo.database.entity.User
import com.example.pagingwithnetworkdemo.network.NetworkService
import retrofit2.HttpException
import java.io.IOException

// 网络与数据库之间的中转站
@OptIn(ExperimentalPagingApi::class)
class UserMediator(
    private val database: AppDatabase,
    private val networkService: NetworkService,
    private val query: String
) : RemoteMediator<Int, User>() {

    private val userDao = database.getUserDao()

    override suspend fun load(loadType: LoadType, state: PagingState<Int, User>): MediatorResult {

        return try {

            // 根据加载类型进行不同的操作
            val loadKey = when (loadType) {
                // 刷新
                LoadType.REFRESH -> null
                // 向上加载
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true) // endOfPaginationReached判读分页是否到底了，即还有没有后续数据
                // 向下加载
                LoadType.APPEND -> {

                    val lastItem = state.lastItemOrNull()
                        ?: return MediatorResult.Success(endOfPaginationReached = true)

                    lastItem.id
                }
            }

            // 从网络获取应答
            val response = networkService.getDataByItem(
                query = query,
                after = loadKey,
                limit = when (loadType) {
                    LoadType.REFRESH -> state.config.initialLoadSize
                    else -> state.config.pageSize
                }
            )

            // 将数据写入数据库
            database.withTransaction {

                if (loadType == LoadType.REFRESH) {
                    userDao.clearAll()
                }

                userDao.insertAll(response.data)
            }

            // 根据后台给出的数据判断是否还有后续数据
            MediatorResult.Success(
                endOfPaginationReached = response.isFinal
            )

        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }


}