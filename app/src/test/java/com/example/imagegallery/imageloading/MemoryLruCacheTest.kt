package com.example.imagegallery.imageloading

import android.graphics.Bitmap
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MemoryLruCacheTest {

    private val subject = MemoryLruCache()

    @Test
    fun testMemoryCache() {
        val createdBitmapFirst = Bitmap.createBitmap(10, 10, Bitmap.Config.ALPHA_8)
        val createdBitmapSecond = Bitmap.createBitmap(20, 20, Bitmap.Config.ALPHA_8)
        subject.put("1", createdBitmapFirst)
        subject.put("2", createdBitmapSecond)

        val receivedBitmapFirst = subject.get("1")
        assertNotNull(receivedBitmapFirst)
        assertEquals(receivedBitmapFirst, createdBitmapFirst)

        val receivedBitmapSecond = subject.get("2")
        assertNotNull(receivedBitmapSecond)
        assertEquals(receivedBitmapSecond, createdBitmapSecond)

        subject.clear()
        assertNull(subject.get("1"))
        assertNull(subject.get("2"))
    }
}
