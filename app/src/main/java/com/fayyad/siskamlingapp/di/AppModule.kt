package com.fayyad.siskamlingapp.di

import com.fayyad.siskamlingapp.data.ImgbbApi
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): DatabaseReference {
        return FirebaseDatabase.getInstance().getReference("laporan_kamtibmas")
    }

    // Mesin baru pengunggah foto menggunakan Retrofit
    @Provides
    @Singleton
    fun provideImgbbApi(): ImgbbApi {
        return Retrofit.Builder()
            .baseUrl("https://api.imgbb.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ImgbbApi::class.java)
    }
}