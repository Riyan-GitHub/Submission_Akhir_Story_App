package com.example.submissionakhirstoryapp.view.pagging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.submissionakhirstoryapp.config.ApiService
import com.example.submissionakhirstoryapp.data.response.ListStoryItem

class StoriesPagging(private val apiService: ApiService, val token: String) : PagingSource<Int, ListStoryItem>() {
    private companion object {
        const val INITIAL_PAGE_INDEX = 1
        const val TAG = "StoriesPagging"
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            // Menentukan halaman yang akan dimuat (jika tidak ada, mulai dari halaman 1)
            val page = params.key ?: INITIAL_PAGE_INDEX
            // Memanggil API untuk mendapatkan data cerita
            val responseData = apiService.getStories(token, page, params.loadSize)

            // Mengembalikan hasil dalam bentuk LoadResult.Page
            LoadResult.Page(
                data = responseData.listStory,
                prevKey = if (page == INITIAL_PAGE_INDEX) null else page - 1,
                nextKey = if (responseData.listStory.isNullOrEmpty()) null else page + 1
            )
        } catch (exception: Exception) {
            // Menangani error jika API gagal
            Log.e(TAG, "Error paging: ${exception.localizedMessage}")
            return LoadResult.Error(exception)
        }
    }

    // Fungsi untuk menentukan kunci penyegaran ketika paging perlu di-refresh
    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            // Menentukan halaman penyegaran berdasarkan posisi anchor
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}