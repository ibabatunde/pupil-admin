package com.okediran.administrator.di

import com.okediran.administrator.data.repositories.IPupilRepository
import com.okediran.administrator.data.repositories.PupilRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    abstract fun bindPupilRepository(impl: PupilRepository): IPupilRepository

}