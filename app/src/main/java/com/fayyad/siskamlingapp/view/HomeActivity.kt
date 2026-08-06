package com.fayyad.siskamlingapp.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fayyad.siskamlingapp.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Aksi jika Menu "Lapor Kejadian" ditekan (Membuka Form MainActivity)
        binding.cardLapor.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Aksi jika Menu "Daftar Laporan" ditekan (Membuka ListReportActivity)
        binding.cardDaftar.setOnClickListener {
            val intent = Intent(this, ListReportActivity::class.java)
            startActivity(intent)
        }
    }
}