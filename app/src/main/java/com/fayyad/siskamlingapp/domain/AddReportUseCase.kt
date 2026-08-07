package com.fayyad.siskamlingapp.domain

import android.net.Uri
import com.fayyad.siskamlingapp.data.ReportModel
import javax.inject.Inject

class AddReportUseCase @Inject constructor(
    private val repository: IReportRepository
) {
    suspend operator fun invoke(imageUri: Uri?, report: ReportModel): Result<String> {
        return repository.uploadPhotoAndInsertReport(imageUri, report)
    }
}