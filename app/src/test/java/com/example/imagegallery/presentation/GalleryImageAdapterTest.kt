package com.example.imagegallery.presentation

import android.app.Application
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.imagegallery.FileReader
import com.example.imagegallery.data.model.PhotoResponse
import com.example.imagegallery.imageloading.ImageLoader
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment


@RunWith(RobolectricTestRunner::class)
class GalleryImageAdapterTest {

    @Mock
    lateinit var imageLoader: ImageLoader

    private lateinit var context: Application

    private lateinit var subject: GalleryImageAdapter

    @Before
    @Throws(Exception::class)
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        subject = GalleryImageAdapter(imageLoader)
        val application: Application = RuntimeEnvironment.application
        assertNotNull(application)
        context = application
    }

    @Test
    fun testOnCreateViewHolder() {
        val parent = LinearLayout(context)

        val viewHolder: ViewHolder = subject.onCreateViewHolder(parent, 0)
        assertTrue(viewHolder is GalleryImageAdapter.ItemViewHolder)

    }

    @Test
    fun testGetItemCount() {
        //initial state
        val initialCount: Int = subject.itemCount
        assertEquals(0, initialCount)
        val photoResponse = Gson().fromJson(
            FileReader.readStringFromFile("json/success.json"),
            PhotoResponse::class.java
        )
        subject.addPhotos(photoResponse.photos.photo)

        val currentCount: Int = subject.itemCount
        assertEquals(100, currentCount)
    }
}
