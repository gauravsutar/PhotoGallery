package com.example.imagegallery.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagegallery.data.model.Photos
import com.example.imagegallery.data.model.Resource
import com.example.imagegallery.data.repository.PhotoDataRepository
import com.example.imagegallery.di.MainDispatcher
import com.example.imagegallery.presentation.GalleryActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

/**
 * ViewModel for the [GalleryActivity] screen.
 * The ViewModel works with the [PhotoDataRepository] to get the data.
 */
@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val photoDataRepository: PhotoDataRepository,
    @MainDispatcher private val defaultDispatcher: CoroutineDispatcher
) :
    ViewModel() {

    private var currentQueryValue: String? = null
    private var currentPage: Int = INITIAL_PAGE
    private var pages: Int? = null
    private var lastScrollTime: Long = 0

    private var _photoList = MutableLiveData<Resource<Photos?>>()
    val photoList: LiveData<Resource<Photos?>> = _photoList

    /**
     * Function to call search photos api by query and post it to UI
     * @param queryString text to search
     */
    fun searchPhotos(queryString: String, page: Int = currentPage) {
        viewModelScope.launch(defaultDispatcher) {
            try {
                // Checks if new query then set initial page count
                if (currentQueryValue != queryString) {
                    currentPage = INITIAL_PAGE
                }
                // Checks if current page is last page
                pages?.let {
                    if (it in 1 until currentPage) {
                        return@launch
                    }
                }
                currentQueryValue = queryString

                val photos = photoDataRepository.getPhotos(queryString, page)
                _photoList.value = Resource.success(photos)

                pages = photos.pages
                currentPage = photos.page + 1
            } catch (ex: Exception) {
                ex.printStackTrace()
                _photoList.value =
                    Resource.error("Error while loading photos.\n\nPlease try again!", null)
            }
        }
    }

    /**
     * Function to call search photos api with existing query and on scroll event
     */
    fun listScrolled(visibleItemCount: Int, lastVisibleItemPosition: Int, totalItemCount: Int) {
        if (totalItemCount != 0 && visibleItemCount + lastVisibleItemPosition + VISIBLE_THRESHOLD >= totalItemCount) {
            //TODO:[Improvement] this logic can be improved by using paging lib
            //Reduces multiple calls while scrolling
            if (System.currentTimeMillis() - lastScrollTime < 1200L) {
                return
            } else {
                val immutableQuery = currentQueryValue
                if (immutableQuery != null) {
                    searchPhotos(immutableQuery)
                }
            }
            lastScrollTime = System.currentTimeMillis()
        }
    }

    companion object {
        private const val VISIBLE_THRESHOLD = 5
        private const val INITIAL_PAGE = 1
    }
}
