// Tanggal Pengerjaan: 6 Agustus 2026
// NIM: 10121054
// Nama: Fayyad Azka Muhammad
// Kelas: IF12K

package com.fayyad.siskamlingapp.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fayyad.siskamlingapp.data.ReportModel
import com.fayyad.siskamlingapp.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val repository: ReportRepository
) : ViewModel() {

    // Menyimpan status keberhasilan dan pesan dari Firebase untuk dipantau oleh UI
    private val _insertStatus = MutableLiveData<Pair<Boolean, String>>()
    val insertStatus: LiveData<Pair<Boolean, String>> = _insertStatus

    // Fungsi yang akan dipanggil oleh tombol Submit di UI
    fun addReport(imageUri: Uri?, report: ReportModel) {
        repository.uploadPhotoAndInsertReport(imageUri, report) { isSuccess, message ->
            _insertStatus.value = Pair(isSuccess, message)
        }
    }

    // Menyimpan daftar laporan
    private val _reports = MutableLiveData<List<ReportModel>>()
    val reports: LiveData<List<ReportModel>> = _reports

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    // Fungsi mengambil data
    fun fetchReports() {
        repository.getReports { list, error ->
            if (error != null) {
                _errorMessage.value = error ?: "Terjadi kesalahan"
            } else {
                _reports.value = list
            }
        }
    }
    // Variabel untuk memantau status aksi Update/Delete
    private val _actionStatus = MutableLiveData<Pair<Boolean, String>>()
    val actionStatus: LiveData<Pair<Boolean, String>> = _actionStatus

    // Memanggil fungsi Update di Repository
    fun updateStatus(id: String, newStatus: String) {
        repository.updateVerifikasiStatus(id, newStatus) { isSuccess, message ->
            _actionStatus.value = Pair(isSuccess, message)
        }
    }

    // Memanggil fungsi Delete di Repository
    fun deleteReport(id: String) {
        repository.deleteReport(id) { isSuccess, message ->
            _actionStatus.value = Pair(isSuccess, message)
        }
    }
}