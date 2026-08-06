package com.fayyad.siskamlingapp.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.fayyad.siskamlingapp.databinding.ActivityHomeBinding
import com.fayyad.siskamlingapp.viewmodel.ReportViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint // Tambahkan ini agar ViewModel Hilt bisa disuntikkan
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val viewModel: ReportViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cardLapor.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.cardDaftar.setOnClickListener {
            startActivity(Intent(this, ListReportActivity::class.java))
        }

        // Aksi Tombol Sinkronisasi API
        binding.cardSyncApi.setOnClickListener {
            Toast.makeText(this, "Menyambungkan ke Server API...", Toast.LENGTH_SHORT).show()

            // GANTI TULISAN DI BAWAH DENGAN LINK MOCKY MILIK ANDA DARI LANGKAH 1
            val apiUrl = "https://gist.githubusercontent.com/Fayyadazka/279faa99d6bbafd20dd92edac85e026d/raw/597e0fb5beb10ff9e401befec5ab8ff4c2fe6be3/data_awal.json"

            viewModel.syncApiData(apiUrl)
        }

        // Pantau hasil tarikan data API
        viewModel.actionStatus.observe(this) { (isSuccess, message) ->
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }
}
    //val apiUrl = "https://gist.githubusercontent.com/Fayyadazka/279faa99d6bbafd20dd92edac85e026d/raw/597e0fb5beb10ff9e401befec5ab8ff4c2fe6be3/data_awal.json"