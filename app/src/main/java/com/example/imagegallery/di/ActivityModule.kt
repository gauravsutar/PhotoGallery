package com.example.imagegallery.di

import com.example.imagegallery.imageloading.ImageLoader
import com.example.imagegallery.presentation.GalleryImageAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@InstallIn(ActivityComponent::class)
@Module
object ActivityModule {

    @Provides
    fun provideGalleryImageAdapter(imageLoader: ImageLoader): GalleryImageAdapter =
        GalleryImageAdapter(imageLoader)
}
