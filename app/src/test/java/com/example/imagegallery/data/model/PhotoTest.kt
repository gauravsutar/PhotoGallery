package com.example.imagegallery.data.model

import org.junit.Assert.assertEquals
import org.junit.Test

class PhotoTest {

    @Test
    fun testGetPhotoUrl() {
        val subject = Photo(12345, "owner", "secret", 8080, 0, "Test Title", 1, 1, 1)
        assertEquals("https://farm0.static.flickr.com/8080/12345_secret.jpg", subject.getPhotoUrl())
    }
}
