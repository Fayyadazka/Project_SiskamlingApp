// Tanggal Pengerjaan: 2 Agustus 2026
// NIM: 10121054
// Nama: Fayyad Azka Muhammad
// Kelas: IF12K

package com.fayyad.siskamlingapp.view

import android.content.ContentValues
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.fayyad.siskamlingapp.data.ReportModel
import com.fayyad.siskamlingapp.databinding.ActivityListReportBinding
import com.fayyad.siskamlingapp.viewmodel.ReportViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class ListReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListReportBinding
    private val viewModel: ReportViewModel by viewModels()
    private lateinit var adapter: ReportAdapter
    private var originalReportList: List<ReportModel> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSearch()
        observeData()

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.fetchReports()
        }

        // Aksi Tombol Cetak PDF
        binding.btnCetakPdf.setOnClickListener {
            if (originalReportList.isNotEmpty()) {
                generatePdfReport(originalReportList)
            } else {
                Toast.makeText(this, "Tidak ada data untuk dicetak!", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.fetchReports()
    }

    private fun setupRecyclerView() {
        adapter = ReportAdapter(arrayListOf(),
            onVerifyClick = { report -> viewModel.updateStatus(report.id, "Telah Diverifikasi") },
            onDeleteClick = { report -> showDeleteDialog(report.id) }
        )
        binding.rvReports.layoutManager = LinearLayoutManager(this)
        binding.rvReports.adapter = adapter
    }

    private fun setupSearch() {
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val keyword = s.toString().trim()
                if (keyword.isEmpty()) {
                    adapter.updateData(originalReportList)
                } else {
                    val filteredList = originalReportList.filter { report ->
                        report.titikRawan.contains(keyword, ignoreCase = true) ||
                                report.kategoriKejadian.contains(keyword, ignoreCase = true)
                    }
                    adapter.updateData(filteredList)
                }
            }
        })
    }

    private fun showDeleteDialog(reportId: String) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Laporan")
            .setMessage("Apakah Anda yakin ingin menghapus data laporan ini?")
            .setPositiveButton("Hapus") { _, _ -> viewModel.deleteReport(reportId) }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun observeData() {
        viewModel.reports.observe(this) { list ->
            binding.swipeRefresh.isRefreshing = false
            originalReportList = list
            binding.edtSearch.text?.clear()
            adapter.updateData(list)

            if (list.isEmpty()) {
                binding.layoutEmptyState.visibility = View.VISIBLE
                binding.rvReports.visibility = View.GONE
            } else {
                binding.layoutEmptyState.visibility = View.GONE
                binding.rvReports.visibility = View.VISIBLE
            }
        }

        viewModel.errorMessage.observe(this) { error ->
            binding.swipeRefresh.isRefreshing = false
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }

        viewModel.actionStatus.observe(this) { (isSuccess, message) ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    // ==========================================
    // FUNGSI PAMUNGKAS: GENERATE & SAVE PDF
    // ==========================================
    private fun generatePdfReport(reportList: List<ReportModel>) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Ukuran A4 (Point)
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()

        // 1. Gambar Judul (Kop Laporan)
        paint.color = Color.rgb(183, 28, 28) // Warna Merah Gelap
        paint.textSize = 24f
        paint.isFakeBoldText = true
        canvas.drawText("REKAPITULASI LAPORAN SISKAMLING", 80f, 60f, paint)

        paint.color = Color.BLACK
        paint.textSize = 14f
        paint.isFakeBoldText = false
        val currentDate = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date())
        canvas.drawText("Dicetak pada: $currentDate", 80f, 90f, paint)

        // Garis Pembatas
        canvas.drawLine(80f, 110f, 515f, 110f, paint)

        // 2. Tulis Isi Data Laporan
        var yPosition = 140f
        paint.textSize = 12f

        for ((index, report) in reportList.withIndex()) {
            // Jika konten melebihi batas halaman bawah, potong saja (untuk simplifikasi)
            if (yPosition > 800f) break

            paint.isFakeBoldText = true
            canvas.drawText("${index + 1}. Titik Rawan: ${report.titikRawan}", 80f, yPosition, paint)
            yPosition += 20f

            paint.isFakeBoldText = false
            canvas.drawText("    Kategori: ${report.kategoriKejadian}", 80f, yPosition, paint)
            yPosition += 20f
            canvas.drawText("    Jadwal Ronda: ${report.jadwalRonda}", 80f, yPosition, paint)
            yPosition += 20f
            canvas.drawText("    Status: ${report.statusVerifikasiRw}", 80f, yPosition, paint)

            yPosition += 30f // Jarak antar laporan
        }

        pdfDocument.finishPage(page)

        // 3. Simpan ke Memori HP (Folder Downloads)
        savePdfToStorage(pdfDocument)
    }

    private fun savePdfToStorage(pdfDocument: PdfDocument) {
        val fileName = "Rekap_Siskamling_${System.currentTimeMillis()}.pdf"
        var outputStream: OutputStream? = null

        try {
            // Cara modern menyimpan file di Android 10+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                if (uri != null) {
                    outputStream = contentResolver.openOutputStream(uri)
                }
            } else {
                // Cara lama (Android 9 ke bawah)
                val targetPdf = java.io.File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
                outputStream = java.io.FileOutputStream(targetPdf)
            }

            outputStream?.let {
                pdfDocument.writeTo(it)
                Toast.makeText(this, "Berhasil! PDF disimpan di folder Download", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Gagal menyimpan PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            pdfDocument.close()
            outputStream?.close()
        }
    }
}