package com.fayyad.siskamlingapp.di

import com.fayyad.siskamlingapp.domain.IReportRepository
import com.fayyad.siskamlingapp.repository.ReportRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindReportRepository(
        reportRepository: ReportRepository
    ): IReportRepository
}