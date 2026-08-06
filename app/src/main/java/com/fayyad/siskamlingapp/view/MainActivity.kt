// Tanggal Pengerjaan: 2 Agustus 2026
// NIM: 10121054
// Nama: Fayyad Azka Muhammad
// Kelas: IF12K

package com.fayyad.siskamlingapp.view

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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Inisialisasi ViewModel melalui Hilt
    private val viewModel: ReportViewModel by viewModels()

    // Variabel untuk menyimpan lokasi file gambar yang dipilih dari HP
    private var selectedImageUri: Uri? = null

    // Peluncur Galeri Modern
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
        observeViewModel()

        // Aksi Tombol Pilih Foto
        binding.btnPilihFoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // Aksi Tombol Kirim
        binding.btnSubmit.setOnClickListener {
            submitData()
        }
        // Aksi Tombol Lihat Data
        binding.btnLihatData.setOnClickListener {
            val intent = android.content.Intent(this, ListReportActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupDropdownMenus() {
        // Setup Kategori Kejadian
        val kategoriList = arrayOf("Pencurian", "Keributan Warga", "Penerangan Mati", "Fasilitas Rusak", "Bencana Alam", "Lainnya")
        val kategoriAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, kategoriList)
        binding.spinKategori.setAdapter(kategoriAdapter)

        // Setup Jadwal Ronda
        val jadwalList = arrayOf("Senin Malam (Regu A)", "Selasa Malam (Regu B)", "Rabu Malam (Regu C)", "Kamis Malam (Regu D)", "Jumat Malam (Regu E)", "Sabtu Malam (Regu F)", "Minggu Malam (Regu G)")
        val jadwalAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, jadwalList)
        binding.spinJadwal.setAdapter(jadwalAdapter)
    }

    private fun submitData() {
        val titikRawan = binding.edtTitikRawan.text.toString().trim()
        val kategori = binding.spinKategori.text.toString().trim()
        val jadwal = binding.spinJadwal.text.toString().trim()

        // Validasi form kosong
        if (titikRawan.isEmpty() || kategori.isEmpty() || jadwal.isEmpty()) {
            Toast.makeText(this, "Mohon lengkapi titik rawan, kategori, dan jadwal ronda!", Toast.LENGTH_SHORT).show()
            return
        }

        // Ubah tampilan tombol saat proses upload ke Cloud
        binding.btnSubmit.isEnabled = false
        binding.btnSubmit.text = "Mengunggah & Mengirim..."

        // Bungkus data ke dalam Model
        val newReport = ReportModel(
            titikRawan = titikRawan,
            jadwalRonda = jadwal,
            kategoriKejadian = kategori,
            fotoKejadian = "", // Foto akan otomatis diisi URL-nya oleh Repository
            statusVerifikasiRw = "Belum Diverifikasi"
        )

        // Kirim gambar dan teks ke ViewModel
        viewModel.addReport(selectedImageUri, newReport)
    }

    private fun observeViewModel() {
        // Memantau status pengiriman dari ViewModel
        viewModel.insertStatus.observe(this) { (isSuccess, message) ->
            // Kembalikan tombol seperti semula
            binding.btnSubmit.isEnabled = true
            binding.btnSubmit.text = "Kirim Laporan"

            Toast.makeText(this, message, Toast.LENGTH_LONG).show()

            if (isSuccess) {
                // Bersihkan form teks setelah sukses
                binding.edtTitikRawan.text?.clear()
                binding.spinKategori.text.clear()
                binding.spinJadwal.text.clear()
                binding.spinKategori.clearFocus()
                binding.spinJadwal.clearFocus()

                // Hilangkan foto dari layar
                selectedImageUri = null
                binding.ivPreview.visibility = View.GONE
            }
        }
    }
}