package com.example.pagingwithnetworkdemo.repository.datasource

import androidx.paging.PagingSource
import com.example.pagingwithnetworkdemo.database.entity.User
import com.example.pagingwithnetworkdemo.network.NetworkService
import retrofit2.HttpException
import java.io.IOException

// ItemKey，使用条目中的关键信息进行关联查询。
// 适合一些上下有关联的，例如有序的id。
class ItemKeyUserDataSource(
    private val networkService: NetworkService,
    private val query: String
) : PagingSource<String, User>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, User> {
        return try {

            // 从网络中获取应答
            val response = networkService.getDataByItem(
                // 后端接口查询数据库所需的信息
                query = query,
                // 如果是Append向后追加，就启用after，after是上一次请求的nextKey
                after = if (params is LoadParams.Append) params.key else null,
                // 如果是Prepend向前追加，就启用before，before是上一次请求的prevKey
                before = if (params is LoadParams.Prepend) params.key else null,
                // 尺寸
                limit = params.loadSize
            )

            // 从应答中获取数据
            val data = response.data

            // 构建PageSource作为数据源
            // prevKey和nextKey用于下一次加载，根据Append和Prepend来决定加载使用哪一个
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