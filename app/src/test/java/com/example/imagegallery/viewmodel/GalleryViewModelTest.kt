package com.example.imagegallery.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.imagegallery.data.repository.PhotoDataRepository
import com.example.imagegallery.data.model.Photos
import com.example.imagegallery.data.model.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.anyString
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner.Silent::class)
class GalleryViewModelTest {

    @get:Rule
    val testInstantTaskExecutorRule = InstantTaskExecutorRule()

    private val testCoroutineDispatcher = TestCoroutineDispatcher()

    @Mock
    lateinit var mockPhotoDataRepository: PhotoDataRepository

    @Mock
    lateinit var observer: Observer<Resource<Photos>>

    private val photosFirst = Photos(1, 3, 50, 1000, emptyList())
    private val photosSecond = Photos(2, 3, 50, 1000, emptyList())
    private val photosThird = Photos(3, 3, 50, 1000, emptyList())

    @Before
    fun setUp() {
        Dispatchers.setMain(testCoroutineDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testCoroutineDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun testSearchPhotos() = testCoroutineDispatcher.runBlockingTest {
        `when`(mockPhotoDataRepository.getPhotos(anyString(), anyInt())).thenReturn(photosFirst)
        val subject = GalleryViewModel(mockPhotoDataRepository, testCoroutineDispatcher)
        subject.photoList.observeForever(observer)
        subject.searchPhotos(queryString)
        verify(mockPhotoDataRepository, times(1)).getPhotos(queryString, 1)
        assertTrue(subject.photoList.hasObservers())
        verify(observer).onChanged(Resource.success(photosFirst))
        Assert.assertNotNull(subject.photoList.value)
        subject.photoList.removeObserver(observer)
    }

    @Test
    fun testSearchNullPhotos() = testCoroutineDispatcher.runBlockingTest {
        `when`(mockPhotoDataRepository.getPhotos(anyString(), anyInt())).thenReturn(null)
        val subject = GalleryViewModel(mockPhotoDataRepository, testCoroutineDispatcher)
        subject.photoList.observeForever(observer)

        subject.searchPhotos(queryString)

        assertTrue(subject.photoList.hasObservers())
        verify(observer).onChanged(
            Resource.error(
                "Error while loading photos.\n\nPlease try again!",
                null
            )
        )
        subject.photoList.removeObserver(observer)
    }

    @Test
    fun testSearchPhotosPageIncrements() = testCoroutineDispatcher.runBlockingTest {
        `when`(mockPhotoDataRepository.getPhotos(anyString(), anyInt())).thenReturn(photosFirst)
        val subject = GalleryViewModel(mockPhotoDataRepository, testCoroutineDispatcher)
        subject.photoList.observeForever(observer)

        subject.searchPhotos(queryString)
        verify(mockPhotoDataRepository, times(1)).getPhotos(queryString, 1)

        `when`(mockPhotoDataRepository.getPhotos(anyString(), anyInt())).thenReturn(photosSecond)
        subject.searchPhotos(queryString)
        verify(mockPhotoDataRepository, times(1)).getPhotos(queryString, 2)

        `when`(mockPhotoDataRepository.getPhotos(anyString(), anyInt())).thenReturn(photosThird)
        subject.searchPhotos(queryString)
        verify(mockPhotoDataRepository, times(1)).getPhotos(queryString, 3)

        assertTrue(subject.photoList.hasObservers())
        verify(observer).onChanged(Resource.success(photosFirst))
        Assert.assertNotNull(subject.photoList.value)

        subject.photoList.removeObserver(observer)
    }

    @Test
    fun testSearchPhotosPageIncrementsToMaxNumberAndStopCallingApi() =
        testCoroutineDispatcher.runBlockingTest {
            `when`(mockPhotoDataRepository.getPhotos(anyString(), anyInt())).thenReturn(photosFirst)
            val subject = GalleryViewModel(mockPhotoDataRepository, testCoroutineDispatcher)
            subject.photoList.observeForever(observer)

            subject.searchPhotos(queryString)
            verify(mockPhotoDataRepository, times(1)).getPhotos(queryString, 1)

            `when`(
                mockPhotoDataRepository.getPhotos(
                    anyString(),
                    anyInt()
                )
            ).thenReturn(photosSecond)
            subject.searchPhotos(queryString)
            verify(mockPhotoDataRepository, times(1)).getPhotos(queryString, 2)

            `when`(mockPhotoDataRepository.getPhotos(anyString(), anyInt())).thenReturn(photosThird)
            subject.searchPhotos(queryString)
            verify(mockPhotoDataRepository, times(1)).getPhotos(queryString, 3)

            `when`(mockPhotoDataRepository.getPhotos(anyString(), anyInt())).thenReturn(photosThird)
            subject.searchPhotos(queryString)
            verify(mockPhotoDataRepository, never()).getPhotos(queryString, 4)

            assertTrue(subject.photoList.hasObservers())
            verify(observer).onChanged(Resource.success(photosFirst))
            Assert.assertNotNull(subject.photoList.value)

            subject.photoList.removeObserver(observer)
        }

    @Test
    fun testSearchPhotosPageIncrementsAndQueryChanges() = testCoroutineDispatcher.runBlockingTest {
        `when`(mockPhotoDataRepository.getPhotos(anyString(), anyInt())).thenReturn(photosFirst)
        val subject = GalleryViewModel(mockPhotoDataRepository, testCoroutineDispatcher)
        subject.photoList.observeForever(observer)

        subject.searchPhotos(queryString)
        verify(mockPhotoDataRepository, times(1)).getPhotos(queryString, 1)

        `when`(mockPhotoDataRepository.getPhotos(anyString(), anyInt())).thenReturn(photosSecond)
        subject.searchPhotos(queryString)
        verify(mockPhotoDataRepository, times(1)).getPhotos(queryString, 2)

        subject.searchPhotos(anotherQueryString)
        verify(mockPhotoDataRepository, times(1)).getPhotos(queryString, 1)

        subject.searchPhotos(queryString)
        verify(mockPhotoDataRepository, times(1)).getPhotos(queryString, 1)

        assertTrue(subject.photoList.hasObservers())
        verify(observer).onChanged(Resource.success(photosFirst))
        Assert.assertNotNull(subject.photoList.value)

        subject.photoList.removeObserver(observer)
    }

    @Test
    fun testListScrolled() = testCoroutineDispatcher.runBlockingTest {
        `when`(mockPhotoDataRepository.getPhotos(anyString(), anyInt())).thenReturn(photosFirst)
        val subject = GalleryViewModel(mockPhotoDataRepository, testCoroutineDispatcher)
        subject.photoList.observeForever(observer)

        subject.searchPhotos(queryString)
        verify(mockPhotoDataRepository, times(1)).getPhotos(queryString, 1)

        `when`(mockPhotoDataRepository.getPhotos(anyString(), anyInt())).thenReturn(photosSecond)
        subject.listScrolled(15, 85, 100)
        verify(mockPhotoDataRepository, times(1)).getPhotos(queryString, 2)

        //TODO: This can be improved as test should not depend on time
        Thread.sleep(1300)
        `when`(mockPhotoDataRepository.getPhotos(anyString(), anyInt())).thenReturn(photosThird)
        subject.listScrolled(15, 85, 100)
        verify(mockPhotoDataRepository, times(1)).getPhotos(queryString, 3)

        assertTrue(subject.photoList.hasObservers())
        verify(observer).onChanged(Resource.success(photosFirst))
        Assert.assertNotNull(subject.photoList.value)

        subject.photoList.removeObserver(observer)
    }

    @Test
    fun testListScrolledWithZeroTotalItemCount() = testCoroutineDispatcher.runBlockingTest {
        `when`(mockPhotoDataRepository.getPhotos(anyString(), anyInt())).thenReturn(photosFirst)
        val subject = GalleryViewModel(mockPhotoDataRepository, testCoroutineDispatcher)

        subject.listScrolled(15, 85, 0)
        verify(mockPhotoDataRepository, never()).getPhotos(queryString, 2)
    }

    @Test
    fun testListScrolledWithLessVisibleItemCountThanTotalItemCount() =
        testCoroutineDispatcher.runBlockingTest {
            val subject = GalleryViewModel(mockPhotoDataRepository, testCoroutineDispatcher)

            subject.listScrolled(15, 50, 100)
            verify(mockPhotoDataRepository, never()).getPhotos(queryString, 1)
        }

    companion object {
        const val queryString = "text"
        const val anotherQueryString = "anotherText"
    }
}
