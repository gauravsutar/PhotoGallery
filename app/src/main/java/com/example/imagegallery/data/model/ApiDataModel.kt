package com.example.imagegallery.data.model

/**
 * Data class to hold photos responses from search API calls.
 */
data class PhotoResponse(
    val photos: Photos,
    val stat: String
)

/**
 * Data class to hold information of list of photos and other information
 */
data class Photos(
    val page: Int,
    val pages: Int,
    val perpage: Int,
    val total: Int,
    val photo: List<Photo>
)

/**
 * Data class to hold information about single photo
 */
data class Photo(
    val id: Long,
    val owner: String,
    val secret: String,
    val server: Int,
    val farm: Int,
    val title: String,
    val ispublic: Int,
    val isfriend: Int,
    val isfamily: Int
) {
    fun getPhotoUrl(): String =
        "https://farm${farm}.static.flickr.com/${server}/${id}_${secret}.jpg"
}
