package com.example.imagegallery

import java.io.File

/**
 * Class to read file from path in tests
 */
object FileReader {

    /**
     * Helper function which will load JSON from the path specified
     * @param path of JSON file
     * @return [String] JSON at given path
     */
    fun readStringFromFile(path: String): String {
        // Load the JSON response
        val uri = this.javaClass.classLoader.getResource(path)
        val file = File(uri.path)
        return String(file.readBytes())
    }
}
