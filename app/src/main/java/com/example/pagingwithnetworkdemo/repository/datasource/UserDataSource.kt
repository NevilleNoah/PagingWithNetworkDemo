package com.example.pagingwithnetworkdemo.repository.datasource

import androidx.paging.LoadType
import androidx.paging.Pager
import androidx.paging.PagingSource
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.pagingwithnetworkdemo.database.entity.User
import com.example.pagingwithnetworkdemo.network.NetworkService
import retrofit2.HttpException
import java.io.IOException

class UserDataSource(
    val networkService: NetworkService,
    val query: String
) : PagingSource<String, User>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, User> {
        return try {

            // 从网络拿数据
            val data = networkService.getData(
                query = query,
                after = if (params is LoadParams.Append) params.key else null,
                before = if (params is LoadParams.Prepend) params.key else null,
                limit = params.loadSize
            ).data

            // 构建PageSource作为数据源
            LoadResult.Page(
                data = data,
                prevKey = data.firstOrNull()?.id,
                nextKey = data.lastOrNull()?.id
            )

        } catch (e: IOException) {
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        }
    }

}