// Tanggal Pengerjaan: 2 Agustus 2026
// NIM: 10121054
// Nama: Fayyad Azka Muhammad
// Kelas: IF12K

package com.fayyad.siskamlingapp.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.fayyad.siskamlingapp.data.ReportModel
import com.fayyad.siskamlingapp.databinding.ActivityMainBinding
import com.fayyad.siskamlingapp.viewmodel.ReportViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: ReportViewModel by viewModels()
    private var selectedImageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            binding.ivPreview.setImageURI(uri)
            binding.ivPreview.visibility = View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDropdownMenus()
        setupDatePicker() // Memanggil fungsi kalender
        observeViewModel()

        binding.btnPilihFoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnSubmit.setOnClickListener {
            submitData()
        }

        binding.btnLihatData.setOnClickListener {
            val intent = Intent(this, ListReportActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupDropdownMenus() {
        val kategoriList = arrayOf("Pencurian", "Keributan Warga", "Penerangan Mati", "Fasilitas Rusak", "Bencana Alam", "Lainnya")
        val kategoriAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, kategoriList)
        binding.spinKategori.setAdapter(kategoriAdapter)
    }

    // Fungsi Baru: Menampilkan Kalender Material Design
    private fun setupDatePicker() {
        binding.edtTanggalRonda.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pilih Tanggal Ronda")
                .build()

            datePicker.addOnPositiveButtonClickListener { selection ->
                // Mengubah format angka menjadi tanggal (Contoh: 06 Agustus 2026)
                val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
                val dateString = sdf.format(Date(selection))
                binding.edtTanggalRonda.setText(dateString)
            }

            datePicker.show(supportFragmentManager, "MATERIAL_DATE_PICKER")
        }
    }

    private fun submitData() {
        val titikRawan = binding.edtTitikRawan.text.toString().trim()
        val kategori = binding.spinKategori.text.toString().trim()
        val tanggal = binding.edtTanggalRonda.text.toString().trim()

        if (titikRawan.isEmpty() || kategori.isEmpty() || tanggal.isEmpty()) {
            Toast.makeText(this, "Mohon lengkapi titik rawan, kategori, dan tanggal ronda!", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnSubmit.isEnabled = false
        binding.btnSubmit.text = "Mengunggah & Mengirim..."

        val newReport = ReportModel(
            titikRawan = titikRawan,
            jadwalRonda = tanggal, // Menyimpan format tanggal kalender
            kategoriKejadian = kategori,
            fotoKejadian = "",
            statusVerifikasiRw = "Belum Diverifikasi"
        )

        viewModel.addReport(selectedImageUri, newReport)
    }

    private fun observeViewModel() {
        viewModel.insertStatus.observe(this) { (isSuccess, message) ->
            binding.btnSubmit.isEnabled = true
            binding.btnSubmit.text = "Kirim Laporan"
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()

            if (isSuccess) {
                binding.edtTitikRawan.text?.clear()
                binding.spinKategori.text.clear()
                binding.edtTanggalRonda.text?.clear() // Bersihkan kalender
                binding.spinKategori.clearFocus()

                selectedImageUri = null
                binding.ivPreview.visibility = View.GONE
            }
        }
    }
}