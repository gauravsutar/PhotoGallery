package com.example.imagegallery.imageloading

import android.graphics.Bitmap

/**
 * Bitmap cache interface
 */
interface Cache {
    fun get(id: String): Bitmap?
    fun put(id: String, bitmap: Bitmap)
    fun clear()
}
