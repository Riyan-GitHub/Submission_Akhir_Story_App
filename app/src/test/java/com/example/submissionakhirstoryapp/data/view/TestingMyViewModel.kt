package com.example.submissionakhirstoryapp.data.view

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.submissionakhirstoryapp.DispatcherRule
import com.example.submissionakhirstoryapp.data.Dummy
import com.example.submissionakhirstoryapp.data.adapter.StoriesAdapter
import com.example.submissionakhirstoryapp.data.getOrAwaitValue
import com.example.submissionakhirstoryapp.data.pref.UserRepository
import com.example.submissionakhirstoryapp.data.response.ListStoryItem
import com.example.submissionakhirstoryapp.view.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class TestingMyViewModel {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = DispatcherRule()

    @Mock
    private lateinit var storyRepository: UserRepository

    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {
        val dummyStory = Dummy.generateDummyStoryResponse() // Mendapatkan data cerita dummy
        val data: PagingData<ListStoryItem> = StoryPagingSource.snapshot(dummyStory) // Membuat PagingData dari dummy data
        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStory.value = data
        Mockito.`when`(storyRepository.getStories("token")).thenReturn(expectedStory) // Mocking method repository

        val mainViewModel = MainViewModel(storyRepository)
        val actualStory: PagingData<ListStoryItem> = mainViewModel.getStories("token").getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoriesAdapter.DIFF_CALLBACK, // Adapter callback untuk membandingkan data
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStory.size, differ.snapshot().size)
        Assert.assertEquals(dummyStory[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val data: PagingData<ListStoryItem> = PagingData.from(emptyList())
        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStory.value = data
        Mockito.`when`(storyRepository.getStories("token")).thenReturn(expectedStory) // Mocking method repository

        val mainViewModel = MainViewModel(storyRepository)
        val actualStory: PagingData<ListStoryItem> = mainViewModel.getStories("token").getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoriesAdapter.DIFF_CALLBACK, // Adapter callback untuk membandingkan data
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        Assert.assertEquals(0, differ.snapshot().size) // Memastikan data kosong
    }
}

class StoryPagingSource : PagingSource<Int, LiveData<List<ListStoryItem>>>() {
    companion object {
        fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
            return PagingData.from(items) // Mengubah list menjadi PagingData
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryItem>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStoryItem>>> {
        return LoadResult.Page(emptyList(), 0, 1)  // Mengembalikan data kosong
    }
}

// No-op callback untuk update list pada differ
val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}