// Tanggal Pengerjaan: 6 Agustus 2026
// NIM: 10121054
// Nama: Fayyad Azka Muhammad
// Kelas: IF12K

package com.fayyad.siskamlingapp.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fayyad.siskamlingapp.data.ReportModel
import com.fayyad.siskamlingapp.domain.AddReportUseCase
import com.fayyad.siskamlingapp.domain.IReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val addReportUseCase: AddReportUseCase,
    private val repository: IReportRepository // Inject antarmuka IReportRepository
) : ViewModel() {

    // Menyimpan status keberhasilan menggunakan Sealed Class UI State
    private val _insertState = MutableLiveData<UiState<String>>(UiState.Idle)
    val insertState: LiveData<UiState<String>> = _insertState

    // Fungsi Submit (Menggunakan Coroutines & UseCase)
    fun addReport(imageUri: Uri?, report: ReportModel) {
        _insertState.value = UiState.Loading

        viewModelScope.launch {
            val result = addReportUseCase(imageUri, report)

            result.onSuccess { message ->
                _insertState.value = UiState.Success(message)
            }.onFailure { exception ->
                _insertState.value = UiState.Error(exception.message ?: "Terjadi kesalahan")
            }
        }
    }

    // reset state saat dibutuhkan
    fun resetInsertState() {
        _insertState.value = UiState.Idle
    }

    // --- BAGIAN FUNGSI LAMA ---

    private val _reports = MutableLiveData<List<ReportModel>>()
    val reports: LiveData<List<ReportModel>> = _reports

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun fetchReports() {
        repository.getReports { list, error ->
            if (error != null) {
                _errorMessage.value = error ?: "Terjadi kesalahan"
            } else {
                _reports.value = list
            }
        }
    }

    private val _actionStatus = MutableLiveData<Pair<Boolean, String>>()
    val actionStatus: LiveData<Pair<Boolean, String>> = _actionStatus

    fun updateStatus(id: String, newStatus: String) {
        repository.updateVerifikasiStatus(id, newStatus) { isSuccess, message ->
            _actionStatus.value = Pair(isSuccess, message)
        }
    }

    fun deleteReport(id: String) {
        repository.deleteReport(id) { isSuccess, message ->
            _actionStatus.value = Pair(isSuccess, message)
        }
    }

    fun syncApiData(url: String) {
        repository.syncDataFromApi(url) { isSuccess, message ->
            _actionStatus.value = Pair(isSuccess, message)
        }
    }
}