package com.fayyad.siskamlingapp.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface OpenDataApi {
    @GET
    suspend fun getInitialData(@Url url: String): Response<List<ReportModel>>
}