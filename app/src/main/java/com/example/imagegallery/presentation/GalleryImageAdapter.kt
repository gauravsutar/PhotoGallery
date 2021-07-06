package com.example.imagegallery.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.imagegallery.R
import com.example.imagegallery.data.model.Photo
import com.example.imagegallery.imageloading.ImageLoader

/**
 * Adapter for the list of photos.
 */
class GalleryImageAdapter(private val imageLoader: ImageLoader) :
    RecyclerView.Adapter<GalleryImageAdapter.ItemViewHolder>() {

    private val imageList = ArrayList<Photo>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_image, parent, false)
        return ItemViewHolder(view, imageLoader)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = imageList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = imageList.size

    fun addPhotos(list: List<Photo>) {
        val currentCount = itemCount
        imageList.addAll(list)
        notifyItemRangeInserted(currentCount + 1, list.size)
    }

    fun clearList() {
        imageList.clear()
        notifyDataSetChanged()
    }

    class ItemViewHolder(view: View, private val imageLoader: ImageLoader) :
        RecyclerView.ViewHolder(view) {

        private val imgPhoto = view.findViewById<ImageView>(R.id.img_photo)!!
        private val txtTitle = view.findViewById<TextView>(R.id.txt_title)!!

        fun bind(item: Photo) {
            txtTitle.text = item.title.trim()
            imageLoader.loadImage(item.getPhotoUrl(), imgPhoto, R.drawable.ic_placeholder)
        }
    }
}
