package com.example.imagegallery.imageloading

import java.io.File
import javax.inject.Inject

/**
 * Class to get files from cache directory
 */
class FileCache @Inject constructor(
    private val cacheDirectory: File
) {

    init {
        if (!cacheDirectory.exists()) {
            cacheDirectory.mkdirs()
        }
    }

    fun getFile(fileName: String): File {
        return File(cacheDirectory, fileName)
    }

    fun clear() {
        val files: Array<File> = cacheDirectory.listFiles() ?: return
        //delete all files from cache directory files
        for (f in files) {
            f.delete()
        }
    }
}
