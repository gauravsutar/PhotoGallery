package com.example.imagegallery.di

import com.example.imagegallery.data.repository.PhotoDataRepository
import com.example.imagegallery.data.repository.PhotoDataRepositoryImpl
import com.example.imagegallery.data.datasource.PhotoDataSource
import com.example.imagegallery.data.datasource.RemotePhotoDataSource
import com.example.imagegallery.imageloading.Cache
import com.example.imagegallery.imageloading.MemoryLruCache
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class ApplicationAbstractModule {

    @Binds
    @Singleton
    abstract fun provideGithubRepository(repository: PhotoDataRepositoryImpl): PhotoDataRepository

    @Binds
    @Singleton
    abstract fun providePhotoDataSource(dataSource: RemotePhotoDataSource): PhotoDataSource

    @Binds
    @Singleton
    abstract fun provideCache(cache: MemoryLruCache): Cache
}
