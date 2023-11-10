package com.ibrahimaluc.busbookingapp.di

import com.ibrahimaluc.busbookingapp.data.MapService
import com.ibrahimaluc.busbookingapp.data.repository.MapRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideProductRepository(
        mapService: MapService,
    ): MapRepository = MapRepository(mapService)
}