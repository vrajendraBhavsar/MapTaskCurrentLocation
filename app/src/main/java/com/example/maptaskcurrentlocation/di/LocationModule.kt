package com.example.maptaskcurrentlocation.di

import com.example.maptaskcurrentlocation.CurrentLocation
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object LocationModule {
    @Provides
    fun providesCurrentLocation(): CurrentLocation {
        return CurrentLocation()
    }
}