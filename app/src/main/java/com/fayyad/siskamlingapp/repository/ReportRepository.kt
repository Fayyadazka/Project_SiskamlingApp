package com.fayyad.siskamlingapp.repository

import android.content.Context
import android.net.Uri
import com.fayyad.siskamlingapp.data.ImgbbApi
import com.fayyad.siskamlingapp.data.ImgbbResponse
import com.fayyad.siskamlingapp.data.OpenDataApi
import com.fayyad.siskamlingapp.data.ReportModel
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class ReportRepository @Inject constructor(
    private val dbReference: DatabaseReference,
    private val imgbbApi: ImgbbApi,
    @ApplicationContext private val context: Context
) {
    fun uploadPhotoAndInsertReport(imageUri: Uri?, report: ReportModel, onResult: (Boolean, String) -> Unit) {
        if (imageUri != null) {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()

            if (bytes == null) {
                onResult(false, "Gagal membaca file gambar dari galeri.")
                return
            }

            val mediaType = MediaType.parse("image/*")
            val requestFile = RequestBody.create(mediaType, bytes)
            val body = MultipartBody.Part.createFormData("image", "kejadian.jpg", requestFile)

            val apiKey = "9e5fdf46d8fd9c8e70392191f6de1e14"

            imgbbApi.uploadImage(apiKey, body).enqueue(object : Callback<ImgbbResponse> {
                override fun onResponse(call: Call<ImgbbResponse>, response: Response<ImgbbResponse>) {
                    if (response.isSuccessful && response.body()?.data?.url != null) {
                        val imageUrl = response.body()!!.data!!.url!!
                        val reportWithImage = report.copy(fotoKejadian = imageUrl)

                        insertReportToDatabase(reportWithImage, onResult)
                    } else {
                        onResult(false, "Gagal mendapatkan respons dari server gambar.")
                    }
                }

                override fun onFailure(call: Call<ImgbbResponse>, t: Throwable) {
                    onResult(false, "Koneksi ke server gambar terputus: ${t.message}")
                }
            })
        } else {
            insertReportToDatabase(report, onResult)
        }
    }

    private fun insertReportToDatabase(report: ReportModel, onResult: (Boolean, String) -> Unit) {
        val newRef = dbReference.push()
        val reportWithId = report.copy(id = newRef.key ?: "")

        newRef.setValue(reportWithId)
            .addOnSuccessListener { onResult(true, "Laporan Kamtibmas berhasil dikirim!") }
            .addOnFailureListener { exception -> onResult(false, exception.message ?: "Terjadi kesalahan sistem.") }
    }
    // Fungsi untuk menarik data dari Realtime Database secara Real-time
    fun getReports(onResult: (List<ReportModel>, String?) -> Unit) {
        // Menggunakan addValueEventListener agar setiap ada laporan baru, UI otomatis ter-update
        dbReference.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val reportList = mutableListOf<ReportModel>()
                for (data in snapshot.children) {
                    val report = data.getValue(ReportModel::class.java)
                    if (report != null) {
                        reportList.add(report)
                    }
                }
                // Membalikkan urutan agar laporan terbaru ada di paling atas
                reportList.reverse()
                onResult(reportList, null)
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                onResult(emptyList(), error.message)
            }
        })
    }
    // Fungsi UPDATE: Mengubah Status Verifikasi
    fun updateVerifikasiStatus(id: String, newStatus: String, onResult: (Boolean, String) -> Unit) {
        dbReference.child(id).child("statusVerifikasiRw").setValue(newStatus)
            .addOnSuccessListener { onResult(true, "Status berhasil diupdate!") }
            .addOnFailureListener { onResult(false, it.message ?: "Gagal mengupdate status") }
    }

    // Fungsi DELETE: Menghapus Laporan
    fun deleteReport(id: String, onResult: (Boolean, String) -> Unit) {
        dbReference.child(id).removeValue()
            .addOnSuccessListener { onResult(true, "Laporan berhasil dihapus!") }
            .addOnFailureListener { onResult(false, it.message ?: "Gagal menghapus laporan") }
    }
    // Menarik Data dari API Eksternal lalu Push ke Firebase
    fun syncDataFromApi(apiUrl: String, onResult: (Boolean, String) -> Unit) {
        val retrofit = retrofit2.Retrofit.Builder()
            .baseUrl("https://run.mocky.io/")
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()


        val api = retrofit.create(OpenDataApi::class.java)

        api.getInitialData(apiUrl).enqueue(object : retrofit2.Callback<List<ReportModel>> {
            override fun onResponse(call: retrofit2.Call<List<ReportModel>>, response: retrofit2.Response<List<ReportModel>>) {
                if (response.isSuccessful && response.body() != null) {
                    val listData = response.body()!!

                    // Looping data dari API dan masukkan ke Firebase
                    for (report in listData) {
                        val newId = dbReference.push().key ?: continue
                        report.id = newId // Berikan ID unik Firebase
                        dbReference.child(newId).setValue(report)
                    }
                    onResult(true, "Berhasil! ${listData.size} Data Awal API disinkronisasi ke Firebase.")
                } else {
                    onResult(false, "Gagal mengambil data dari Server API.")
                }
            }

            override fun onFailure(call: retrofit2.Call<List<ReportModel>>, t: Throwable) {
                onResult(false, "Error Jaringan: ${t.message}")
            }
        })
    }

}