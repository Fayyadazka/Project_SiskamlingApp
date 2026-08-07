package com.fayyad.siskamlingapp.view

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.fayyad.siskamlingapp.databinding.ActivityHomeBinding
import com.fayyad.siskamlingapp.databinding.DialogPinRwBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Tombol Lapor Kejadian (Untuk Warga)
        binding.btnLaporKejadian.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // 2. Tombol Lihat Laporan (Mode Warga - Hanya Lihat)
        binding.btnLihatLaporan.setOnClickListener {
            val intent = Intent(this, ListReportActivity::class.java)
            intent.putExtra("EXTRA_IS_RW", false) // Mode Warga Biasa
            startActivity(intent)
        }

        // 3. Tombol Khusus Pengurus RW (Pop-up PIN Modern)
        binding.btnVerifikasiRw.setOnClickListener {
            showModernPinDialog()
        }
    }

    private fun showModernPinDialog() {
        // Inflate custom layout dialog_pin_rw.xml
        val dialogBinding = DialogPinRwBinding.inflate(LayoutInflater.from(this))

        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogBinding.root)
            .setCancelable(true)
            .create()

        // Mengatur latar belakang transparan agar sudut rounded cardview terlihat sempurna
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Aksi Tombol Batal
        dialogBinding.btnBatal.setOnClickListener {
            dialog.dismiss()
        }

        // Aksi Tombol Masuk
        dialogBinding.btnMasuk.setOnClickListener {
            val pinInput = dialogBinding.edtPin.text.toString().trim()

            if (pinInput.isEmpty()) {
                dialogBinding.tlPin.error = "PIN tidak boleh kosong!"
                return@setOnClickListener
            }

            if (pinInput == "1234") { // PIN Default RW
                dialogBinding.tlPin.error = null
                dialog.dismiss()

                Toast.makeText(this, "Akses Pengurus RW Diterima!", Toast.LENGTH_SHORT).show()

                // Pindah ke ListReportActivity dengan Mode RW
                val intent = Intent(this, ListReportActivity::class.java)
                intent.putExtra("EXTRA_IS_RW", true)
                startActivity(intent)
            } else {
                // PIN Salah
                dialogBinding.tlPin.error = "PIN Salah! Akses ditolak."
                dialogBinding.edtPin.setText("")
            }
        }

        dialog.show()
    }
}