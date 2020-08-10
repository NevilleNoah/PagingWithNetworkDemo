package com.example.pagingwithnetworkdemo.repository.datasource

import androidx.paging.PagingSource
import com.example.pagingwithnetworkdemo.database.entity.User
import com.example.pagingwithnetworkdemo.network.NetworkService
import retrofit2.HttpException
import java.io.IOException

// PageKey，按页码加载。
// 适用于分页加载的场景。
class PageKeyUserDataSource(
    private val networkService: NetworkService,
    private val query: String
) : PagingSource<Int, User>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        return try {

            // 页码
            val page = params.key ?: 0

            // 从网络中获取应答
            val response = networkService.getDataByPage(
                // 后端接口查询数据库所需的信息
                query = query,
                // 如果是Append向后追加，就启用after，after是上一次请求的nextKey
                page = params.key,
                // 尺寸
                limit = params.loadSize
            )

            // 从应答中获取数据
            val data = response.data

            // 构建PageSource作为数据源
            // prevKey和nextKey用于下一次加载，根据Append和Prepend来决定加载使用哪一个
            LoadResult.Page(
                data = data,
                // 如果当前页码不为0，则赋值为前一页的页码
                prevKey = if (page == 0) null else page - 1,
                // 如果是最后一页，则下一页为空，否则赋值为下一页的页码
                nextKey = if (response.isFinal) null else page + 1
            )

        } catch (e: IOException) {
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        }
    }
}