package com.fayyad.siskamlingapp.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.fayyad.siskamlingapp.data.ImgbbApi
import com.fayyad.siskamlingapp.data.OpenDataApi
import com.fayyad.siskamlingapp.data.ReportModel
import com.fayyad.siskamlingapp.domain.IReportRepository
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ReportRepository @Inject constructor(
    private val dbReference: DatabaseReference,
    private val imgbbApi: ImgbbApi,
    @param:ApplicationContext private val context: Context
) : IReportRepository {

    override suspend fun uploadPhotoAndInsertReport(imageUri: Uri?, report: ReportModel): Result<String> {
        return try {

            // 1. UPLOAD GAMBAR TERLEBIH DAHULU (JIKA ADA FOTO)
            if (imageUri != null) {
                Log.d("UPLOAD_DEBUG", "Mulai mengunggah gambar ke ImgBB...")

                val imageUrl = withContext(Dispatchers.IO) {
                    val inputStream = context.contentResolver.openInputStream(imageUri)
                    val bytes = inputStream?.readBytes()
                    inputStream?.close()

                    if (bytes == null) throw Exception("Gagal membaca file gambar dari galeri HP.")

                    // Siapkan file gambar
                    val mediaType = okhttp3.MediaType.parse("image/*")
                    val requestFile = okhttp3.RequestBody.create(mediaType, bytes)
                    val body = okhttp3.MultipartBody.Part.createFormData("image", "kejadian.jpg", requestFile)

                    // Hardcode API Key sementara agar terjamin 100% terbaca
                    val apiKey = "9e5fdf46d8fd9c8e70392191f6de1e14"

                    // Proses Upload ke Server
                    val response = imgbbApi.uploadImage(apiKey, body)

                    if (response.isSuccessful && response.body()?.data != null) {
                        val imgData = response.body()!!.data!!
                        // Prioritaskan displayUrl (link gambar langsung), jika kosong baru pakai url biasa
                        val urlSukses = imgData.displayUrl ?: imgData.url ?: ""
                        Log.d("UPLOAD_DEBUG", "Upload Berhasil! Link Gambar: $urlSukses")
                        urlSukses // Kembalikan URL
                    } else {
                        val pesanError = response.errorBody()?.string() ?: response.message()
                        Log.e("UPLOAD_DEBUG", "ImgBB Menolak: $pesanError")
                        throw Exception("Gagal Upload: API ImgBB menolak gambar ini.")
                    }
                }

                // KUNCI PERBAIKAN: Masukkan URL langsung ke variabel fotoKejadian tanpa .copy()
                report.fotoKejadian = imageUrl

            } else {
                Log.d("UPLOAD_DEBUG", "Tidak ada gambar yang dipilih. Mengirim teks saja.")
            }

            // 2. SIMPAN KE FIREBASE
            Log.d("UPLOAD_DEBUG", "Menyimpan seluruh data ke Firebase...")
            val newRef = dbReference.push()
            report.id = newRef.key ?: ""

            // Proses Simpan Instan (Tanpa await)
            newRef.setValue(report)
            Log.d("UPLOAD_DEBUG", "Selesai menyimpan ke Firebase!")

            Result.success("Laporan Kamtibmas berhasil dikirim!")

        } catch (e: java.net.SocketTimeoutException) {
            Log.e("UPLOAD_DEBUG", "Waktu Habis/Timeout!")
            Result.failure(Exception("Waktu habis! Ukuran foto terlalu besar atau koneksi lambat."))
        } catch (e: Exception) {
            Log.e("UPLOAD_DEBUG", "Error Fatal: ${e.message}")
            Result.failure(Exception("Gagal: ${e.message}"))
        }
    }

    override fun getReports(onResult: (List<ReportModel>, String?) -> Unit) {
        dbReference.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val reportList = mutableListOf<ReportModel>()
                for (data in snapshot.children) {
                    val report = data.getValue(ReportModel::class.java)
                    if (report != null) {
                        reportList.add(report)
                    }
                }
                reportList.reverse()
                onResult(reportList, null)
            }
            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                onResult(emptyList(), error.message)
            }
        })
    }

    override fun updateVerifikasiStatus(id: String, newStatus: String, onResult: (Boolean, String) -> Unit) {
        dbReference.child(id).child("statusVerifikasiRw").setValue(newStatus)
            .addOnSuccessListener { onResult(true, "Status berhasil diupdate!") }
            .addOnFailureListener { onResult(false, it.message ?: "Gagal mengupdate status") }
    }

    override fun deleteReport(id: String, onResult: (Boolean, String) -> Unit) {
        dbReference.child(id).removeValue()
            .addOnSuccessListener { onResult(true, "Laporan berhasil dihapus!") }
            .addOnFailureListener { onResult(false, it.message ?: "Gagal menghapus laporan") }
    }

    override fun syncDataFromApi(apiUrl: String, onResult: (Boolean, String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val retrofit = retrofit2.Retrofit.Builder()
                    .baseUrl("https://gist.githubusercontent.com/")
                    .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                    .build()
                val api = retrofit.create(OpenDataApi::class.java)

                val response = api.getInitialData(apiUrl)
                if (response.isSuccessful && response.body() != null) {
                    val listData = response.body()!!
                    for (report in listData) {
                        val newId = dbReference.push().key ?: continue
                        report.id = newId
                        dbReference.child(newId).setValue(report)
                    }
                    withContext(Dispatchers.Main) {
                        onResult(true, "Berhasil! ${listData.size} Data tersinkronisasi.")
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onResult(false, "Gagal mengambil data dari Server API.")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResult(false, "Error Jaringan: ${e.message}")
                }
            }
        }
    }
}