package com.fayyad.siskamlingapp.domain

import android.net.Uri
import com.fayyad.siskamlingapp.data.ReportModel

interface IReportRepository {
    // Fungsi Baru dengan Coroutines
    suspend fun uploadPhotoAndInsertReport(imageUri: Uri?, report: ReportModel): Result<String>

    // Fungsi Lama dipertahankan agar tidak merusak halaman lain
    fun getReports(onResult: (List<ReportModel>, String?) -> Unit)
    fun updateVerifikasiStatus(id: String, newStatus: String, onResult: (Boolean, String) -> Unit)
    fun deleteReport(id: String, onResult: (Boolean, String) -> Unit)
    fun syncDataFromApi(apiUrl: String, onResult: (Boolean, String) -> Unit)
}