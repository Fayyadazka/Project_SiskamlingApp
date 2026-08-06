package com.fayyad.siskamlingapp.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.fayyad.siskamlingapp.data.ReportModel
import com.fayyad.siskamlingapp.databinding.ActivityListReportBinding
import com.fayyad.siskamlingapp.viewmodel.ReportViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListReportBinding
    private val viewModel: ReportViewModel by viewModels()
    private lateinit var adapter: ReportAdapter

    // Variabel untuk menyimpan data asli sebelum di-filter
    private var originalReportList: List<ReportModel> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSearch()
        observeData()

        viewModel.fetchReports()
    }

    private fun setupRecyclerView() {
        adapter = ReportAdapter(arrayListOf(),
            onVerifyClick = { report ->
                viewModel.updateStatus(report.id, "Telah Diverifikasi")
            },
            onDeleteClick = { report ->
                showDeleteDialog(report.id)
            }
        )
        binding.rvReports.layoutManager = LinearLayoutManager(this)
        binding.rvReports.adapter = adapter
    }

    private fun setupSearch() {
        // Mendeteksi setiap ketikan huruf di kolom pencarian
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val keyword = s.toString().trim()
                if (keyword.isEmpty()) {
                    // Jika kolom pencarian kosong, tampilkan semua data asli
                    adapter.updateData(originalReportList)
                } else {
                    // Filter data berdasarkan Titik Rawan atau Kategori
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
            .setPositiveButton("Hapus") { _, _ ->
                viewModel.deleteReport(reportId)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun observeData() {
        viewModel.reports.observe(this) { list ->
            // Simpan data dari Firebase ke variabel original
            originalReportList = list
            // Kosongkan kolom pencarian setiap kali ada data baru
            binding.edtSearch.text?.clear()
            // Tampilkan datanya ke layar
            adapter.updateData(list)
        }

        viewModel.errorMessage.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }

        viewModel.actionStatus.observe(this) { (isSuccess, message) ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}