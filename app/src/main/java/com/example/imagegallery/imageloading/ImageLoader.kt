package com.example.imagegallery.imageloading

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import com.example.imagegallery.data.api.Method
import com.example.imagegallery.data.api.Request
import com.example.imagegallery.di.IoDispatcher
import com.example.imagegallery.di.MainDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Collections
import java.util.WeakHashMap
import javax.inject.Inject

/**
 * ImageLoader class is responsible for downloading the image from web and apply that to imageview.
 */
class ImageLoader @Inject constructor(
    private val cache: Cache,
    private val fileCache: FileCache,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
) {

    private val mapOfImageViewAndString: MutableMap<ImageView, String> =
        Collections.synchronizedMap(WeakHashMap())

    /**
     * Function loads image from given url and set to given imageView
     * @param url given image url to load
     * @param imageView given [ImageView] to load image into
     */
    fun loadImage(url: String, imageView: ImageView, placeHolder: Int? = null) {
        //Store image and url in image loader map
        mapOfImageViewAndString[imageView] = url

        //Check image is stored in MemoryCache Map
        val bitmap: Bitmap? = cache.get(url)

        bitmap?.let {
            imageView.setImageBitmap(it)
        } ?: run {
            placeHolder?.let { imageView.setImageResource(placeHolder) }
            downloadImage(url, imageView)
        }
    }

    /**
     * Function manages downloaded image and set to image view
     * @param url given image url
     * @param imageView given image view
     */
    private fun downloadImage(url: String, imageView: ImageView) {
        CoroutineScope(context = ioDispatcher).launch {
            try {
                if (isImageAvailable(url, imageView)) {
                    return@launch
                }
                yield()
                val bitmap: Bitmap? = getBitmap(Request(url, Method.GET))
                bitmap?.let {
                    cache.put(url, bitmap)
                }

                withContext(mainDispatcher) {
                    // Show bitmap on UI thread
                    bitmap?.let { imageView.setImageBitmap(it) }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    /**
     * Function to download the bitmap from given url
     * @param request given Request
     * @return [Bitmap] if successfully downloaded otherwise null.
     */
    private fun getBitmap(request: Request): Bitmap? {
        val url = request.url
        val file: File = fileCache.getFile(getFileNameFromUrlString(url))
        var bitmap: Bitmap? = decodeFileToBitmap(file)

        bitmap?.let {
            return it
        }

        var outputStream: OutputStream? = null
        var urlConnection: HttpURLConnection? = null
        var inputStream: InputStream? = null
        try {
            val imageUrl = URL(url)
            urlConnection = imageUrl.openConnection() as HttpURLConnection

            urlConnection = urlConnection.apply {
                connectTimeout = request.connectTimeout
                readTimeout = request.readTimeout
                requestMethod = request.method.toString()
            }

            inputStream = urlConnection.inputStream

            // Constructs a new FileOutputStream that writes to file
            // if file not exist then it will create file
            outputStream = FileOutputStream(file)

            val bufferSize = 1024
            val bytes = ByteArray(bufferSize)
            while (true) {
                //Read byte from input stream
                val count: Int = inputStream.read(bytes, 0, bufferSize)
                if (count == -1) break

                //Write byte from output stream
                outputStream.write(bytes, 0, count)
            }

            outputStream.close()
            urlConnection.disconnect()

            bitmap = decodeFileToBitmap(file)
        } catch (ex: Exception) {
            ex.printStackTrace()
            if (ex is OutOfMemoryError) {
                cache.clear()
            }
            bitmap = null
        } finally {
            inputStream?.close()
            outputStream?.close()
            urlConnection?.disconnect()
        }

        return bitmap
    }

    /**
     * Function to extract file name from url
     * @param url given url string
     * @return [String] file name
     */
    private fun getFileNameFromUrlString(url: String): String {
        return url.substring(url.lastIndexOf('/') + 1).split("\\?")[0].split("#")[0]
    }

    /**
     * Function decodes the file and convert file to bitmap
     * @param file file object to fetch image data
     * @return [Bitmap] if file exists otherwise null
     */
    private fun decodeFileToBitmap(file: File): Bitmap? {
        try {
            //TODO:[Improvement] bitmap compression logic can be added here to reduce the size
            return BitmapFactory.decodeFile(file.absolutePath)
        } catch (ex: FileNotFoundException) {
            ex.printStackTrace()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return null
    }

    /**
     * Function to check image is present in image loader map
     * @param url given url
     * @param imageView given imageview
     * @return [Boolean] true if url is present in image loader map otherwise false
     */
    private fun isImageAvailable(url: String, imageView: ImageView): Boolean {
        val tag: String? = mapOfImageViewAndString[imageView]
        //Check url is already exist in mapOfImageViewAndString
        return tag == null || tag != url
    }

    /**
     * Function to clear cache directory downloaded images and stored data in maps
     */
    fun clearCache() {
        cache.clear()
        fileCache.clear()
    }
}
