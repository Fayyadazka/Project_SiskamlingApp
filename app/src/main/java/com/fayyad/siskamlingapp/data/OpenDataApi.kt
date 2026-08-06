package com.fayyad.siskamlingapp.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface OpenDataApi {
    // Fungsi untuk menarik data list dari URL acak (Mocky)
    @GET
    fun getInitialData(@Url url: String): Call<List<ReportModel>>
}