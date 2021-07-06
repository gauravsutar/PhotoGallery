package com.example.imagegallery.imageloading

import android.graphics.Bitmap
import androidx.collection.LruCache
import javax.inject.Inject

/**
 * Class manages in memory cache to store loaded images
 */
class MemoryLruCache @Inject constructor() : Cache {

    // set limit to 20% of memory
    private var lruCache: LruCache<String, Bitmap> =
        LruCache((Runtime.getRuntime().maxMemory() / 5).toInt())

    /**
     * Function to get bitmap from memory map cache by given key
     * @param id key
     */
    override fun get(id: String): Bitmap? {
        return lruCache.get(id)
    }

    /**
     * Function to put bitmap in memory map cache
     * @param id key
     * @param bitmap Bitmap
     */
    override fun put(id: String, bitmap: Bitmap) {
        lruCache.put(id, bitmap)
    }

    /**
     * Function clears all cache
     */
    override fun clear() {
        lruCache.evictAll()
    }
}
