package com.fayyad.siskamlingapp.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.fayyad.siskamlingapp.databinding.ActivityListReportBinding
import com.fayyad.siskamlingapp.viewmodel.ReportViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListReportBinding
    private val viewModel: ReportViewModel by viewModels()
    private lateinit var reportAdapter: ReportAdapter

    // Default Mode Warga (False)
    private var isRwMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Menerima status mode dari Intent
        isRwMode = intent.getBooleanExtra("EXTRA_IS_RW", false)

        // Ubah Judul Header sesuai Mode
        if (isRwMode) {
            binding.tvHeaderTitle?.text = "Panel Verifikasi RW"
            binding.btnSyncApi.visibility = View.VISIBLE
        } else {
            binding.tvHeaderTitle?.text = "Daftar Laporan Warga"
            binding.btnSyncApi.visibility = View.GONE // Warga tidak perlu tombol sync
        }

        setupRecyclerView()
        observeViewModel()

        viewModel.fetchReports()

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.fetchReports()
        }

        binding.btnSyncApi.setOnClickListener {
            val apiUrl = "https://gist.githubusercontent.com/Fayyadazka/279faa99d6bbafd20dd92edac85e026d/raw/28ebf3647654b9da23a646ada108e073f76cf5d4/data_awal.json"
            viewModel.syncApiData(apiUrl)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchReports()
    }

    private fun setupRecyclerView() {
        reportAdapter = ReportAdapter(
            isRwMode = isRwMode, // Kirim status role ke Adapter
            onUpdateStatus = { report, newStatus ->
                viewModel.updateStatus(report.id, newStatus)
            },
            onDelete = { report ->
                viewModel.deleteReport(report.id)
            }
        )

        binding.rvReports.apply {
            layoutManager = LinearLayoutManager(this@ListReportActivity)
            adapter = reportAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.reports.observe(this) { list ->
            binding.swipeRefresh.isRefreshing = false

            if (list.isNullOrEmpty()) {
                binding.layoutEmptyState.visibility = View.VISIBLE
                binding.rvReports.visibility = View.GONE
            } else {
                binding.layoutEmptyState.visibility = View.GONE
                binding.rvReports.visibility = View.VISIBLE
                reportAdapter.submitList(list)
            }
        }

        viewModel.errorMessage.observe(this) { error ->
            binding.swipeRefresh.isRefreshing = false
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }

        viewModel.actionStatus.observe(this) { status ->
            val (isSuccess, message) = status
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            if (isSuccess) {
                viewModel.fetchReports()
            }
        }
    }
}